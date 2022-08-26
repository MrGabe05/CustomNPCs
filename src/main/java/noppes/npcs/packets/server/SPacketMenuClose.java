package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketMenuClose extends PacketServerBasic
{
    public SPacketMenuClose() {

    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketMenuClose msg, PacketBuffer buf) {

    }

    public static SPacketMenuClose decode(PacketBuffer buf) {
        return new SPacketMenuClose();
    }

    @Override
    protected void handle() {
        npc.reset();
        if(npc.linkedData != null)
            LinkedNpcController.Instance.saveNpcData(npc);
        NoppesUtilServer.setEditingNpc(player, null);
    }
}