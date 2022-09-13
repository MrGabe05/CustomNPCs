package noppes.npcs.api;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.IInventory;
import noppes.npcs.api.item.IItemStack;

public interface IContainer {

	int getSize();
	
	IItemStack getSlot(int slot);
	
	void setSlot(int slot, IItemStack item);
	
	/**
	 * Expert users only
	 * @return Returns minecrafts container
	 */
	IInventory getMCInventory();
	
	/**
	 * Expert users only
	 * @return Returns minecrafts container
	 */
	Container getMCContainer();

	/**
	 * @param item
	 * @param ignoreDamage Whether to ignore the item_damage value when comparing
	 * @param ignoreNBT Whether to ignore NBT when comparing
	 * @return
	 */
	int count(IItemStack item, boolean ignoreDamage, boolean ignoreNBT);
	
	IItemStack[] getItems();
}
