package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerBankData;

public class ContainerNPCBankInterface extends ContainerNpcInterface{
    public InventoryNPC currencyMatrix;
    public SlotNpcBankCurrency currency;
    public int slot = 0;
    public int bankid;
    private PlayerBankData data;
    public ContainerNPCBankInterface(ContainerType type, int containerId, PlayerInventory playerInventory, int slot, int bankid){
    	super(type, containerId, playerInventory);
    	this.bankid = bankid;
    	this.slot = slot;
    	    	
        currencyMatrix = new InventoryNPC("currency", 1, this);
        if(!isAvailable() || canBeUpgraded()){
        	currency = new SlotNpcBankCurrency(this, currencyMatrix, 0, 80, 29);
        	addSlot(currency);
        }
        
        NpcMiscInventory items = new NpcMiscInventory(54);
        if(!player.level.isClientSide){
    		data = PlayerDataController.instance.getBankData(player,bankid);
        	items = data.getBankOrDefault(bankid).itemSlots.get(slot);
        }
        
        int xOffset = xOffset();
        for (int j = 0; j < getRowNumber(); j++){
            for (int i1 = 0; i1 < 9; i1++){
            	int id = i1 + j * 9;
            	addSlot(new Slot(items, id, 8 + i1 * 18, 17 + xOffset + j * 18));
            }
        }
        
        if(isUpgraded())
        	xOffset += 54;
        for (int k = 0; k < 3; k++){
            for (int j1 = 0; j1 < 9; j1++){
            	addSlot(new Slot(playerInventory, j1 + k * 9 + 9, 8 + j1 * 18, 86 + xOffset + k * 18));
            }
        }

        for (int l = 0; l < 9; l++){
        	addSlot(new Slot(playerInventory, l, 8 + l * 18, 144 + xOffset ));
        }
    }
    
    public synchronized void setCurrency(ItemStack item){
    	currency.item = item;
    }
    
    public int getRowNumber() {
		return 0;
	}
    
	public int xOffset(){
    	return 0;
    }
	
    @Override
    public void slotsChanged(IInventory inv){
    	
    }
    
    public boolean isAvailable(){
    	return false;
    }
    
    public boolean isUpgraded(){
    	return false;
    }
    
    public boolean canBeUpgraded(){
    	return false;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity par1PlayerEntity, int i){
    	return ItemStack.EMPTY;
    }   
    
    @Override
    public void removed(PlayerEntity entityplayer){
        super.removed(entityplayer);
        if (!entityplayer.level.isClientSide){
            ItemStack var3 = this.currencyMatrix.getItem(0);
            currencyMatrix.setItem(0, ItemStack.EMPTY);
            if (!NoppesUtilServer.IsItemStackNull(var3))
            {
            	entityplayer.drop(var3, false);
            }
        }
    }
}
