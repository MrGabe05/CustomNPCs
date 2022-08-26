package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IDialogCategory;

public class DialogCategory implements IDialogCategory{

	public int id = -1;
	public String title = "";
	public HashMap<Integer,Dialog> dialogs;
	
	public DialogCategory(){
		dialogs = new HashMap<Integer, Dialog>();
	}
	
	public void readNBT(CompoundNBT compound){
        id = compound.getInt("Slot");
        title = compound.getString("Title");
        
        ListNBT dialogsList = compound.getList("Dialogs", 10);
        if(dialogsList != null){
            for(int ii = 0; ii < dialogsList.size(); ii++)
            {
            	Dialog dialog = new Dialog(this);
            	CompoundNBT comp = dialogsList.getCompound(ii);
            	dialog.readNBT(comp);
            	dialog.id = comp.getInt("DialogId");
            	dialogs.put(dialog.id, dialog);
            }
        }
	}

	public CompoundNBT writeNBT(CompoundNBT compound) {
		compound.putInt("Slot", id);
		compound.putString("Title", title);
        ListNBT dialogs = new ListNBT();
        for(Dialog dialog : this.dialogs.values()){
        	dialogs.add(dialog.save(new CompoundNBT()));
        }
		compound.put("Dialogs", dialogs);
        return compound;
	}

	@Override
	public List<IDialog> dialogs() {
		return new ArrayList<IDialog>(dialogs.values());
	}

	@Override
	public String getName() {
		return title;
	}

	@Override
	public IDialog create() {
		return new Dialog(this);
	}
}
