package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketGuiScrollSelected;
import noppes.npcs.roles.RoleTransporter;

public class SPacketNpcTransportGet extends PacketServerBasic {

    public SPacketNpcTransportGet() {
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketNpcTransportGet msg, PacketBuffer buf) {
    }

    public static SPacketNpcTransportGet decode(PacketBuffer buf) {
        return new SPacketNpcTransportGet();
    }

    @Override
    protected void handle() {
        if(npc.role.getType() != RoleType.TRANSPORTER)
            return;
        RoleTransporter role = (RoleTransporter) npc.role;
        if(role.hasTransport()){
            Packets.send(player, new PacketGuiData(role.getLocation().writeNBT()));
            Packets.send(player, new PacketGuiScrollSelected(role.getLocation().category.title));
        }
    }
}