package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleTrader;


public class ContainerNPCTrader extends ContainerNpcInterface{
	public RoleTrader role;
	private final EntityNPCInterface npc;

    public ContainerNPCTrader(int containerId, PlayerInventory playerInventory, int entityId){
        super(CustomContainer.container_trader, containerId, playerInventory);
        npc = (EntityNPCInterface) playerInventory.player.level.getEntity(entityId);
        role = (RoleTrader) npc.role;

        for(int i = 0; i < 18; i++){
        	int x =  53;
        	x += i%3 * 72;
        	int y = 7;
        	y += i/3 * 21;

			addSlot(new Slot(role.inventorySold, i, x, y));
        }

        for(int i1 = 0; i1 < 3; i1++){
            for(int l1 = 0; l1 < 9; l1++){
            	addSlot(new Slot(playerInventory, l1 + i1 * 9 + 9, 32 + l1 * 18, 140 + i1 * 18));
            }

        }

        for(int j1 = 0; j1 < 9; j1++){
        	addSlot(new Slot(playerInventory, j1, 32 + j1 * 18, 198));
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity par1PlayerEntity, int i){
        return ItemStack.EMPTY;
    }
    
    @Override
    public ItemStack clicked(int i, int j, ClickType par3, PlayerEntity entityplayer){
    	if( par3 != ClickType.PICKUP) {
            return ItemStack.EMPTY;
        }
    	if(i < 0 || i >= 18)
        	return super.clicked(i, j, par3, entityplayer);
		if(j == 1)
			return ItemStack.EMPTY;
        Slot slot = slots.get(i);
        if(slot == null || slot.getItem() == null || slot.getItem().isEmpty())
        	return ItemStack.EMPTY;
        
        ItemStack item = slot.getItem();
        if(!canGivePlayer(item, entityplayer))
        	return ItemStack.EMPTY;

		ItemStack currency = role.inventoryCurrency.getItem(i);
		ItemStack currency2 = role.inventoryCurrency.getItem(i + 18);
        if(!canBuy(currency, currency2, entityplayer)){
            RoleEvent.TradeFailedEvent event = new RoleEvent.TradeFailedEvent(entityplayer, npc.wrappedNPC, item, currency, currency2);
            EventHooks.onNPCRole(npc, event);
        	return event.receiving == null?ItemStack.EMPTY:event.receiving.getMCItemStack();
        }
        
        RoleEvent.TraderEvent event = new RoleEvent.TraderEvent(entityplayer, npc.wrappedNPC, item, currency, currency2);
        
        if(EventHooks.onNPCRole(npc, event))
			return ItemStack.EMPTY;        

        if(event.currency1 != null && !event.currency1.isEmpty())
        	currency = event.currency1.getMCItemStack();
        if(event.currency2 != null && !event.currency2.isEmpty())
        	currency2 = event.currency2.getMCItemStack();
        
        if(!canBuy(currency, currency2, entityplayer))
        	return ItemStack.EMPTY;
        
        NoppesUtilPlayer.consumeItem(entityplayer, currency, role.ignoreDamage, role.ignoreNBT);
        NoppesUtilPlayer.consumeItem(entityplayer, currency2, role.ignoreDamage, role.ignoreNBT);
        
        
        ItemStack soldItem = ItemStack.EMPTY;
        if(event.sold != null && !event.sold.isEmpty()){
        	soldItem = event.sold.getMCItemStack();
            givePlayer(soldItem.copy(), entityplayer);
        }
        return soldItem;
    }
    public boolean canBuy(ItemStack currency, ItemStack currency2, PlayerEntity player) {
		if(NoppesUtilServer.IsItemStackNull(currency) && NoppesUtilServer.IsItemStackNull(currency2))
			return true;
		
		if(NoppesUtilServer.IsItemStackNull(currency)){
			currency = currency2;
			currency2 = ItemStack.EMPTY;
		}
		if(NoppesUtilPlayer.compareItems(currency, currency2, role.ignoreDamage, role.ignoreNBT)){
			currency = currency.copy();
			currency.grow(currency2.getCount());
			currency2 = ItemStack.EMPTY;
		}
		if(NoppesUtilServer.IsItemStackNull(currency2))
			return NoppesUtilPlayer.compareItems(player, currency, role.ignoreDamage, role.ignoreNBT);
		return NoppesUtilPlayer.compareItems(player, currency, role.ignoreDamage, role.ignoreNBT) && NoppesUtilPlayer.compareItems(player, currency2, role.ignoreDamage, role.ignoreNBT);
		
    }

	private boolean canGivePlayer(ItemStack item,PlayerEntity entityplayer){
        ItemStack itemstack3 = entityplayer.inventory.getCarried();
        if(NoppesUtilServer.IsItemStackNull(itemstack3)){
        	return true;
        }
        else if(NoppesUtilPlayer.compareItems(itemstack3, item, false, false)){
            int k1 = item.getCount();
            return k1 > 0 && k1 + itemstack3.getCount() <= itemstack3.getMaxStackSize();
        }
        return false;
    }
    private void givePlayer(ItemStack item,PlayerEntity entityplayer){
        ItemStack itemstack3 = entityplayer.inventory.getCarried();
        if(NoppesUtilServer.IsItemStackNull(itemstack3)){
        	entityplayer.inventory.setCarried(item);
        }
        else if(NoppesUtilPlayer.compareItems(itemstack3, item, false, false)){

            int k1 = item.getCount();
            if(k1 > 0 && k1 + itemstack3.getCount() <= itemstack3.getMaxStackSize())
            {
            	itemstack3.grow(k1);
            }
        }
    }
}
