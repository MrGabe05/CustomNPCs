package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketNaturalSpawnGet extends PacketServerBasic
{
    private int id;
    public SPacketNaturalSpawnGet(int id) {
        this.id = id;
    }

    public static void encode(SPacketNaturalSpawnGet msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketNaturalSpawnGet decode(PacketBuffer buf) {
        return new SPacketNaturalSpawnGet(buf.readInt());
    }

    @Override
    protected void handle() {
        SpawnData spawn = SpawnController.instance.getSpawnData(id);
        if(spawn != null){
            Packets.send(player, new PacketGuiData(spawn.writeNBT(new CompoundNBT())));
        }
    }
}