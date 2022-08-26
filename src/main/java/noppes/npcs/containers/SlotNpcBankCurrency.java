package noppes.npcs.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilServer;

public class SlotNpcBankCurrency extends Slot{

    public ItemStack item = ItemStack.EMPTY; 

    public SlotNpcBankCurrency(ContainerNPCBankInterface containerplayer, IInventory iinventory, int i, int j, int k){
        super(iinventory, i, j, k);
    }

    @Override
    public int getMaxStackSize(){
        return 64;
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
    	if(NoppesUtilServer.IsItemStackNull(itemstack))
    		return false;
		if(item.getItem() == itemstack.getItem()){
		    return true;
		}
		return false;
    }
}
