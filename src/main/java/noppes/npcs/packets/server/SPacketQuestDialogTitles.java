package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketQuestDialogTitles extends PacketServerBasic {
    private int dialogId1;
    private int dialogId2;
    private int dialogId3;

    public SPacketQuestDialogTitles(int dialogId1, int dialogId2, int dialogId3) {
        this.dialogId1 = dialogId1;
        this.dialogId2 = dialogId2;
        this.dialogId3 = dialogId3;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_QUEST;
    }

    public static void encode(SPacketQuestDialogTitles msg, PacketBuffer buf) {
        buf.writeInt(msg.dialogId1);
        buf.writeInt(msg.dialogId2);
        buf.writeInt(msg.dialogId3);
    }

    public static SPacketQuestDialogTitles decode(PacketBuffer buf) {
        return new SPacketQuestDialogTitles(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    protected void handle() {
        Dialog dialog = DialogController.instance.dialogs.get(dialogId1);
        Dialog dialog2 = DialogController.instance.dialogs.get(dialogId2);
        Dialog dialog3 = DialogController.instance.dialogs.get(dialogId3);
        CompoundNBT compound = new CompoundNBT();
        if(dialog != null)
            compound.putString("1", dialog.title);
        if(dialog2 != null)
            compound.putString("2", dialog2.title);
        if(dialog3 != null)
            compound.putString("3", dialog3.title);
        Packets.send(player, new PacketGuiData(compound));
    }
}