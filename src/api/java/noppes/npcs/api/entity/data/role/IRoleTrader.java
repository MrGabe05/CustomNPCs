package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.entity.data.INPCRole;
import noppes.npcs.api.item.IItemStack;

public interface IRoleTrader extends INPCRole{

	/**
	 * @param slot Slot number 0-17
	 */
    IItemStack getSold(int slot);
	
	/**
	 * @param slot Slot number 0-17
	 */
    IItemStack getCurrency1(int slot);
	
	/**
	 * @param slot Slot number 0-17
	 */
    IItemStack getCurrency2(int slot);

	/**
	 * @param slot Slot number 0-17
	 */
    void set(int slot, IItemStack currency, IItemStack currency2, IItemStack sold);

	/**
	 * @param slot Slot number 0-17
	 */
    void remove(int slot);

	void setMarket(String name);

	String getMarket();
}
