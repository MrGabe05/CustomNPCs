package noppes.npcs.controllers.data;

import java.util.HashMap;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.NBTTags;
import noppes.npcs.NpcMiscInventory;


public class Bank {
	public int id = -1;
	public String name = "";
	public HashMap<Integer,Integer> slotTypes;
	public int startSlots = 1;
	public int maxSlots = 6;
	public NpcMiscInventory currencyInventory;
	public NpcMiscInventory upgradeInventory;
	public Bank(){
		slotTypes = new HashMap<Integer, Integer>();
		currencyInventory = new NpcMiscInventory(6);
		upgradeInventory = new NpcMiscInventory(6);
		for(int i = 0; i < 6; i++)
			slotTypes.put(i, 0);
	}
	
	public void addAdditionalSaveData(CompoundNBT nbttagcompound) {
		nbttagcompound.putInt("BankID", id);
    	nbttagcompound.put("BankCurrency", currencyInventory.getToNBT());
    	nbttagcompound.put("BankUpgrade", upgradeInventory.getToNBT());
    	nbttagcompound.putString("Username", name);
    	nbttagcompound.putInt("MaxSlots", maxSlots);
    	nbttagcompound.putInt("StartSlots", startSlots);
    	nbttagcompound.put("BankTypes", NBTTags.nbtIntegerIntegerMap(slotTypes));
	}

	public void readAdditionalSaveData(CompoundNBT nbttagcompound) {
		id = nbttagcompound.getInt("BankID");
        name = nbttagcompound.getString("Username");
        startSlots = nbttagcompound.getInt("StartSlots");
        maxSlots = nbttagcompound.getInt("MaxSlots");
        slotTypes = NBTTags.getIntegerIntegerMap(nbttagcompound.getList("BankTypes", 10));
		currencyInventory.setFromNBT(nbttagcompound.getCompound("BankCurrency"));
		upgradeInventory.setFromNBT(nbttagcompound.getCompound("BankUpgrade"));
	}
	
	public boolean isUpgraded(int slot){
    	return slotTypes.get(slot) != null && slotTypes.get(slot) == 2;
	}

	public boolean canBeUpgraded(int slot) {
    	if(upgradeInventory.getItem(slot) == null || upgradeInventory.getItem(slot).isEmpty())
    		return false;
    	return slotTypes.get(slot) == null || slotTypes.get(slot) == 0;
	}

	public int getMaxSlots() {
		for(int i = 0; i < maxSlots; i++){
	    	if((currencyInventory.getItem(i) == null || currencyInventory.getItem(i).isEmpty()) && i > startSlots-1)
	    		return i;
		}
		return maxSlots;
	}
}
