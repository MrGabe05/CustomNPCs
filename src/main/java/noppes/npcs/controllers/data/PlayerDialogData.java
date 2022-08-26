package noppes.npcs.controllers.data;

import java.util.HashSet;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class PlayerDialogData{
	public HashSet<Integer> dialogsRead = new HashSet<Integer>();
	
	public void loadNBTData(CompoundNBT compound) {
		HashSet<Integer> dialogsRead = new HashSet<Integer>();
		if(compound == null)
			return;
        ListNBT list = compound.getList("DialogData", 10);
        if(list == null){
        	return;
        }

        for(int i = 0; i < list.size(); i++)
        {
            CompoundNBT nbttagcompound = list.getCompound(i);
            dialogsRead.add(nbttagcompound.getInt("Dialog"));
        }
        this.dialogsRead = dialogsRead;
	}

	public void saveNBTData(CompoundNBT compound) {
		ListNBT list = new ListNBT();
		for(int dia : dialogsRead){
			CompoundNBT nbttagcompound = new CompoundNBT();
			nbttagcompound.putInt("Dialog", dia);
			list.add(nbttagcompound);
		}
		
		compound.put("DialogData", list);
	}
	
}
