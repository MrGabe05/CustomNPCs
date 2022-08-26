package noppes.npcs.controllers.data;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.api.handler.data.IRecipe;
import noppes.npcs.controllers.RecipeController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeCarpentry extends ShapedRecipe implements IRecipe{
	public Availability availability = new Availability();
	public boolean isGlobal = false;
	public boolean ignoreDamage = false;
	public boolean ignoreNBT = false;
	public boolean savesRecipe = true;
	public String name;

	public RecipeCarpentry(ResourceLocation location, int width, int height, NonNullList<Ingredient> recipe, ItemStack result) {
		super(location, "customnpcs", width, height, recipe, result);
	}

	public RecipeCarpentry(ResourceLocation location, String name) {
		super(location, "customnpcs", 0, 0, NonNullList.create(), ItemStack.EMPTY);
		this.name = name;
	}

	public static RecipeCarpentry load(CompoundNBT compound) {
		ResourceLocation location = null;
		if(compound.contains("ID")){
			location = new ResourceLocation("customnpcs", compound.getString("ID"));
		}
		else{
			location = new ResourceLocation(compound.getString("Id"));
		}
		RecipeCarpentry recipe = new RecipeCarpentry(location, compound.getInt("Width"), compound.getInt("Height"),
				NBTTags.getIngredientList(compound.getList("Materials", 10)), ItemStack.of(compound.getCompound("Item")));
		recipe.availability.load(compound.getCompound("Availability"));
		recipe.ignoreDamage = compound.getBoolean("IgnoreDamage");
		recipe.ignoreNBT = compound.getBoolean("IgnoreNBT");
		recipe.isGlobal = compound.getBoolean("Global");

		return recipe;
	}

	public CompoundNBT writeNBT() {
		CompoundNBT compound = new CompoundNBT();
		compound.putInt("Width", getRecipeWidth());
		compound.putInt("Height", getRecipeHeight());
		if(getResult() != null)
			compound.put("Item", getResult().save(new CompoundNBT()));
		compound.put("Materials", NBTTags.nbtIngredientList(getIngredients()));
		compound.put("Availability", availability.save(new CompoundNBT()));
		compound.putString("Name", name);
		compound.putString("Id", getId().toString());
		compound.putBoolean("Global", isGlobal);
		compound.putBoolean("IgnoreDamage", ignoreDamage);
		compound.putBoolean("IgnoreNBT", ignoreNBT);
		return compound;
	}

	public static RecipeCarpentry createRecipe(ResourceLocation location, RecipeCarpentry recipe, ItemStack par1ItemStack, Object... limbSwingAmountArrayOfObj) {
		String var3 = "";
		int var4 = 0;
		int var5 = 0;
		int var6 = 0;
		int var9;

		if (limbSwingAmountArrayOfObj[var4] instanceof String[]) {
			String[] var7 = (String[]) limbSwingAmountArrayOfObj[var4++];
			String[] var8 = var7;
			var9 = var7.length;

			for (int var10 = 0; var10 < var9; ++var10) {
				String var11 = var8[var10];
				++var6;
				var5 = var11.length();
				var3 = var3 + var11;
			}
		} else {
			while (limbSwingAmountArrayOfObj[var4] instanceof String) {
				String var13 = (String) limbSwingAmountArrayOfObj[var4++];
				++var6;
				var5 = var13.length();
				var3 = var3 + var13;
			}
		}

		HashMap var14;

		for (var14 = new HashMap(); var4 < limbSwingAmountArrayOfObj.length; var4 += 2) {
			Character var16 = (Character) limbSwingAmountArrayOfObj[var4];
			ItemStack var17 = ItemStack.EMPTY;

			if (limbSwingAmountArrayOfObj[var4 + 1] instanceof Item) {
				var17 = new ItemStack((Item) limbSwingAmountArrayOfObj[var4 + 1]);
			} else if (limbSwingAmountArrayOfObj[var4 + 1] instanceof Block) {
				var17 = new ItemStack((Block) limbSwingAmountArrayOfObj[var4 + 1], 1);
			} else if (limbSwingAmountArrayOfObj[var4 + 1] instanceof ItemStack) {
				var17 = (ItemStack) limbSwingAmountArrayOfObj[var4 + 1];
			}

			var14.put(var16, var17);
		}

		NonNullList<Ingredient> ingredients = NonNullList.create();

		for (var9 = 0; var9 < var5 * var6; ++var9) {
			char var18 = var3.charAt(var9);

			if (var14.containsKey(Character.valueOf(var18))) {
				ingredients.add(var9, Ingredient.of(((ItemStack) var14.get(Character.valueOf(var18))).copy()));
			} else {
				ingredients.add(var9, Ingredient.EMPTY);
			}
		}

		RecipeCarpentry newrecipe = new RecipeCarpentry(location, var5, var6, ingredients, par1ItemStack);
		newrecipe.copy(recipe);
		if (var5 == 4 || var6 == 4)
			newrecipe.isGlobal = false;

		return newrecipe;
	}

	@Override
	public boolean matches(CraftingInventory inventoryCrafting, World world) {
		for (int i = 0; i <= 4 - this.getRecipeWidth(); ++i) {
			for (int j = 0; j <= 4 - this.getRecipeHeight(); ++j) {
				if (this.checkMatch(inventoryCrafting, i, j, true))
					return true;

				if (this.checkMatch(inventoryCrafting, i, j, false))
					return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getResultItem() {
		if (getResult().isEmpty())
			return ItemStack.EMPTY;
		return getResult().copy();
	}

	/**
	 * Checks if the region of a crafting inventory is match for the recipe.
	 */
	private boolean checkMatch(IInventory inventoryCrafting, int limbSwingAmount, int par3, boolean par4) {

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int var7 = i - limbSwingAmount;
				int var8 = j - par3;
				Ingredient ingredient = Ingredient.EMPTY;

				if (var7 >= 0 && var8 >= 0 && var7 < this.getRecipeWidth() && var8 < this.getRecipeHeight()) {
					if (par4)
						ingredient = this.getIngredients().get(this.getRecipeWidth() - var7 - 1 + var8 * this.getRecipeWidth());
					else
						ingredient = this.getIngredients().get(var7 + var8 * this.getRecipeWidth());
				}

				//ItemStack var10 = inventoryCrafting.getStackInRowAndColumn(i, j);
				ItemStack var10 = ItemStack.EMPTY;

				if (!var10.isEmpty() || ingredient.getItems().length == 0) {
					return false;
				}

				ItemStack var9 = ingredient.getItems()[0];

				if ((!var10.isEmpty() || !var9.isEmpty()) && !NoppesUtilPlayer.compareItems(var9, var10, ignoreDamage, ignoreNBT)) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inventoryCrafting) {
		NonNullList<ItemStack> list = NonNullList.withSize(inventoryCrafting.getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); ++i) {
			ItemStack itemstack = inventoryCrafting.getItem(i);
			list.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
		}

		return list;
	}

	@Override
	public boolean isSpecial() {
		return false;
	}

	public void copy(RecipeCarpentry recipe) {
		this.availability = recipe.availability;
		this.isGlobal = recipe.isGlobal;
		this.ignoreDamage = recipe.ignoreDamage;
		this.ignoreNBT = recipe.ignoreNBT;
	}

	public ItemStack getCraftingItem(int i){
		if (i >= getIngredients().size())
			return ItemStack.EMPTY;
		Ingredient ingredients = getIngredients().get(i);
		if (ingredients.getItems().length == 0)
			return ItemStack.EMPTY;
		return ingredients.getItems()[0];
	}

	public boolean isValid() {
		if(getIngredients().size() == 0 || getResult().isEmpty())
			return false;
		for(Ingredient ingredient : getIngredients()){
			if(ingredient.getItems().length > 0)
				return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return name;
	}
	@Override
	public ItemStack getResult() {
		return getResult();
	}
	@Override
	public boolean isGlobal() {
		return isGlobal;
	}
	@Override
	public void setIsGlobal(boolean bo) {
		isGlobal = bo;
	}
	@Override
	public boolean getIgnoreNBT() {
		return ignoreNBT;
	}
	@Override
	public void setIgnoreNBT(boolean bo) {
		ignoreNBT = bo;
	}
	@Override
	public boolean getIgnoreDamage() {
		return ignoreDamage;
	}
	@Override
	public void setIgnoreDamage(boolean bo) {
		ignoreDamage = bo;
	}
	@Override
	public void save() {
		RecipeController.instance.saveRecipe(this);
	}
	@Override
	public void delete() {
		//RecipeController.instance.delete(getId());
	}
	@Override
	public int getWidth() {
		return this.getRecipeWidth();
	}
	@Override
	public int getHeight() {
		return getRecipeHeight();
	}
	@Override
	public ItemStack[] getRecipe() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for(Ingredient ingredient : getIngredients()){
			if(ingredient.getItems().length > 0)
				list.add(ingredient.getItems()[0]);
		}
		return list.toArray(new ItemStack[list.size()]);
	}
	@Override
	public void saves(boolean bo) {
		savesRecipe = bo;
	}
	@Override
	public boolean saves() {
		return savesRecipe;
	}

}
