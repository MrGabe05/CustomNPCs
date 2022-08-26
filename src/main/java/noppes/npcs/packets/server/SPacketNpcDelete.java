package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketNpcDelete;

public class SPacketNpcDelete extends PacketServerBasic
{
    public SPacketNpcDelete() {

    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_DELETE;
    }

    public static void encode(SPacketNpcDelete msg, PacketBuffer buf) {

    }

    public static SPacketNpcDelete decode(PacketBuffer buf) {
        return new SPacketNpcDelete();
    }

    @Override
    protected void handle() {
        Packets.sendNearby(npc, new PacketNpcDelete(npc.getId()));
        npc.delete();
    }
}