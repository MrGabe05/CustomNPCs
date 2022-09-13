package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import noppes.npcs.NoppesUtilServer;

public class InventoryNpcTrader implements IInventory{

    private final String inventoryTitle;
    private final int slotsCount;
    public final NonNullList<ItemStack> inventoryContents;
    private final ContainerNPCTrader con;

    public InventoryNpcTrader(String s, int i,ContainerNPCTrader con){
    	this.con = con;
        inventoryTitle = s;
        slotsCount = i;
        inventoryContents = NonNullList.withSize(i, ItemStack.EMPTY);
    }
    
    @Override
    public ItemStack getItem(int i){
        ItemStack toBuy = inventoryContents.get(i);
        if(NoppesUtilServer.IsItemStackNull(toBuy))
        	return ItemStack.EMPTY;
        
        return toBuy.copy();
    }

    @Override
    public ItemStack removeItem(int i, int j){
        ItemStack stack = inventoryContents.get(i);
        if(!NoppesUtilServer.IsItemStackNull(stack)){
            return  stack.copy();
        }
        return ItemStack.EMPTY;

    }

    @Override
    public void setItem(int i, ItemStack itemstack){
    	if(!itemstack.isEmpty())
    		inventoryContents.set(i, itemstack.copy());
        setChanged();
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
        return true;
    }


	@Override
	public ItemStack removeItemNoUpdate(int i) {
		return inventoryContents.set(i, ItemStack.EMPTY);
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
