package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNpcDialogRemove extends PacketServerBasic {
    private final int slot;

    public SPacketNpcDialogRemove(int slot) {
        this.slot = slot;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcDialogRemove msg, PacketBuffer buf) {
        buf.writeInt(msg.slot);
    }

    public static SPacketNpcDialogRemove decode(PacketBuffer buf) {
        return new SPacketNpcDialogRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        npc.dialogs.remove(slot);
    }
}