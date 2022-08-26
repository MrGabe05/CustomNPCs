package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.packets.IPacketServer;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

import java.io.IOException;

public class SPacketDialogSave extends IPacketServer {
    private int category;
    private CompoundNBT data;
    public SPacketDialogSave(int category, CompoundNBT data) {
        this.data = data;
        this.category = category;
    }

    public SPacketDialogSave(){

    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_DIALOG;
    }

    @Override
    public void handle() {
        DialogCategory dcategory = DialogController.instance.categories.get(category);
        if(dcategory == null)
            return;
        Dialog dialog = new Dialog(dcategory);
        dialog.readNBT(data);
        DialogController.instance.saveDialog(dcategory, dialog);
        Packets.send(player, new PacketGuiUpdate());
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        category = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeInt(category);
        buf.writeNbt(data);
    }
}