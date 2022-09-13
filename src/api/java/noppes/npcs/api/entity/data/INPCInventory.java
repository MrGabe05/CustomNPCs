package noppes.npcs.api.entity.data;

import noppes.npcs.api.item.IItemStack;

public interface INPCInventory {

	IItemStack getRightHand();
	
	void setRightHand(IItemStack item);

	IItemStack getLeftHand();
	
	void setLeftHand(IItemStack item);

	IItemStack getProjectile();
	
	void setProjectile(IItemStack item);
	

	/**
	 * @param slot The armor slot to return. 0:head, 1:body, 2:legs, 3:boots
	 * @return Returns the armor item
	 */
    IItemStack getArmor(int slot);
	

	/**
	 * @param slot The armor slot to return. 0:head, 1:body, 2:legs, 3:boots
	 * @param item
	 */
    void setArmor(int slot, IItemStack item);

	/**
	 * @param slot 0-8
	 * @param item
	 * @param chance 1-100
	 */
    void setDropItem(int slot, IItemStack item, int chance);

	/**
	 * @param slot 0-8
	 */
    IItemStack getDropItem(int slot);

	int getExpMin();

	int getExpMax();

	/**
	 * @return Returns a value between expMin and expMax
	 */
    int getExpRNG();

	/**
	 * Sets the random exp dropped when the npc dies
	 */
    void setExp(int min, int max);

	IItemStack[] getItemsRNG();

}
