package noppes.npcs.quests;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;

import java.util.ArrayList;
import java.util.List;

public class QuestLocation extends QuestInterface{
	public String location = "";
	public String location2 = "";
	public String location3 = "";

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		location = compound.getString("QuestLocation");
		location2 = compound.getString("QuestLocation2");
		location3 = compound.getString("QuestLocation3");
		
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		compound.putString("QuestLocation", location);
		compound.putString("QuestLocation2", location2);
		compound.putString("QuestLocation3", location3);
	}

	@Override
	public boolean isCompleted(PlayerEntity player) {
		PlayerQuestData playerdata = PlayerData.get(player).questData;
		QuestData data = playerdata.activeQuests.get(questId);
		if(data == null)
			return false;
		return getFound(data, 0);
	}

	@Override
	public void handleComplete(PlayerEntity player) {
	}

	public boolean getFound(QuestData data, int i) {
		if(i == 1)
			return data.extraData.getBoolean("LocationFound");
		if(i == 2)
			return data.extraData.getBoolean("Location2Found");
		if(i == 3)
			return data.extraData.getBoolean("Location3Found");

		if(!location.isEmpty() && !data.extraData.getBoolean("LocationFound"))
			return false;
		if(!location2.isEmpty() && !data.extraData.getBoolean("Location2Found"))
			return false;
		return location3.isEmpty() || data.extraData.getBoolean("Location3Found");
	}
	public boolean setFound(QuestData data, String location) {
		if(location.equalsIgnoreCase(this.location) && !data.extraData.getBoolean("LocationFound")){
			data.extraData.putBoolean("LocationFound", true);
			return true;
		}
		if(location.equalsIgnoreCase(location2) && !data.extraData.getBoolean("LocationFound2")){
			data.extraData.putBoolean("Location2Found", true);
			return true;
		}
		if(location.equalsIgnoreCase(location3) && !data.extraData.getBoolean("LocationFound3")){
			data.extraData.putBoolean("Location3Found", true);
			return true;
		}
		
		return false;
	}

	@Override
	public IQuestObjective[] getObjectives(PlayerEntity player) {
		List<IQuestObjective> list = new ArrayList<IQuestObjective>();
		if(!location.isEmpty()) {
			list.add(new QuestLocationObjective(player, location, "LocationFound"));
		}
		if(!location2.isEmpty()) {
			list.add(new QuestLocationObjective(player, location2, "Location2Found"));
		}
		if(!location3.isEmpty()) {
			list.add(new QuestLocationObjective(player, location3, "Location3Found"));
		}
		return list.toArray(new IQuestObjective[list.size()]);
	}
	
	class QuestLocationObjective implements IQuestObjective{
		private final PlayerEntity player;
		private final String location;
		private final String nbtName;
		public QuestLocationObjective(PlayerEntity player, String location, String nbtName) {
			this.player = player;
			this.location = location;
			this.nbtName = nbtName;
		}
		
		@Override
		public int getProgress() {
			return isCompleted()?1:0;
		}

		@Override
		public void setProgress(int progress) {
			if(progress < 0 || progress > 1) {
				throw new CustomNPCsException("Progress has to be 0 or 1");
			}
			PlayerData data = PlayerData.get(player);
			QuestData questData = data.questData.activeQuests.get(questId);
			boolean completed = questData.extraData.getBoolean(nbtName);
			if(completed && progress == 1 || !completed && progress == 0) {
				return;
			}
			questData.extraData.putBoolean(nbtName, progress == 1);
			data.questData.checkQuestCompletion(player, QuestType.LOCATION);
			data.updateClient = true;
		}

		@Override
		public int getMaxProgress() {
			return 1;
		}

		@Override
		public boolean isCompleted() {
			PlayerData data = PlayerData.get(player);
			QuestData questData = data.questData.activeQuests.get(questId);
			return questData.extraData.getBoolean(nbtName);
		}

		@Override
		public String getText() {
			return getMCText().getString();
		}

		@Override
		public ITextComponent getMCText() {
			return new TranslationTextComponent(location).append(new TranslationTextComponent(isCompleted() ? "quest.found" : "quest.notfound"));
		}
	}

}
