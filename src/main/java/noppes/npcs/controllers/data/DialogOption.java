package noppes.npcs.controllers.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.handler.data.IDialogOption;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.db.DatabaseColumn;

public class DialogOption implements IDialogOption{
	@DatabaseColumn(name = "id", type = DatabaseColumn.Type.INT)
	public int id = -1;

	@DatabaseColumn(name = "dialog", type = DatabaseColumn.Type.INT)
	public int dialogId = -1;

	@DatabaseColumn(name = "option", type = DatabaseColumn.Type.VARCHAR)
	public String option = "Talk";

	@DatabaseColumn(name = "text", type = DatabaseColumn.Type.TEXT)
	public String title = "Talk";

	@DatabaseColumn(name = "type", type = DatabaseColumn.Type.SMALLINT)
	public int optionType = OptionType.DIALOG_OPTION;

	@DatabaseColumn(name = "color", type = DatabaseColumn.Type.SMALLINT)
	public int optionColor = 0xe0e0e0;

	@DatabaseColumn(name = "command", type = DatabaseColumn.Type.TEXT)
	public String command = "";

	@DatabaseColumn(name = "order", type = DatabaseColumn.Type.SMALLINT)
	public int slot = -1;
	
	public void readNBT(CompoundNBT compound) {
		if(compound == null)
			return;
		title = compound.getString("Title");
		dialogId = compound.getInt("Dialog");
		optionColor = compound.getInt("DialogColor");
		optionType = compound.getInt("OptionType");
		command = compound.getString("DialogCommand");
		if(optionColor == 0){
			optionColor = 0xe0e0e0;
		}
	}

	public CompoundNBT writeNBT() {
		CompoundNBT compound = new CompoundNBT();
		compound.putString("Title", title);
		compound.putInt("OptionType", optionType);
		compound.putInt("Dialog", dialogId);
		compound.putInt("DialogColor", optionColor);
		compound.putString("DialogCommand", command);
		return compound;
	}
	

	public boolean hasDialog(){
		if(dialogId <= 0 || optionType != OptionType.DIALOG_OPTION)
			return false;
        return DialogController.instance.hasDialog(dialogId);
    }

	public Dialog getDialog() {
		if(!hasDialog())
			return null;
		return DialogController.instance.dialogs.get(dialogId);
	}

	public boolean isAvailable(PlayerEntity player) {
		if(optionType == OptionType.DISABLED)
			return false;
		if(optionType != OptionType.DIALOG_OPTION)
			return true;
		Dialog dialog = getDialog();
		if(dialog == null)
			return false;
		
		return dialog.availability.isAvailable(player);
	}

	public boolean isValid(){
		if(optionType == OptionType.DISABLED){
			return false;
		}
        return optionType != OptionType.DIALOG_OPTION || hasDialog();
    }

	public boolean canClose(){
		return !(optionType == OptionType.DIALOG_OPTION && hasDialog());
	}

	@Override
	public int getSlot() {
		return slot;
	}

	@Override
	public String getName() {
		return title;
	}

	@Override
	public int getType() {
		return optionType;
	}
}
