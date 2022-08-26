package noppes.npcs.controllers.data;

import java.util.HashMap;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.controllers.BankController;

public class PlayerBankData{
	public HashMap<Integer,BankData> banks;
	public PlayerBankData(){
		banks = new HashMap<Integer,BankData>();
	}
	
	public void loadNBTData(CompoundNBT compound) {
		HashMap<Integer,BankData> banks = new HashMap<Integer,BankData>();
        ListNBT list = compound.getList("BankData", 10);
        if(list == null){
        	return;
        }
        for(int i = 0; i < list.size(); i++)
        {
            CompoundNBT nbttagcompound = list.getCompound(i);
			BankData data = new BankData();
            data.readNBT(nbttagcompound);
            banks.put(data.bankId, data);
        }
        this.banks = banks;
	}

	public void saveNBTData(CompoundNBT playerData){
		ListNBT list = new ListNBT();
		for(BankData data : banks.values()){
			CompoundNBT nbttagcompound = new CompoundNBT();
			data.writeNBT(nbttagcompound);
			list.add(nbttagcompound);
		}
		playerData.put("BankData", list);
	}
	

	public BankData getBank(int bankId) {
		return banks.get(bankId);
	}
	public BankData getBankOrDefault(int bankId) {
		BankData data = banks.get(bankId);
		if(data != null)
			return data;
		Bank bank = BankController.getInstance().getBank(bankId);
		return banks.get(bank.id);
	}
	public boolean hasBank(int bank) {
		return banks.containsKey(bank);
	}
	public void loadNew(int bank) {
		BankData data = new BankData();
		data.bankId = bank;
		banks.put(bank, data);
				
	}
}
