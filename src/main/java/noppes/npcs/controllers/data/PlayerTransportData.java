package noppes.npcs.controllers.data;

import java.util.HashSet;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class PlayerTransportData{
	public HashSet<Integer> transports = new HashSet<Integer>();

	public void loadNBTData(CompoundNBT compound) {
		HashSet<Integer> dialogsRead = new HashSet<Integer>();
		if(compound == null)
			return;
        ListNBT list = compound.getList("TransportData", 10);
        if(list == null){
        	return;
        }

        for(int i = 0; i < list.size(); i++)
        {
            CompoundNBT nbttagcompound = list.getCompound(i);
            dialogsRead.add(nbttagcompound.getInt("Transport"));
        }
        this.transports = dialogsRead;
	}

	public void saveNBTData(CompoundNBT compound) {
		ListNBT list = new ListNBT();
		for(int dia : transports){
			CompoundNBT nbttagcompound = new CompoundNBT();
			nbttagcompound.putInt("Transport", dia);
			list.add(nbttagcompound);
		}
		
		compound.put("TransportData", list);
	}
	
}
