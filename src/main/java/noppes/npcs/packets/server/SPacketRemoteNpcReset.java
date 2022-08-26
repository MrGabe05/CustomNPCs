package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketRemoteNpcReset extends PacketServerBasic {
    private int entityId;

    public SPacketRemoteNpcReset(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_RESET;
    }

    public static void encode(SPacketRemoteNpcReset msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
    }

    public static SPacketRemoteNpcReset decode(PacketBuffer buf) {
        return new SPacketRemoteNpcReset(buf.readInt());
    }

    @Override
    protected void handle() {
        Entity entity = player.level.getEntity(entityId);
        if(entity == null || !(entity instanceof EntityNPCInterface))
            return;
        npc = (EntityNPCInterface) entity;
        npc.reset();
    }
}