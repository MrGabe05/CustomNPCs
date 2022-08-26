package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketDialogCategoryRemove extends PacketServerBasic {
    private int id;
    public SPacketDialogCategoryRemove(int id) {
        this.id = id;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_DIALOG;
    }

    public static void encode(SPacketDialogCategoryRemove msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketDialogCategoryRemove decode(PacketBuffer buf) {
        return new SPacketDialogCategoryRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        DialogController.instance.removeCategory(id);
        Packets.send(player, new PacketGuiUpdate());
    }
}