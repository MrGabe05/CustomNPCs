package noppes.npcs;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.shared.client.util.AnalyticsTracking;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.MassBlockController;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.entity.data.DataScenes.SceneContainer;
import noppes.npcs.entity.data.DataScenes.SceneState;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;

import java.util.ArrayList;

public class ServerTickHandler {
	public int ticks = 0;

	@SubscribeEvent
	public void onServerTick(TickEvent.PlayerTickEvent event){
		if(event.side != LogicalSide.SERVER || event.phase != Phase.START)
			return;
		PlayerEntity player = event.player;
		PlayerData data = PlayerData.get(player);


		if(player.getCommandSenderWorld().getDayTime()%24000==1 || player.getCommandSenderWorld().getDayTime()%240000==12001){
			VisibilityController.instance.onUpdate((ServerPlayerEntity) player);
		}

		if(data.updateClient) {
			Packets.send((ServerPlayerEntity)player, new PacketSync(SyncType.PLAYER_DATA, data.getSyncNBT(), true));
			VisibilityController.instance.onUpdate((ServerPlayerEntity) player);
			data.updateClient = false;
		}


		if(data.prevHeldItem != player.getMainHandItem()) {
			if(data.prevHeldItem.getItem() == CustomItems.wand || player.getMainHandItem().getItem() == CustomItems.wand) {
				VisibilityController.instance.onUpdate((ServerPlayerEntity) player);
			}
		}
		data.prevHeldItem = player.getMainHandItem();
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.WorldTickEvent event){
		if(event.side == LogicalSide.SERVER && event.phase == Phase.START){
			NPCSpawning.findChunksForSpawning((ServerWorld) event.world);
		}
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event){
		if(event.side == LogicalSide.SERVER && event.phase == Phase.START && ticks++ >= 20){
			SchematicController.Instance.updateBuilding();
			MassBlockController.Update();

			ticks = 0;
			for(SceneState state : DataScenes.StartedScenes.values()){
				if(!state.paused)
					state.ticks++;
			}
			for(SceneContainer entry : DataScenes.ScenesToRun){
				entry.update();
			}
			DataScenes.ScenesToRun = new ArrayList<>();
			
		}
	}

	@SubscribeEvent
	public void playerLogin(PlayerEvent.PlayerLoggedInEvent event){
		ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
		MinecraftServer server = event.getPlayer().getServer();
    	for(ServerWorld level : server.forgeGetWorldMap().values()) {
    		ServerScoreboard board = level.getScoreboard();
    		for(String objective : Availability.scores) {
            	ScoreObjective so = board.getObjective(objective);
            	if(so != null) {
            		if(board.getObjectiveDisplaySlotCount(so) == 0) {
                		player.connection.send(new SScoreboardObjectivePacket(so, 0));
            		}

            		Score sco = board.getOrCreatePlayerScore(player.getScoreboardName(), so);
            		player.connection.send(new SUpdateScorePacket(ServerScoreboard.Action.CHANGE, sco.getObjective().getName(), sco.getOwner(), sco.getScore()));
            	}
    		}
    	}
		player.inventoryMenu.addSlotListener( new IContainerListener() {

			@Override
			public void refreshContainer(Container container, NonNullList<ItemStack> itemsList) {

			}

			@Override
			public void slotChanged(Container container, int slotInd, ItemStack stack) {
				if(player.level.isClientSide)
					return;
				PlayerQuestData playerdata = PlayerData.get(player).questData;
				playerdata.checkQuestCompletion(player, QuestType.ITEM);
			}

			@Override
			public void setContainerData(Container container, int varToUpdate, int newValue) {

			}
		});

		PlayerData data = PlayerData.get(event.getPlayer());
		String serverName = "local";
		if(server.isDedicatedServer()){
			serverName = "server";
		}
		else if(server.isPublished()) {
			serverName =  "lan";
		}
		AnalyticsTracking.sendData(data.iAmStealingYourDatas, "join", serverName);
		
		SyncController.syncPlayer((ServerPlayerEntity) event.getPlayer());
	}
}
