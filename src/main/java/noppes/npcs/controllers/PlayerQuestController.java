package noppes.npcs.controllers;

import java.util.Vector;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.EventHooks;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.constants.EnumQuestRepeat;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketAchievement;
import noppes.npcs.packets.client.PacketChat;
import noppes.npcs.quests.QuestDialog;

public class PlayerQuestController {

	public static boolean hasActiveQuests(PlayerEntity player){
		PlayerQuestData data = PlayerData.get(player).questData;
		return !data.activeQuests.isEmpty();
	}
	
	public static boolean isQuestActive(PlayerEntity player, int quest){
		PlayerQuestData data = PlayerData.get(player).questData;
		return data.activeQuests.containsKey(quest);
	}
	
	public static boolean isQuestCompleted(PlayerEntity player, int quest){
		PlayerQuestData data = PlayerData.get(player).questData;
		QuestData q = data.activeQuests.get(quest);
		if(q == null)
			return false;
		return q.isCompleted;
	}
	
	public static boolean isQuestFinished(PlayerEntity player, int questid){
		PlayerQuestData data = PlayerData.get(player).questData;
		return data.finishedQuests.containsKey(questid);
	}
	
	public static boolean canQuestBeAccepted(PlayerEntity player, int questId){
		Quest quest = QuestController.instance.quests.get(questId);
		if(quest == null)
			return false;

		PlayerQuestData data = PlayerData.get(player).questData;
		if(data.activeQuests.containsKey(quest.id))
			return false;
		
		if(!data.finishedQuests.containsKey(quest.id) || quest.repeat == EnumQuestRepeat.REPEATABLE)
			return true;
		if(quest.repeat == EnumQuestRepeat.NONE)
			return false;
		
		long questTime = data.finishedQuests.get(quest.id);
		
		if(quest.repeat == EnumQuestRepeat.MCDAILY){
			return player.level.getGameTime() - questTime >= 24000;
		}
		else if(quest.repeat == EnumQuestRepeat.MCWEEKLY){
			return player.level.getGameTime() - questTime >= 168000;
		}
		else if(quest.repeat == EnumQuestRepeat.RLDAILY){
			return System.currentTimeMillis() - questTime >= 86400000;
		}
		else if(quest.repeat == EnumQuestRepeat.RLWEEKLY){
			return System.currentTimeMillis() - questTime >= 604800000;
		}
		return false;
	}

	public static void addActiveQuest(Quest quest, PlayerEntity player) {
		PlayerData playerdata = PlayerData.get(player);
        LogWriter.debug("AddActiveQuest: " + quest.title + " + " + playerdata);
		PlayerQuestData data = playerdata.questData;
		if(playerdata.scriptData.getPlayer().canQuestBeAccepted(quest.id)){
			if(EventHooks.onQuestStarted(playerdata.scriptData, quest))
				return;
			data.activeQuests.put(quest.id, new QuestData(quest));
			Packets.send((ServerPlayerEntity)player, new PacketAchievement(new TranslationTextComponent("quest.newquest"), new TranslationTextComponent(quest.title), 2));
			Packets.send((ServerPlayerEntity)player, new PacketChat(new TranslationTextComponent("quest.newquest").append(":").append(new TranslationTextComponent(quest.title))));
			playerdata.updateClient = true;
		}
	}
	
	public static void setQuestFinished(Quest quest, PlayerEntity player){
		PlayerData playerdata = PlayerData.get(player);
		PlayerQuestData data = playerdata.questData;
		data.activeQuests.remove(quest.id);
		if(quest.repeat == EnumQuestRepeat.RLDAILY || quest.repeat == EnumQuestRepeat.RLWEEKLY)
			data.finishedQuests.put(quest.id, System.currentTimeMillis());
		else
			data.finishedQuests.put(quest.id,player.level.getGameTime());
		if(quest.repeat != EnumQuestRepeat.NONE && quest.type == QuestType.DIALOG){
			QuestDialog questdialog = (QuestDialog) quest.questInterface;
			for(int dialog : questdialog.dialogs.values()){
				playerdata.dialogData.dialogsRead.remove(dialog);
			}
		}
		playerdata.updateClient = true;
	}
	
	public static Vector<Quest> getActiveQuests(PlayerEntity player){
		Vector<Quest> quests = new Vector<Quest>();
		PlayerQuestData data = PlayerData.get(player).questData;
		for(QuestData questdata: data.activeQuests.values()){
			if(questdata.quest == null)
				continue;
			quests.add(questdata.quest);
		}
		return quests;
	}
}
