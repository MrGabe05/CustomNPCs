package noppes.npcs.controllers.data;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.RecipeController;
public class RecipesDefault {
	public static void addRecipe(String name, Object ob, boolean isGlobal, Object... recipe){
		ItemStack item;
		if(ob instanceof Item)
			item = new ItemStack((Item)ob);
		else if(ob instanceof Block)
			item = new ItemStack((Block)ob);
		else
			item = (ItemStack) ob;
		
		RecipeCarpentry recipeAnvil = new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, name), name);
		recipeAnvil.isGlobal = isGlobal;
		recipeAnvil = RecipeCarpentry.createRecipe(new ResourceLocation(CustomNpcs.MODID, name), recipeAnvil, item, recipe);
		RecipeController.instance.saveRecipe(recipeAnvil);
	}
	public static void loadDefaultRecipes(int i) {
		if(i < 0){
			addRecipe("npc_wand", CustomItems.wand, true, "XX"," Y"," Y",'X', Items.BREAD,'Y', Items.STICK);
			addRecipe("mob_cloner", CustomItems.cloner, true, "XX","XY"," Y",'X', Items.BREAD,'Y', Items.STICK);

		}
	}
}
