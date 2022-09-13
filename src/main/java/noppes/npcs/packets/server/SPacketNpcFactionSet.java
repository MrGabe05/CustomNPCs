package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNpcFactionSet extends PacketServerBasic {
    private final int faction;

    public SPacketNpcFactionSet(int faction) {
        this.faction = faction;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcFactionSet msg, PacketBuffer buf) {
        buf.writeInt(msg.faction);
    }

    public static SPacketNpcFactionSet decode(PacketBuffer buf) {
        return new SPacketNpcFactionSet(buf.readInt());
    }

    @Override
    protected void handle() {
        npc.setFaction(faction);
    }
}