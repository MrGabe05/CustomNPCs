package noppes.npcs;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import noppes.npcs.items.ItemScripted;

public class ScriptItemEventHandler {

	@SubscribeEvent
	public void invoke(EntityJoinWorldEvent event) {
		if(event.getWorld().isClientSide || !(event.getEntity() instanceof ItemEntity))
			return;
		
		ItemEntity entity = (ItemEntity) event.getEntity();
		ItemStack stack = entity.getItem();
		if(!stack.isEmpty() && stack.getItem() == CustomItems.scripted_item) {
			if(EventHooks.onScriptItemSpawn(ItemScripted.GetWrapper(stack), entity)) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void invoke(ItemTossEvent event) {
		if(event.getPlayer().level.isClientSide)
			return;
		
		ItemEntity entity = event.getEntityItem();
		ItemStack stack = entity.getItem();
		if(!stack.isEmpty() && stack.getItem() == CustomItems.scripted_item) {
			if(EventHooks.onScriptItemTossed(ItemScripted.GetWrapper(stack), event.getPlayer(), entity)) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void invoke(EntityItemPickupEvent event) {
		if(event.getPlayer().level.isClientSide)
			return;
		ItemEntity entity = event.getItem();
		ItemStack stack = entity.getItem();
		if(!stack.isEmpty() && stack.getItem() == CustomItems.scripted_item) {
			EventHooks.onScriptItemPickedUp(ItemScripted.GetWrapper(stack), event.getPlayer(), entity);
		}		
	}
}
