package noppes.npcs.controllers;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.handler.IRecipeHandler;
import noppes.npcs.api.handler.data.IRecipe;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.controllers.data.RecipesDefault;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeController implements IRecipeHandler{
	public HashMap<ResourceLocation, RecipeCarpentry> globalRecipes = new HashMap<ResourceLocation, RecipeCarpentry>();
	public HashMap<ResourceLocation, RecipeCarpentry> anvilRecipes = new HashMap<ResourceLocation, RecipeCarpentry>();
	public static RecipeController instance;

	public static final int version = 1;
	public int nextId = 1;
	
	public static HashMap<Integer, RecipeCarpentry> syncRecipes = new HashMap<Integer, RecipeCarpentry>();
	//public static IForgeRegistry<IRecipe> Registry;
	
	public RecipeController(){
		instance = this;
	}
	
	public void load(){
		loadCategories();
		reloadGlobalRecipes();
		EventHooks.onGlobalRecipesLoaded(this);
	}
	
	public void reloadGlobalRecipes(){

//        ForgeRegistry<net.minecraft.item.crafting.IRecipe> reg = (ForgeRegistry<net.minecraft.item.crafting.IRecipe>)ForgeRegistries.RECIPES;
//        reg.unfreeze();
//        reg.clear();
//        CraftingHelper.loadRecipes(false);
//		ForgeRegistry reg = (ForgeRegistry) Registry;
//		reg.unfreeze();
//		Iterator<Entry<ResourceLocation, net.minecraft.item.crafting.IRecipe>> list = reg.getEntries().iterator();
//		while(list.hasNext()){
//			Entry<ResourceLocation, net.minecraft.item.crafting.IRecipe> rec = list.next();
//			net.minecraft.item.crafting.IRecipe recipe = rec.getValue();
//			if(recipe instanceof RecipeCarpentry){
//				reg.remove(rec.getKey());
//				RegistryHandler.Remove(CraftingManager.REGISTRY, rec.getKey(), recipe);
//			}
//		}
//		for(RecipeCarpentry recipe : globalRecipes.values()){
//			if(recipe.isValid()){
//				if(recipe.getRegistryName() == null)
//					recipe.setRegistryName(new ResourceLocation("customnpcs", recipe.id + ""));
//				reg.register(recipe);
//			}
//		}
//		reg.freeze();
	}
	
	private void loadCategories(){
		File saveDir = CustomNpcs.getWorldSaveDirectory();
		try {
	        File file = new File(saveDir, "recipes.dat");
	        if(file.exists()){
		        loadCategories(file);
	        }
	        else{
	    		globalRecipes.clear();
	    		anvilRecipes.clear();
	    		loadDefaultRecipes(-1);
	        }
		} catch (Exception e) {
			e.printStackTrace();
			try {
		        File file = new File(saveDir, "recipes.dat_old");
		        if(file.exists()){
		        	loadCategories(file);
		        }
			} catch (Exception ee) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadDefaultRecipes(int i) {
		if(i == version)
			return;
		RecipesDefault.loadDefaultRecipes(i);	
		saveCategories();
	}

	private void loadCategories(File file) throws Exception{
//        CompoundNBT nbttagcompound1 = CompressedStreamTools.readCompressed(new FileInputStream(file));
//        nextId = nbttagcompound1.getInt("LastId");
//        ListNBT list = nbttagcompound1.getList("Data", 10);
//        HashMap<ResourceLocation,RecipeCarpentry> globalRecipes = new HashMap<ResourceLocation, RecipeCarpentry>();
//        HashMap<ResourceLocation,RecipeCarpentry> anvilRecipes = new HashMap<ResourceLocation, RecipeCarpentry>();
//        if(list != null){
//            for(int i = 0; i < list.size(); i++)
//            {
//        		RecipeCarpentry recipe = RecipeCarpentry.load(list.getCompound(i));
//            	if(recipe.isGlobal)
//            		globalRecipes.put(recipe.getId(),recipe);
//            	else
//            		anvilRecipes.put(recipe.getId(),recipe);
//            	if(recipe.id > nextId)
//            		nextId = recipe.id;
//            }
//        }
//        this.anvilRecipes = anvilRecipes;
//        this.globalRecipes = globalRecipes;
//		loadDefaultRecipes(nbttagcompound1.getInt("Version"));
	}
	
	private void saveCategories(){
		try {
			File saveDir = CustomNpcs.getWorldSaveDirectory();
	        ListNBT list = new ListNBT();
	        for(RecipeCarpentry recipe : globalRecipes.values()){
	        	if(recipe.savesRecipe)
	        		list.add(recipe.writeNBT());
	        }
	        for(RecipeCarpentry recipe : anvilRecipes.values()){
	        	if(recipe.savesRecipe)
	        		list.add(recipe.writeNBT());
	        }
	        CompoundNBT nbttagcompound = new CompoundNBT();
	        nbttagcompound.put("Data", list);
	        nbttagcompound.putInt("LastId", nextId);
	        nbttagcompound.putInt("Version", version);
            File file = new File(saveDir, "recipes.dat_new");
            File file1 = new File(saveDir, "recipes.dat_old");
            File file2 = new File(saveDir, "recipes.dat");
            CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file));
            if(file1.exists())
            {
                file1.delete();
            }
            file2.renameTo(file1);
            if(file2.exists())
            {
                file2.delete();
            }
            file.renameTo(file2);
            if(file.exists())
            {
                file.delete();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RecipeCarpentry findMatchingRecipe(CraftingInventory inventoryCrafting) {
    	for(RecipeCarpentry recipe : anvilRecipes.values()){
			if (recipe.isValid() && recipe.matches(inventoryCrafting, null))
    			return recipe;
    	}
    	return null;
    }

	public RecipeCarpentry getRecipe(int id) {
		if(globalRecipes.containsKey(id))
			return globalRecipes.get(id);
		if(anvilRecipes.containsKey(id))
			return anvilRecipes.get(id);
		return null;
	}

	public RecipeCarpentry saveRecipe(RecipeCarpentry recipe) {
//		RecipeCarpentry current = getRecipe(recipe.id);
//		if(current != null && !current.name.equals(recipe.name)){
//			while(containsRecipeName(recipe.name))
//				recipe.name += "_";
//		}
//
//		if(recipe.id == -1){
//			recipe.id = getUniqueId();
//			while(containsRecipeName(recipe.name))
//				recipe.name += "_";
//		}
//		if(recipe.isGlobal){
//			anvilRecipes.remove(recipe.id);
//			globalRecipes.put(recipe.id, recipe);
//			Packets.sendAll(new PacketSyncUpdate(recipe.id, SyncType.RECIPE_NORMAL, recipe.writeNBT()));
//		}
//		else{
//			globalRecipes.remove(recipe.id);
//			anvilRecipes.put(recipe.id, recipe);
//			Packets.sendAll(new PacketSyncUpdate(recipe.id, SyncType.RECIPE_CARPENTRY, recipe.writeNBT()));
//		}
//		saveCategories();
//		reloadGlobalRecipes();
//
//		return recipe;
		return null;
	}

	private int getUniqueId() {
		nextId++;
		return nextId;
	}
	
	private boolean containsRecipeName(String name) {
//		name = name.toLowerCase();
//		for(RecipeCarpentry recipe : globalRecipes.values()){
//			if(recipe.name.toLowerCase().equals(name))
//				return true;
//		}
//		for(RecipeCarpentry recipe : anvilRecipes.values()){
//			if(recipe.name.toLowerCase().equals(name))
//				return true;
//		}
		return false;
	}

	public RecipeCarpentry delete(int id) {
		RecipeCarpentry recipe = getRecipe(id);
        //		globalRecipes.remove(recipe.id);
//		anvilRecipes.remove(recipe.id);
//		if(recipe.isGlobal)
//			Packets.sendAll(new PacketSyncRemove(id, SyncType.RECIPE_NORMAL));
//		else
//			Packets.sendAll(new PacketSyncRemove(id, SyncType.RECIPE_CARPENTRY));
//		saveCategories();
//		reloadGlobalRecipes();
//		recipe.id = -1;
		return recipe;
	}

	@Override
	public List<IRecipe> getGlobalList() {
		return new ArrayList<IRecipe>(globalRecipes.values());
	}

	@Override
	public List<IRecipe> getCarpentryList() {
		return new ArrayList<IRecipe>(anvilRecipes.values());
	}

	@Override
	public IRecipe addRecipe(String name, boolean global, ItemStack result, Object... objects) {
//		RecipeCarpentry recipe = new RecipeCarpentry(name);
//		recipe.isGlobal = global;
//		recipe = RecipeCarpentry.createRecipe(recipe, result, objects);
//		return saveRecipe(recipe);
		return null;
	}

	@Override
	public IRecipe addRecipe(String name, boolean global, ItemStack result, int width, int height, ItemStack... objects){
		NonNullList<Ingredient> list = NonNullList.create();
		for(ItemStack item : objects){
			if(!item.isEmpty())
				list.add(Ingredient.of(item));
		}
//		RecipeCarpentry recipe = new RecipeCarpentry(width, height, list, result);
//		recipe.isGlobal = global;
//		recipe.name = name;
//		return saveRecipe(recipe);
		return null;
	}
}
