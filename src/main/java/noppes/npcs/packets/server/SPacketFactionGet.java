package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketFactionGet extends PacketServerBasic {

    private int id;

    public SPacketFactionGet(int id) {
        this.id = id;
    }


    public static void encode(SPacketFactionGet msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketFactionGet decode(PacketBuffer buf) {
        return new SPacketFactionGet(buf.readInt());
    }

    @Override
    protected void handle() {
        CompoundNBT compound = new CompoundNBT();
        Faction faction = FactionController.instance.getFaction(id);
        faction.writeNBT(compound);
        Packets.send(player, new PacketGuiData(compound));
    }

}