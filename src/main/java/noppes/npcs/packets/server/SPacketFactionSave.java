package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketFactionSave extends PacketServerBasic {
    private CompoundNBT data;
    public SPacketFactionSave(CompoundNBT data) {
        this.data = data;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_FACTION;
    }

    public static void encode(SPacketFactionSave msg, PacketBuffer buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketFactionSave decode(PacketBuffer buf) {
        return new SPacketFactionSave(buf.readNbt());
    }

    @Override
    protected void handle() {
        Faction faction = new Faction();
        faction.readNBT(data);
        FactionController.instance.saveFaction(faction);
        SPacketFactionsGet.sendFactionDataAll(player);
        CompoundNBT compound = new CompoundNBT();
        faction.writeNBT(compound);
        Packets.send(player, new PacketGuiData(compound));
    }
}