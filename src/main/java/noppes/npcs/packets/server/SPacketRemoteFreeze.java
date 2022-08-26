package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollSelected;

public class SPacketRemoteFreeze extends PacketServerBasic {
    public SPacketRemoteFreeze() {

    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_FREEZE;
    }

    public static void encode(SPacketRemoteFreeze msg, PacketBuffer buf) {

    }

    public static SPacketRemoteFreeze decode(PacketBuffer buf) {
        return new SPacketRemoteFreeze();
    }

    @Override
    protected void handle() {
        CustomNpcs.FreezeNPCs = !CustomNpcs.FreezeNPCs;
        Packets.send(player, new PacketGuiScrollSelected(CustomNpcs.FreezeNPCs?"Unfreeze Npcs":"Freeze Npcs"));
    }
}