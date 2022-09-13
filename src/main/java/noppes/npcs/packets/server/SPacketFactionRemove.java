package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketFactionRemove extends PacketServerBasic {
    private final int id;
    public SPacketFactionRemove(int id) {
        this.id = id;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_FACTION;
    }

    public static void encode(SPacketFactionRemove msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketFactionRemove decode(PacketBuffer buf) {
        return new SPacketFactionRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        FactionController.instance.delete(id);
        SPacketFactionsGet.sendFactionDataAll(player);
        CompoundNBT compound = new CompoundNBT();
        (new Faction()).writeNBT(compound);
        Packets.send(player, new PacketGuiData(compound));
    }
}