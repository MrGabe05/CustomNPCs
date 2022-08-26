package noppes.npcs;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.api.wrapper.WrapperEntityData;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemSoulstoneEmpty;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiCloneOpen;
import noppes.npcs.packets.client.PacketGuiOpen;
import noppes.npcs.packets.client.PacketMarkData;
import noppes.npcs.quests.QuestKill;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

public class ServerEventsHandler {

	public static VillagerEntity Merchant;
	
	@SubscribeEvent
	public void invoke(PlayerInteractEvent.EntityInteract event) {
		ItemStack item = event.getPlayer().getMainHandItem();
		if(item == null)
			return;
		boolean isClientSide = event.getPlayer().level.isClientSide;
		boolean npcInteracted = event.getTarget() instanceof EntityNPCInterface;

		if(!isClientSide && CustomNpcs.OpsOnly && !event.getPlayer().getServer().getPlayerList().isOp(event.getPlayer().getGameProfile())){
			return;
		}
		
		if(!isClientSide && item.getItem() == CustomItems.soulstoneEmpty && event.getTarget() instanceof LivingEntity) {
			((ItemSoulstoneEmpty)item.getItem()).store((LivingEntity)event.getTarget(), item, event.getPlayer());
		}
		
		if(item.getItem() == CustomItems.wand && npcInteracted && !isClientSide){
			if (!CustomNpcsPermissions.Instance.hasPermission(event.getPlayer() , CustomNpcsPermissions.NPC_GUI)){
				return;
			}
			event.setCanceled(true);
			NoppesUtilServer.sendOpenGui(event.getPlayer(), EnumGuiType.MainMenuDisplay, (EntityNPCInterface) event.getTarget());
		}
		else if(item.getItem() == CustomItems.cloner && !isClientSide && !(event.getTarget() instanceof PlayerEntity)){
			CompoundNBT compound = new CompoundNBT();
			if(!event.getTarget().saveAsPassenger(compound))
				return;
			PlayerData data = PlayerData.get(event.getPlayer());
			ServerCloneController.Instance.cleanTags(compound);
			Packets.send((ServerPlayerEntity)event.getPlayer(), new PacketGuiCloneOpen(compound));
			//event.getPlayer().sendMessage(new StringTextComponent("Entity too big to clone"));
			data.cloned = compound;
			event.setCanceled(true);
		}
		else if(item.getItem() == CustomItems.scripter && !isClientSide && npcInteracted){
			if(!CustomNpcsPermissions.Instance.hasPermission(event.getPlayer(), CustomNpcsPermissions.NPC_GUI))
				return;
	    	NoppesUtilServer.setEditingNpc(event.getPlayer(), (EntityNPCInterface)event.getTarget());
			event.setCanceled(true);

			Packets.send((ServerPlayerEntity) event.getPlayer(), new PacketGuiOpen(EnumGuiType.Script, BlockPos.ZERO));
		}
		else if(item.getItem() == CustomItems.mount){
			if(!CustomNpcsPermissions.Instance.hasPermission(event.getPlayer(), CustomNpcsPermissions.TOOL_MOUNTER))
				return;
			PlayerData data = PlayerData.get(event.getPlayer());
			event.setCanceled(true);
			data.mounted = event.getTarget();
	    	if(isClientSide)
	    		CustomNpcs.proxy.openGui(event.getPlayer(), EnumGuiType.MobSpawnerMounter);
		}
		else if(item.getItem() == CustomItems.wand && event.getTarget() instanceof VillagerEntity){
			if(!CustomNpcsPermissions.Instance.hasPermission(event.getPlayer(), CustomNpcsPermissions.EDIT_VILLAGER))
				return;
			event.setCanceled(true);
			Merchant = (VillagerEntity)event.getTarget();
			
			if(!isClientSide){
				ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
				//TODO fix
				//NoppesUtilServer.openContainerGui(player, EnumGuiType.MerchantAdd, null, BlockPos.ZERO);
		        //MerchantRecipeList merchantrecipelist = Merchant.getRecipes(player);
	
		        //if (merchantrecipelist != null)
		        //{
	            //	Packets.send(player, new PacketMerchantList(merchantrecipelist));
		        //}
			}
		}		
	}

	@SubscribeEvent
	public void invoke(LivingDeathEvent event) {
		if(event.getEntityLiving().level.isClientSide)
			return;
		Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());
		if(source != null){			
			if(source instanceof EntityNPCInterface && event.getEntityLiving() != null){
				EntityNPCInterface npc = (EntityNPCInterface) source;
				Line line = npc.advanced.getKillLine();
				if(line != null)
					npc.saySurrounding(Line.formatTarget(line, event.getEntityLiving()));
				EventHooks.onNPCKills(npc, event.getEntityLiving());
			}
			
			PlayerEntity player = null;
			if(source instanceof PlayerEntity)
				player = (PlayerEntity) source;
			else if(source instanceof EntityNPCInterface && ((EntityNPCInterface)source).getOwner() instanceof PlayerEntity)
				player = (PlayerEntity) ((EntityNPCInterface)source).getOwner();
			if(player != null){
				doQuest(player, event.getEntityLiving(), true);
		
				if(event.getEntityLiving() instanceof EntityNPCInterface)
					doFactionPoints(player, (EntityNPCInterface)event.getEntityLiving());
			}
		}
		if(event.getEntityLiving() instanceof PlayerEntity){
			PlayerData data = PlayerData.get((PlayerEntity)event.getEntityLiving());
			data.save(false);
		}
	}

	private void doFactionPoints(PlayerEntity player, EntityNPCInterface npc) {
		npc.advanced.factions.addPoints(player);
	}

	private void doQuest(PlayerEntity player, LivingEntity entity, boolean all) {
		PlayerData pdata = PlayerData.get(player);
		PlayerQuestData playerdata = pdata.questData;
		String entityName = entity.getType().getRegistryName().toString();
		if(entity instanceof PlayerEntity)
			entityName = "Player";
		
		for(QuestData data : playerdata.activeQuests.values()){
			if(data.quest.type != QuestType.KILL && data.quest.type != QuestType.AREA_KILL)
				continue;
			if(data.quest.type == QuestType.AREA_KILL && all){
				List<PlayerEntity> list = player.level.getEntitiesOfClass(PlayerEntity.class, entity.getBoundingBox().inflate(10, 10, 10));
				for(PlayerEntity pl : list)
					if(pl != player)
						doQuest(pl, entity, false);
			
			}
			String name = entityName;
			QuestKill quest = (QuestKill) data.quest.questInterface;
			if(quest.targets.containsKey(entity.getName().getString()))
				name = entity.getName().getString();
			else if(!quest.targets.containsKey(name))
				continue;
			HashMap<String, Integer> killed = quest.getKilled(data);
			if(killed.containsKey(name) && killed.get(name) >= quest.targets.get(name))
				continue;
			int amount = 0;
			if(killed.containsKey(name))
				amount = killed.get(name);
			killed.put(name, amount + 1);
			quest.setKilled(data, killed);
			pdata.updateClient = true;
		}
		playerdata.checkQuestCompletion(player, QuestType.KILL);
		playerdata.checkQuestCompletion(player, QuestType.AREA_KILL);
	}
	
	@SubscribeEvent
	public void world(EntityJoinWorldEvent event){
		if(event.getWorld().isClientSide || !(event.getEntity() instanceof PlayerEntity))
			return;
		PlayerData data = PlayerData.get((PlayerEntity) event.getEntity());
		data.updateCompanion(event.getWorld());
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void attachEntity(AttachCapabilitiesEvent<Entity> event){		
		if(event.getObject() instanceof PlayerEntity)
			PlayerData.register(event);
		if(event.getObject() instanceof LivingEntity)
			MarkData.register(event);
		if(event.getObject().level != null && !event.getObject().level.isClientSide && event.getObject().level instanceof ServerWorld)
			WrapperEntityData.register(event);
	}

	@SubscribeEvent
	public void attachItem(AttachCapabilitiesEvent<ItemStack> event){
		ItemStackWrapper.register(event);
	}

	@SubscribeEvent
	public void savePlayer(PlayerEvent.SaveToFile event){
		PlayerData.get(event.getPlayer()).save(false);
	}

	@SubscribeEvent
	public void saveChunk(ChunkDataEvent.Save event){
		if(!(event.getChunk() instanceof Chunk)){
			return;
		}
		for(ClassInheritanceMultiMap<Entity> map : ((Chunk)event.getChunk()).getEntitySections()){
			for(Entity e : map){
				if(e instanceof LivingEntity){
					MarkData.get((LivingEntity)e).save();
				}
			}
		}
	}

	@SubscribeEvent
	public void playerTracking(PlayerEvent.StartTracking event){
		if(event.getTarget() instanceof EntityNPCInterface){
			EntityNPCInterface npc = (EntityNPCInterface)event.getTarget();
			npc.tracking.add(event.getPlayer().getId());
			VisibilityController.checkIsVisible(npc, (ServerPlayerEntity) event.getPlayer());
		}
		if(!(event.getTarget() instanceof LivingEntity) || event.getTarget().level.isClientSide)
			return;
		MarkData data = MarkData.get((LivingEntity) event.getTarget());
		if(data.marks.isEmpty())
			return;
		Packets.send((ServerPlayerEntity)event.getPlayer(), new PacketMarkData(event.getTarget().getId(), data.getNBT()));
	}

	@SubscribeEvent
	public void playerStopTracking(PlayerEvent.StopTracking event) {
		if(event.getTarget() instanceof EntityNPCInterface){
			EntityNPCInterface npc = (EntityNPCInterface)event.getTarget();
			npc.tracking.remove(event.getPlayer().getId());
		}
	}

	@SubscribeEvent
	public void commandGive(CommandEvent event){
		String command = event.getParseResults().getReader().getString();
		if(!command.startsWith("/give "))
			return;
		try {
			CommandContext<CommandSource> context = event.getParseResults().getContext().build(event.getParseResults().getReader().getString());
			Collection<ServerPlayerEntity> players =  EntityArgument.getPlayers(context, "targets");

			for(ServerPlayerEntity player : players){
				player.getServer().execute(ListenableFutureTask.create(Executors.callable(() -> {
					PlayerQuestData playerdata = PlayerData.get(player).questData;
					playerdata.checkQuestCompletion(player, QuestType.ITEM);
				})));
			}
		}
		catch(Throwable e) {

		}
	}

	@SubscribeEvent
	public void commandTime(CommandEvent event){
		String command = event.getParseResults().getReader().getString();
		if(!command.startsWith("/time ")){
			return;
		}
		try {
			CustomNpcs.Server.submit(() -> {
				List<ServerPlayerEntity> players = CustomNpcs.Server.getPlayerList().getPlayers();
				for(ServerPlayerEntity playerMP:players){
					VisibilityController.instance.onUpdate(playerMP);
				}
			});
		}
		catch(Throwable e) {

		}
	}
}
