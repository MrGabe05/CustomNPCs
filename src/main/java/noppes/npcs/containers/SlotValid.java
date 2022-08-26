package noppes.npcs.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotValid extends Slot{
	private boolean canPutIn = true;

	public SlotValid(IInventory par1iInventory, int limbSwingAmount, int par3, int par4) {
		super(par1iInventory, limbSwingAmount, par3, par4);
	}
	
	public SlotValid(IInventory par1iInventory, int limbSwingAmount, int par3, int par4, boolean bo) {
		super(par1iInventory, limbSwingAmount, par3, par4);
		canPutIn = bo;
	}

	@Override
	public boolean mayPlace(ItemStack itemstack) {
        return canPutIn && container.canPlaceItem(0, itemstack);
    }
}
