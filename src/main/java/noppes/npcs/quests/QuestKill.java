package noppes.npcs.quests;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.NBTTags;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class QuestKill extends QuestInterface{
	public TreeMap<String,Integer> targets = new TreeMap<String,Integer>();

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		targets = new TreeMap(NBTTags.getStringIntegerMap(compound.getList("QuestDialogs", 10)));
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		compound.put("QuestDialogs", NBTTags.nbtStringIntegerMap(targets));
	}

	@Override
	public boolean isCompleted(PlayerEntity player) {
		PlayerQuestData playerdata = PlayerData.get(player).questData;
		QuestData data = playerdata.activeQuests.get(questId);
		if(data == null)
			return false;
		HashMap<String,Integer> killed = getKilled(data);
		if(killed.size() != targets.size())
			return false;
		for(String entity : killed.keySet()){
			if(!targets.containsKey(entity) || targets.get(entity) > killed.get(entity))
				return false;
		}
		
		return true;
	}

	@Override
	public void handleComplete(PlayerEntity player) {
	}

	public HashMap<String, Integer> getKilled(QuestData data) {
		return NBTTags.getStringIntegerMap(data.extraData.getList("Killed", 10));
	}
	public void setKilled(QuestData data, HashMap<String, Integer> killed) {
		data.extraData.put("Killed", NBTTags.nbtStringIntegerMap(killed));
	}

	@Override
	public IQuestObjective[] getObjectives(PlayerEntity player) {
		List<IQuestObjective> list = new ArrayList<IQuestObjective>();
		for(Entry<String,Integer> entry : targets.entrySet()){
			list.add(new QuestKillObjective(player, entry.getKey(), entry.getValue()));
		}
		return list.toArray(new IQuestObjective[list.size()]);
	}
	
	class QuestKillObjective implements IQuestObjective{
		private final PlayerEntity player;
		private final String entity;
		private final int amount;
		public QuestKillObjective(PlayerEntity player, String entity, int amount) {
			this.player = player;
			this.entity = entity;
			this.amount = amount;
		}
		
		@Override
		public int getProgress() {
			PlayerData data = PlayerData.get(player);
			PlayerQuestData playerdata = data.questData;
			QuestData questdata = playerdata.activeQuests.get(questId);
			HashMap<String,Integer> killed = getKilled(questdata);
			if(!killed.containsKey(entity))
				return 0;
			return killed.get(entity);
		}

		@Override
		public void setProgress(int progress) {
			if(progress < 0 || progress > amount) {
				throw new CustomNPCsException("Progress has to be between 0 and " + amount);
			}
			PlayerData data = PlayerData.get(player);
			PlayerQuestData playerdata = data.questData;
			QuestData questdata = playerdata.activeQuests.get(questId);
			HashMap<String,Integer> killed = getKilled(questdata);
			
			if(killed.containsKey(entity) && killed.get(entity) == progress) {
				return;
			}
			killed.put(entity, progress);
			setKilled(questdata, killed);
			data.questData.checkQuestCompletion(player, QuestType.KILL);
			data.questData.checkQuestCompletion(player, QuestType.AREA_KILL);
			data.updateClient = true;			
		}

		@Override
		public int getMaxProgress() {
			return amount;
		}

		@Override
		public boolean isCompleted() {
			return getProgress() >= amount;
		}

		@Override
		public String getText() {
			return getMCText().getString();
		}

		@Override
		public ITextComponent getMCText() {
			return new TranslationTextComponent(entity).append(": " + getProgress() + "/" + getMaxProgress());
		}
	}

}
