package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketRemoteNpcTp extends PacketServerBasic {
    private int entityId;

    public SPacketRemoteNpcTp(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(SPacketRemoteNpcTp msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
    }

    public static SPacketRemoteNpcTp decode(PacketBuffer buf) {
        return new SPacketRemoteNpcTp(buf.readInt());
    }

    @Override
    protected void handle() {
        Entity entity = player.level.getEntity(entityId);
        if(entity == null || !(entity instanceof EntityNPCInterface))
            return;
        npc = (EntityNPCInterface) entity;
        player.connection.teleport(npc.getX(), npc.getY(), npc.getZ(), 0, 0);
    }
}