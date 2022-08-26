package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNaturalSpawnGetAll extends PacketServerBasic
{
    public SPacketNaturalSpawnGetAll() {

    }

    public static void encode(SPacketNaturalSpawnGetAll msg, PacketBuffer buf) {

    }

    public static SPacketNaturalSpawnGetAll decode(PacketBuffer buf) {
        return new SPacketNaturalSpawnGetAll();
    }

    @Override
    protected void handle() {
        NoppesUtilServer.sendScrollData(player, SpawnController.instance.getScroll());
    }
}