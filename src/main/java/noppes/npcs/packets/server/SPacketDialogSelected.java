package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiClose;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleDialog;

public class SPacketDialogSelected extends PacketServerBasic {
    private final int dialogId;
    private final int optionId;

    public SPacketDialogSelected(int dialogId, int optionId) {
        this.dialogId = dialogId;
        this.optionId = optionId;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    public static void encode(SPacketDialogSelected msg, PacketBuffer buf) {
        buf.writeInt(msg.dialogId);
        buf.writeInt(msg.optionId);
    }

    public static SPacketDialogSelected decode(PacketBuffer buf) {
        return new SPacketDialogSelected(buf.readInt(), buf.readInt());
    }

    @Override
    protected void handle() {
        PlayerData data = PlayerData.get(player);

        if(data.dialogId != dialogId) {
            return;
        }
        if(data.dialogId < 0 && npc.role.getType() == RoleType.DIALOG){
            String text = ((RoleDialog)npc.role).optionsTexts.get(optionId);
            if(text != null && !text.isEmpty()){
                Dialog d = new Dialog(null);
                d.text = text;
                NoppesUtilServer.openDialog(player, npc, d);
            }
            return;
        }
        Dialog dialog = DialogController.instance.dialogs.get(data.dialogId);
        if(dialog == null)
            return;
        if(!dialog.hasDialogs(player) && !dialog.hasOtherOptions()) {
            closeDialog(player, npc, true);
            return;
        }

        DialogOption option = dialog.options.get(optionId);

        if(option == null || EventHooks.onNPCDialogOption(npc, player, dialog, option) ||
                option.optionType == OptionType.DIALOG_OPTION && (!option.isAvailable(player) || !option.hasDialog()) ||
                option.optionType == OptionType.DISABLED || option.optionType == OptionType.QUIT_OPTION) {
            closeDialog(player, npc, true);
            return;
        }

        if(option.optionType == OptionType.ROLE_OPTION){
            closeDialog(player, npc, true);
            if(npc.role.getType() == RoleType.COMPANION)
                ((RoleCompanion)npc.role).interact(player, true);
            else
                npc.role.interact(player);
        }
        else if(option.optionType == OptionType.DIALOG_OPTION){
            closeDialog(player, npc, false);
            NoppesUtilServer.openDialog(player, npc, option.getDialog());
        }
        else if(option.optionType == OptionType.COMMAND_BLOCK){
            closeDialog(player, npc, true);
            NoppesUtilServer.runCommand(npc, npc.getName().getString(), option.command, player);
        }
        else {
            closeDialog(player, npc, true);
        }
    }

    public void closeDialog(ServerPlayerEntity player, EntityNPCInterface npc, boolean notifyClient) {
        PlayerData data = PlayerData.get(player);
        Dialog dialog = DialogController.instance.dialogs.get(data.dialogId);
        EventHooks.onNPCDialogClose(npc, player, dialog);
        if(notifyClient) {
            Packets.send(player, new PacketGuiClose(new CompoundNBT()));
        }
        data.dialogId = -1;
    }
}