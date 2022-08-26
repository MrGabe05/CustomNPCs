package noppes.npcs.packets.server;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkHooks;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerTransportData;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiOpen;
import noppes.npcs.packets.client.PacketGuiScrollList;
import noppes.npcs.packets.client.PacketNpcRole;
import noppes.npcs.roles.RoleTransporter;
import noppes.npcs.util.CustomNPCsScheduler;

import java.util.ArrayList;
import java.util.Vector;

public class SPacketGuiOpen extends PacketServerBasic
{
    private EnumGuiType type;

    private BlockPos pos;

    public SPacketGuiOpen(EnumGuiType type, BlockPos pos) {
        this.type = type;
        this.pos = pos;
    }

    public static void encode(SPacketGuiOpen msg, PacketBuffer buf) {
        buf.writeEnum(msg.type);
        buf.writeBlockPos(msg.pos);
    }

    public static SPacketGuiOpen decode(PacketBuffer buf) {
        return new SPacketGuiOpen(buf.readEnum(EnumGuiType.class), buf.readBlockPos());
    }

    @Override
    protected void handle() {
        sendOpenGui(player, type, npc, pos);
    }

    public static void sendOpenGui(final PlayerEntity player, final EnumGuiType gui, final EntityNPCInterface npc, final BlockPos pos) {
        if(!(player instanceof ServerPlayerEntity))
            return;

        NoppesUtilServer.setEditingNpc(player, npc);

        if(gui == EnumGuiType.PlayerFollower || gui == EnumGuiType.PlayerFollowerHire || gui == EnumGuiType.PlayerTrader || gui == EnumGuiType.PlayerTransporter){
            if(npc.role.getType() == RoleType.NONE)
                return;
            CompoundNBT comp = new CompoundNBT();
            npc.role.save(comp);
            comp.putInt("Role", npc.role.getType());
            Packets.send((ServerPlayerEntity)player, new PacketNpcRole(npc.getId(), comp));
        }
        CustomNPCsScheduler.runTack(() -> {
            player.getServer().submit(() -> {
                if(!gui.hasContainer) {
                    Packets.send((ServerPlayerEntity)player, new PacketGuiOpen(gui, pos));
                }
                else{
                    NoppesUtilServer.openContainerGui((ServerPlayerEntity)player, gui, (buffer) -> buffer.writeInt(npc.getId()));
                }
                ArrayList<String> list = getScrollData(player, gui, npc);
                if (list == null || list.isEmpty())
                    return;
                Packets.send((ServerPlayerEntity) player, new PacketGuiScrollList(new Vector<>(list)));
            });
        }, 200);
    }

    private static ArrayList<String> getScrollData(PlayerEntity player, EnumGuiType gui, EntityNPCInterface npc) {
        if(gui == EnumGuiType.PlayerTransporter){
            RoleTransporter role = (RoleTransporter) npc.role;
            ArrayList<String> list = new ArrayList<String>();
            TransportLocation location = role.getLocation();
            String name = role.getLocation().name;
            for(TransportLocation loc: location.category.getDefaultLocations()){
                if(!list.contains(loc.name)){
                    list.add(loc.name);
                }
            }
            PlayerTransportData playerdata = PlayerData.get(player).transportData;
            for(int i : playerdata.transports){
                TransportLocation loc = TransportController.getInstance().getTransport(i);
                if(loc != null && location.category.locations.containsKey(loc.id)){
                    if(!list.contains(loc.name)){
                        list.add(loc.name);
                    }
                }
            }
            list.remove(name);
            return list;
        }
        return null;
    }
}