package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.controllers.data.Bank;

public class ContainerManageBanks extends Container
{
	public Bank bank;
	
    public ContainerManageBanks(int containerId, PlayerInventory playerInventory) {
		super(CustomContainer.container_managebanks, containerId);
		bank = new Bank();
   		
        for(int i = 0; i < 6; i++)
        {
        	int x =  36;
        	int y = 38;
        	y += i * 22;
        	addSlot(new Slot(bank.currencyInventory, i, x, y));
        }   
        
        for(int i = 0; i < 6; i++)
        {
        	int x =  142;
        	int y = 38;
        	y += i * 22;
        	addSlot(new Slot(bank.upgradeInventory, i, x, y));
        }   

        for(int j1 = 0; j1 < 9; j1++)
        {
        	addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 171));
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity par1PlayerEntity, int i){
        return ItemStack.EMPTY;
    }
    
	@Override
	public boolean stillValid(PlayerEntity entityplayer) {
		return true;
	}

	public void setBank(Bank bank2) {
		for(int i = 0; i< 6; i++){
			bank.currencyInventory.setItem(i, bank2.currencyInventory.getItem(i));
			bank.upgradeInventory.setItem(i, bank2.upgradeInventory.getItem(i));
		}
	}
}

