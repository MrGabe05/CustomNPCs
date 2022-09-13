package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketLinkedSet extends PacketServerBasic {

    private final String name;

    public SPacketLinkedSet(String name) {
        this.name = name;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketLinkedSet msg, PacketBuffer buf) {
        buf.writeUtf(msg.name, 32767);
    }

    public static SPacketLinkedSet decode(PacketBuffer buf) {
        return new SPacketLinkedSet(buf.readUtf(32767));
    }

    @Override
    protected void handle() {
        npc.linkedName = name;
        LinkedNpcController.Instance.loadNpcData(npc);
    }
}