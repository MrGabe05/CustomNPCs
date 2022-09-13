package noppes.npcs.api.handler.data;

import net.minecraft.item.ItemStack;

public interface IRecipe {
	
	String getName();
	
	
	boolean isGlobal();
	
	void setIsGlobal(boolean bo);
	
	boolean getIgnoreNBT();
	
	void setIgnoreNBT(boolean bo);
	
	boolean getIgnoreDamage();
	
	void setIgnoreDamage(boolean bo);
	
	int getWidth();
	
	int getHeight();
	
	ItemStack getResult();
	
	ItemStack[] getRecipe();
	
	/**
	 * @param bo Whether or not the recipe saves with customnpcs recipes
	 */
	void saves(boolean bo);
	
	boolean saves();
	
	void save();
	
	void delete();
}
