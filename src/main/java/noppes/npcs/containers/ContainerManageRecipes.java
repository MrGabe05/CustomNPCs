package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomContainer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.data.RecipeCarpentry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContainerManageRecipes extends Container {
    private final Inventory craftingMatrix;
    public RecipeCarpentry recipe;
    public int size;
    public int width;
    private final boolean init = false;
    public ContainerManageRecipes(int containerId, PlayerInventory playerInventory, int size) {
    	super(CustomContainer.container_managerecipes, containerId);
    	this.size = size * size;
    	this.width = size;
    	craftingMatrix = new Inventory( this.size + 1);
    	recipe = new RecipeCarpentry(new ResourceLocation(""), "");
    	addSlot(new Slot(craftingMatrix,0,87,61));

    	for(int i = 0; i < size;i++){
        	for(int j = 0; j < size;j++){
            	addSlot(new Slot(craftingMatrix, i * width + j + 1,j*18 + 8, i*18 + 35));
        	}
    	}
        
        for(int i1 = 0; i1 < 3; i1++)
        {
            for(int l1 = 0; l1 < 9; l1++)
            {
            	addSlot(new Slot(playerInventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 113 + i1 * 18));
            }

        }

        for(int j1 = 0; j1 < 9; j1++)
        {
        	addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 171));
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity par1PlayerEntity, int i)
    {
        return ItemStack.EMPTY;
    }
	@Override
	public boolean stillValid(PlayerEntity entityplayer) {
		return true;
	}
	
	public void setRecipe(RecipeCarpentry recipe) {
    	craftingMatrix.setItem(0,recipe.getResultItem());
    	for(int i = 0; i < width;i++){
        	for(int j = 0; j < width;j++){
        		if(j >= recipe.getRecipeWidth())
        			craftingMatrix.setItem(i*width + j + 1, ItemStack.EMPTY);
        		else
        			craftingMatrix.setItem(i*width + j + 1, recipe.getCraftingItem(i * recipe.getRecipeWidth() + j));
        	}
    	}
    	this.recipe = recipe;
	}
	public void saveRecipe(){
		int nextChar = 0;
		char[] chars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P'};
		Map<ItemStack,Character> nameMapping = new HashMap<ItemStack,Character>();
		int firstRow = width,lastRow = 0,firstColumn = width,lastColumn = 0;
		boolean seenRow = false;
    	for(int i = 0; i < width;i++){
    		boolean seenColumn = false;
        	for(int j = 0; j < width;j++){
	        	ItemStack item = craftingMatrix.getItem(i * width + j + 1);
	        	if(NoppesUtilServer.IsItemStackNull(item)){
	        		continue;
	        	}
	        	
        		if(!seenColumn && j < firstColumn){
        			firstColumn = j;
        		}
        		if(j > lastColumn)
        			lastColumn = j;
        		
        		seenColumn = true;
	        	
	        	Character letter = null;
	        	for(ItemStack mapped : nameMapping.keySet()){
	        		if(NoppesUtilPlayer.compareItems(mapped, item, recipe.ignoreDamage, recipe.ignoreNBT))
	        			letter = nameMapping.get(mapped);
	        	}
	        	if(letter == null){
	        		letter = chars[nextChar];
	        		nextChar++;
	        		nameMapping.put(item, letter);
	        	}
        	}
        	if(seenColumn){
        		if(!seenRow){
	        		firstRow = i;
	        		lastRow = i;
	        		seenRow = true;
        		}
        		else{
        			lastRow = i;
        		}
        	}
        	
    	}    	
		ArrayList<Object> recipe = new ArrayList<Object>();

    	for(int i = 0; i < width;i++){
    		if(i < firstRow || i > lastRow)
    			continue;
    		String row = "";
        	for(int j = 0; j < width;j++){
        		if(j < firstColumn|| j > lastColumn)
        			continue;
	        	ItemStack item = craftingMatrix.getItem(i * width + j + 1);
	        	if(NoppesUtilServer.IsItemStackNull(item)){
	        		row += " ";
	        		continue;
	        	}

	        	for(ItemStack mapped : nameMapping.keySet()){
	        		if(NoppesUtilPlayer.compareItems(mapped, item, false, false)){
	        			row += nameMapping.get(mapped);
	        		}
	        	}
	        	
        	}
        	recipe.add(row);
    	}
		if(nameMapping.isEmpty()){
			RecipeCarpentry r = new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, this.recipe.name), this.recipe.name);
			r.copy(this.recipe);
			this.recipe = r;
			return;
		}
    	for(ItemStack mapped : nameMapping.keySet()){
    		Character letter = nameMapping.get(mapped);
    		recipe.add(letter);
    		recipe.add(mapped);
    	}
    	this.recipe = RecipeCarpentry.createRecipe(new ResourceLocation(CustomNpcs.MODID, this.recipe.name), this.recipe, craftingMatrix.getItem(0), recipe.toArray());
	}
}
