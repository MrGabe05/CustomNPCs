package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.packets.IPacketServer;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

import java.io.IOException;

public class SPacketDialogCategorySave extends IPacketServer {
    private CompoundNBT data;
    public SPacketDialogCategorySave(CompoundNBT data) {
        this.data = data;
    }

    public SPacketDialogCategorySave(){

    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_DIALOG;
    }

    @Override
    public void handle() {
        DialogCategory category = new DialogCategory();
        category.readNBT(data);
        DialogController.instance.saveCategory(category);
        Packets.send(player, new PacketGuiUpdate());
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        data = buf.readNbt();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeNbt(data);
    }
}