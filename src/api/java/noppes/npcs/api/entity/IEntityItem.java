package noppes.npcs.api.entity;

import net.minecraft.entity.item.ItemEntity;
import noppes.npcs.api.item.IItemStack;

public interface IEntityItem<T extends ItemEntity> extends IEntity<T>{

	/**
	 * @return The owner of the item, only the owner can pick the item up
	 */
	String getOwner();

	/**
	 * @param name The UUID for the owner of the item, only the owner can pick up the item
	 * (note that the item can also be picked up if the lifetime - age is equal or smaller than 200)
	 */
	void setOwner(String name);

	/**
	 * @return Ticks remaining before it can be picked up (32767 is infinite)
	 */
	int getPickupDelay();

	/**
	 * @param delay Delay before the item can be picked up (32767 is infinite delay)
	 */
	void setPickupDelay(int delay);
	
	/** 
	 * @return Returns the age of the item
	 */
	long getAge();

	/**
	 * @param age Age of the item (-32767 is infinite age)
	 */
	void setAge(long age);

	/**
	 * @return When the age reaches this the item despawns
	 */
	int getLifeSpawn();
	
	/**
	 * @param age Age at which the item despawns
	 */
	void setLifeSpawn(int age);

	IItemStack getItem();

	void setItem(IItemStack item);
}
