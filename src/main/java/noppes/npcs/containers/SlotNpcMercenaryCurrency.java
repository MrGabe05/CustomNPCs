package noppes.npcs.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import noppes.npcs.roles.RoleFollower;

class SlotNpcMercenaryCurrency extends Slot
{

	RoleFollower role; /* synthetic field */

    public SlotNpcMercenaryCurrency(RoleFollower role, IInventory inv, int i, int j, int k)
    {
        super(inv, i, j, k);
        this.role = role;
    }

    @Override
    public int getMaxStackSize()
    {
        return 64;
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
    	Item item = itemstack.getItem();
    	for(ItemStack is : role.inventory.items){
    		if(item == is.getItem()){
    			return true;
    		}
    	}
        return false;
        
    }
}
