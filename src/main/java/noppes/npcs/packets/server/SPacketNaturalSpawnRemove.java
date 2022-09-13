package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNaturalSpawnRemove extends PacketServerBasic
{
    private final int id;
    public SPacketNaturalSpawnRemove(int id) {
        this.id = id;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_NATURALSPAWN;
    }

    public static void encode(SPacketNaturalSpawnRemove msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketNaturalSpawnRemove decode(PacketBuffer buf) {
        return new SPacketNaturalSpawnRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        SpawnController.instance.removeSpawnData(id);
        NoppesUtilServer.sendScrollData(player, SpawnController.instance.getScroll());
    }
}