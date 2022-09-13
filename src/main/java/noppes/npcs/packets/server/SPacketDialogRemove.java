package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketDialogRemove extends PacketServerBasic {
    private final int id;
    public SPacketDialogRemove(int id) {
        this.id = id;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_DIALOG;
    }

    public static void encode(SPacketDialogRemove msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketDialogRemove decode(PacketBuffer buf) {
        return new SPacketDialogRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        Dialog dialog = DialogController.instance.dialogs.get(id);
        if(dialog != null && dialog.category != null){
            DialogController.instance.removeDialog(dialog);
            Packets.send(player, new PacketGuiUpdate());
        }
    }
}