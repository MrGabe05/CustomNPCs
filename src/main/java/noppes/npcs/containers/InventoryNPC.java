package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import noppes.npcs.NoppesUtilServer;

public class InventoryNPC implements IInventory{
    private final String inventoryTitle;
    private final int slotsCount;
    public final NonNullList<ItemStack> inventoryContents;
    private final Container con;

    public InventoryNPC(String s, int i,Container con){
    	this.con = con;
        inventoryTitle = s;
        slotsCount = i;
        inventoryContents = NonNullList.withSize(i, ItemStack.EMPTY);
    }
    
    @Override
    public ItemStack getItem(int i){
        return inventoryContents.get(i);
    }

    @Override
    public ItemStack removeItem(int index, int count){
        return ItemStackHelper.removeItem(inventoryContents, index, count);
    }

    @Override
    public void setItem(int index, ItemStack stack){
        this.inventoryContents.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize())
        {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public int getContainerSize(){
        return slotsCount;
    }
    
    @Override
    public int getMaxStackSize(){
        return 64;
    }

    @Override
    public boolean stillValid(PlayerEntity entityplayer){
        return false;
    }


	@Override
	public ItemStack removeItemNoUpdate(int i) {
		return ItemStackHelper.takeItem(inventoryContents, i);
	}
	
	@Override
	public boolean canPlaceItem(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public void setChanged() {
    	con.slotsChanged(this);
	}

	@Override
	public void startOpen(PlayerEntity player) {
		
	}
	@Override
	public void stopOpen(PlayerEntity player) {
		
	}

	@Override
    public boolean isEmpty(){
        for (int slot = 0; slot < this.getContainerSize(); slot++){
        	ItemStack item = getItem(slot);
            if (!NoppesUtilServer.IsItemStackNull(item) && !item.isEmpty()){
                return false;
            }
        }
        return true;
    }

    @Override
    public void clearContent() {

    }
}
