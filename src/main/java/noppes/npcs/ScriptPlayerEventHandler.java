package noppes.npcs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.event.ItemEvent;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemNbtBook;
import noppes.npcs.items.ItemScripted;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketItemUpdate;
import noppes.npcs.shared.common.util.LogWriter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ScriptPlayerEventHandler {
	
	@SubscribeEvent
	public void onServerTick(TickEvent.PlayerTickEvent event){
		if(event.side != LogicalSide.SERVER || event.phase != Phase.START)
			return;
		PlayerEntity player = event.player;
		PlayerData data = PlayerData.get(player);
		
		if(player.tickCount % 10 == 0){
			EventHooks.onPlayerTick(data.scriptData);
			for(int i = 0; i < player.inventory.getContainerSize(); i++){
				ItemStack item = player.inventory.getItem(i);
				if(!item.isEmpty() && item.getItem() == CustomItems.scripted_item){
					ItemScriptedWrapper isw = (ItemScriptedWrapper) NpcAPI.Instance().getIItemStack(item);
					EventHooks.onScriptItemUpdate(isw, player);
					if(isw.updateClient) {
						isw.updateClient = false;
						Packets.send((ServerPlayerEntity)player, new PacketItemUpdate(i, isw.getMCNbt()));
					}
				}
			}
		}
		if(data.playerLevel != player.experienceLevel) {
			EventHooks.onPlayerLevelUp(data.scriptData, data.playerLevel - player.experienceLevel);
			data.playerLevel = player.experienceLevel;
		}
		data.timers.update();
	}

	@SubscribeEvent
	public void invoke(PlayerInteractEvent.LeftClickBlock event) {
		if(event.getPlayer().level.isClientSide || event.getHand() != Hand.MAIN_HAND || !(event.getWorld() instanceof ServerWorld))
			return;
		if(event.getItemStack().getItem() == CustomItems.teleporter) {
			event.setCanceled(true);
			return;
		}

		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		PlayerEvent.AttackEvent ev = new PlayerEvent.AttackEvent(handler.getPlayer(), 2, NpcAPI.Instance().getIBlock(event.getWorld(), event.getPos()));
		event.setCanceled(EventHooks.onPlayerAttack(handler, ev));
		
		if(event.getItemStack().getItem() == CustomItems.scripted_item && !event.isCanceled()) {
			ItemScriptedWrapper isw = ItemScripted.GetWrapper(event.getItemStack());
			ItemEvent.AttackEvent eve = new ItemEvent.AttackEvent(isw, handler.getPlayer(), 2, NpcAPI.Instance().getIBlock(event.getWorld(), event.getPos()));
			eve.setCanceled(event.isCanceled());
			event.setCanceled(EventHooks.onScriptItemAttack(isw, eve));
		}
	}
	
	@SubscribeEvent
	public void invoke(PlayerInteractEvent.RightClickBlock event) {
		if(event.getPlayer().level.isClientSide || event.getHand() != Hand.MAIN_HAND || !(event.getWorld() instanceof ServerWorld))
			return;
		if(event.getItemStack().getItem() == CustomItems.nbt_book) {
			((ItemNbtBook)event.getItemStack().getItem()).blockEvent(event);
			event.setCanceled(true);
			return;
		}
		if(event.getItemStack().getItem() == CustomItems.teleporter) {
			event.setCanceled(true);
			return;
		}
		
		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		handler.hadInteract = true;
		PlayerEvent.InteractEvent ev = new PlayerEvent.InteractEvent(handler.getPlayer(), 2, NpcAPI.Instance().getIBlock(event.getWorld(), event.getPos()));
		event.setCanceled(EventHooks.onPlayerInteract(handler, ev));
		
		if(event.getItemStack().getItem() == CustomItems.scripted_item && !event.isCanceled()) {
			ItemScriptedWrapper isw = ItemScripted.GetWrapper(event.getItemStack());
			ItemEvent.InteractEvent eve = new ItemEvent.InteractEvent(isw, handler.getPlayer(), 2, NpcAPI.Instance().getIBlock(event.getWorld(), event.getPos()));
			event.setCanceled(EventHooks.onScriptItemInteract(isw, eve));
		}
	}

	@SubscribeEvent
	public void invoke(PlayerInteractEvent.EntityInteract event) {
		if(event.getPlayer().level.isClientSide || event.getHand() != Hand.MAIN_HAND || !(event.getWorld() instanceof ServerWorld))
			return;
		if(event.getItemStack().getItem() == CustomItems.nbt_book) {
			((ItemNbtBook)event.getItemStack().getItem()).entityEvent(event);
			event.setCanceled(true);
			return;
		}
		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		PlayerEvent.InteractEvent ev = new PlayerEvent.InteractEvent(handler.getPlayer(), 1, NpcAPI.Instance().getIEntity(event.getTarget()));
		event.setCanceled(EventHooks.onPlayerInteract(handler, ev));
		
		if(event.getItemStack().getItem() == CustomItems.scripted_item && !event.isCanceled()) {
			ItemScriptedWrapper isw = ItemScripted.GetWrapper(event.getItemStack());
			ItemEvent.InteractEvent eve = new ItemEvent.InteractEvent(isw, handler.getPlayer(), 1, NpcAPI.Instance().getIEntity(event.getTarget()));
			event.setCanceled(EventHooks.onScriptItemInteract(isw, eve));
		}
	}
	
	@SubscribeEvent
	public void invoke(PlayerInteractEvent.RightClickItem event) {
		if(event.getPlayer().level.isClientSide || event.getHand() != Hand.MAIN_HAND || !(event.getWorld() instanceof ServerWorld))
			return;

		if(event.getPlayer().isCreative() && event.getPlayer().isCrouching() && event.getItemStack().getItem() == CustomItems.scripted_item){
			NoppesUtilServer.sendOpenGui(event.getPlayer(), EnumGuiType.ScriptItem, null);
			return;
		}
		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		if(handler.hadInteract) {
			handler.hadInteract = false;
			return;
		}
		PlayerEvent.InteractEvent ev = new PlayerEvent.InteractEvent(handler.getPlayer(), 0, null);
		event.setCanceled(EventHooks.onPlayerInteract(handler, ev));
		
		if(event.getItemStack().getItem() == CustomItems.scripted_item && !event.isCanceled()) {
			ItemScriptedWrapper isw = ItemScripted.GetWrapper(event.getItemStack());
			ItemEvent.InteractEvent eve = new ItemEvent.InteractEvent(isw, handler.getPlayer(), 0, null);
			event.setCanceled(EventHooks.onScriptItemInteract(isw, eve));
		}
	}

	@SubscribeEvent
	public void invoke(ArrowLooseEvent event){
		if(event.getPlayer().level.isClientSide || !(event.getWorld() instanceof ServerWorld))
			return;
		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		PlayerEvent.RangedLaunchedEvent ev = new PlayerEvent.RangedLaunchedEvent(handler.getPlayer());
		event.setCanceled(EventHooks.onPlayerRanged(handler, ev));
	}

	@SubscribeEvent
	public void invoke(BlockEvent.BreakEvent event) {
		if(event.getPlayer().level.isClientSide || !(event.getWorld() instanceof ServerWorld))
			return;
		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		PlayerEvent.BreakEvent ev = new PlayerEvent.BreakEvent(handler.getPlayer(), 
				NpcAPI.Instance().getIBlock((ServerWorld)event.getWorld(), event.getPos()), event.getExpToDrop());
		event.setCanceled(EventHooks.onPlayerBreak(handler, ev));
		event.setExpToDrop(ev.exp);
	}

	@SubscribeEvent
	public void invoke(ItemTossEvent event) {
		if(!(event.getPlayer().level instanceof ServerWorld))
			return;
		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		event.setCanceled(EventHooks.onPlayerToss(handler, event.getEntityItem()));
	}

	@SubscribeEvent
	public void invoke(EntityItemPickupEvent event) {
		if(!(event.getPlayer().level instanceof ServerWorld))
			return;
		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		event.setCanceled(EventHooks.onPlayerPickUp(handler, event.getItem()));
	}

	@SubscribeEvent
	public void invoke(PlayerContainerEvent.Open event) {
		if(!(event.getPlayer().level instanceof ServerWorld))
			return;
		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		EventHooks.onPlayerContainerOpen(handler, event.getContainer());
	}

	@SubscribeEvent
	public void invoke(PlayerContainerEvent.Close event) {
		if(!(event.getPlayer().level instanceof ServerWorld))
			return;
		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		EventHooks.onPlayerContainerClose(handler, event.getContainer());
	}

	@SubscribeEvent
	public void invoke(LivingDeathEvent event) {
		if(!(event.getEntityLiving().level instanceof ServerWorld))
			return;
		Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());
		if(event.getEntityLiving() instanceof PlayerEntity){
			PlayerScriptData handler = PlayerData.get((PlayerEntity) event.getEntityLiving()).scriptData;
			EventHooks.onPlayerDeath(handler, event.getSource(), source);
		}
		if(source instanceof PlayerEntity){
			PlayerScriptData handler = PlayerData.get((PlayerEntity) source).scriptData;
			EventHooks.onPlayerKills(handler, event.getEntityLiving());
		}
	}

	@SubscribeEvent
	public void invoke(LivingHurtEvent event) {
		if(!(event.getEntityLiving().level instanceof ServerWorld))
			return;
		Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());
		if(event.getEntityLiving() instanceof PlayerEntity){
			PlayerScriptData handler = PlayerData.get((PlayerEntity) event.getEntityLiving()).scriptData;
			PlayerEvent.DamagedEvent pevent = new PlayerEvent.DamagedEvent(handler.getPlayer(), source, event.getAmount(), event.getSource());
			event.setCanceled(EventHooks.onPlayerDamaged(handler, pevent));		
			event.setAmount(pevent.damage);
		}
		
		if(source instanceof PlayerEntity){
			PlayerScriptData handler = PlayerData.get((PlayerEntity) source).scriptData;
			PlayerEvent.DamagedEntityEvent pevent = new PlayerEvent.DamagedEntityEvent(handler.getPlayer(), event.getEntityLiving(), event.getAmount(), event.getSource());
			event.setCanceled(EventHooks.onPlayerDamagedEntity(handler, pevent));		
			event.setAmount(pevent.damage);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void invoke(LivingAttackEvent event) {
		if(!(event.getEntityLiving().level instanceof ServerWorld))
			return;
		Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());
		if(source instanceof PlayerEntity){
			PlayerScriptData handler = PlayerData.get((PlayerEntity) source).scriptData;			
			ItemStack item = ((PlayerEntity) source).getMainHandItem();
			IEntity target = NpcAPI.Instance().getIEntity(event.getEntityLiving());
			IDamageSource damageSource = NpcAPI.Instance().getIDamageSource(event.getSource());
			PlayerEvent.AttackEvent ev = new PlayerEvent.AttackEvent(handler.getPlayer(), target, damageSource);
			event.setCanceled(EventHooks.onPlayerAttack(handler, ev));
			if(item.getItem() == CustomItems.scripted_item && !event.isCanceled()) {
				ItemScriptedWrapper isw = ItemScripted.GetWrapper(item);
				ItemEvent.AttackEvent eve = new ItemEvent.AttackEvent(isw, handler.getPlayer(), target, damageSource);
				eve.setCanceled(event.isCanceled());
				event.setCanceled(EventHooks.onScriptItemAttack(isw, eve));
			}
		}
	}

	@SubscribeEvent
	public void invoke(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
		if(!(event.getPlayer().level instanceof ServerWorld))
			return;
		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		EventHooks.onPlayerLogin(handler);
	}

	@SubscribeEvent
	public void invoke(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
		if(!(event.getPlayer().level instanceof ServerWorld))
			return;
		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		EventHooks.onPlayerLogout(handler);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void invoke(ServerChatEvent event) {
		if(!(event.getPlayer().level instanceof ServerWorld) || event.getPlayer() == EntityNPCInterface.ChatEventPlayer)
			return; 
		PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
		String message = event.getMessage();
		PlayerEvent.ChatEvent ev = new PlayerEvent.ChatEvent(handler.getPlayer(), event.getMessage());
		EventHooks.onPlayerChat(handler, ev);
		event.setCanceled(ev.isCanceled());
		if(!message.equals(ev.message)) {
			TranslationTextComponent chat = new TranslationTextComponent("");
			chat.append(ForgeHooks.newChatWithLinks(ev.message));
			event.setComponent(chat);
			
		}
	}

	public ScriptPlayerEventHandler registerForgeEvents() {
		ForgeEventHandler.eventNames.clear();
		ForgeEventHandler handler = new ForgeEventHandler();
		try {
			Method m = handler.getClass().getMethod("forgeEntity", Event.class);
			Method register = MinecraftForge.EVENT_BUS.getClass().getDeclaredMethod("register", Class.class, Object.class, Method.class);
			register.setAccessible(true);

			HashSet<Class> classes = new HashSet<>();
			Field f = ClassLoader.class.getDeclaredField("classes");
			f.setAccessible(true);
			ClassLoader loader = PlayerContainerEvent.Open.class.getClassLoader();
			while(loader != null){
				for(Class c : new ArrayList<>((List<Class>) f.get(loader))) {
					if(!Event.class.isAssignableFrom(c)){
						continue;
					}
					try{
						if(c.getDeclaredClasses().length > 0){
							classes.addAll(Arrays.asList(c.getDeclaredClasses()));
						}
						else {
							classes.add(c);
						}
					}
					catch(Throwable t){
					}
				}
				loader = loader.getParent();
			}

			for(Class c : classes) {
				String name = c.getName();
				if(name.startsWith("net.minecraftforge.event.terraingen") || !c.getName().startsWith("net.minecraftforge.event")) {
					continue;
				}
				try{
					if(GenericEvent.class.isAssignableFrom(c) || EntityEvent.EntityConstructing.class.isAssignableFrom(c) || WorldEvent.PotentialSpawns.class.isAssignableFrom(c)
							|| TickEvent.RenderTickEvent.class.isAssignableFrom(c) || TickEvent.ClientTickEvent.class.isAssignableFrom(c)
							|| NetworkEvent.ClientCustomPayloadEvent.class.isAssignableFrom(c) || ItemTooltipEvent.class.isAssignableFrom(c)) {
						continue;
					}
					if(Event.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers()) && Modifier.isPublic(c.getModifiers())) {
						String eventName = ForgeEventHandler.getEventName(c);
						if(!ForgeEventHandler.eventNames.contains(eventName)){
							register.invoke(MinecraftForge.EVENT_BUS, c, handler, m);
							ForgeEventHandler.eventNames.add(eventName);
						}
					}
				}
				catch(Throwable t){
					LogWriter.except(t);
				}
			}
			
			if(PixelmonHelper.Enabled) {
		        try {
		            for(Class c : classes){
		            	if(c.getName().startsWith("com.pixelmonmod.pixelmon.api.events")) {
							if(Event.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers()) && Modifier.isPublic(c.getModifiers())) {
								ForgeEventHandler.eventNames.add(ForgeEventHandler.getEventName(c));
								register.invoke(PixelmonHelper.EVENT_BUS, c, handler, m);
							}
		            	}
		            }
		        } catch (Throwable e) {
					LogWriter.except(e);
		        }
			}
		} catch (Throwable e) {
			LogWriter.except(e);
		}
		return this;
	}
}
