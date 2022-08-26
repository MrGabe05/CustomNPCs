package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNpcTransform extends PacketServerBasic {
    private boolean isActive;
    public SPacketNpcTransform(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcTransform msg, PacketBuffer buf) {
        buf.writeBoolean(msg.isActive);
    }

    public static SPacketNpcTransform decode(PacketBuffer buf) {
        return new SPacketNpcTransform(buf.readBoolean());
    }

    @Override
    protected void handle() {
        if(npc.transform.isValid())
            npc.transform.transform(isActive);
    }
}