package noppes.npcs.quests;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.NBTTags;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestDialog extends QuestInterface{

	public HashMap<Integer,Integer> dialogs = new HashMap<Integer,Integer>();

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		dialogs = NBTTags.getIntegerIntegerMap(compound.getList("QuestDialogs", 10));
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		compound.put("QuestDialogs", NBTTags.nbtIntegerIntegerMap(dialogs));
	}

	@Override
	public boolean isCompleted(PlayerEntity player) {
		for(int dialogId : dialogs.values())
			if(!PlayerData.get(player).dialogData.dialogsRead.contains(dialogId))
				return false;
		return true;
	}

	@Override
	public void handleComplete(PlayerEntity player) {
		
	}

	@Override
	public IQuestObjective[] getObjectives(PlayerEntity player) {
		List<IQuestObjective> list = new ArrayList<IQuestObjective>();
		for(int i = 0; i < 3; i++) {
			if(dialogs.containsKey(i)) {
				Dialog dialog = DialogController.instance.dialogs.get(dialogs.get(i));
				if(dialog != null) {
					list.add(new QuestDialogObjective(player, dialog));
				}
			}
		}
		return list.toArray(new IQuestObjective[list.size()]);
	}
	
	class QuestDialogObjective implements IQuestObjective{
		private final PlayerEntity player;
		private final Dialog dialog;
		public QuestDialogObjective(PlayerEntity player, Dialog dialog) {
			this.player = player;
			this.dialog = dialog;
		}
		
		@Override
		public int getProgress() {
			return isCompleted() ? 1 : 0;
		}

		@Override
		public void setProgress(int progress) {
			if(progress < 0 || progress > 1) {
				throw new CustomNPCsException("Progress has to be 0 or 1");
			}
			PlayerData data = PlayerData.get(player);
			boolean completed = data.dialogData.dialogsRead.contains(dialog.id);
			if(progress == 0 && completed) {
				data.dialogData.dialogsRead.remove(dialog.id);
				data.questData.checkQuestCompletion(player, QuestType.DIALOG);
				data.updateClient = true;
			}
			if(progress == 1 && !completed){
				data.dialogData.dialogsRead.add(dialog.id);
				data.questData.checkQuestCompletion(player, QuestType.DIALOG);
				data.updateClient = true;
			}			
		}

		@Override
		public int getMaxProgress() {
			return 1;
		}

		@Override
		public boolean isCompleted() {
			PlayerData data = PlayerData.get(player);
			return data.dialogData.dialogsRead.contains(dialog.id);
		}

		@Override
		public String getText() {
			return getMCText().getString();
		}

		@Override
		public ITextComponent getMCText() {
			return new TranslationTextComponent(dialog.title).append(" (").append(new TranslationTextComponent(isCompleted() ? "quest.read" : "quest.unread")).append(")");
		}
	}
}
