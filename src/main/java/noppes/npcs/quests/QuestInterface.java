package noppes.npcs.quests;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.handler.data.IQuestObjective;

public abstract class QuestInterface{
	public int questId;
	public abstract void addAdditionalSaveData(CompoundNBT compound);
	public abstract void readAdditionalSaveData(CompoundNBT compound);
	public abstract boolean isCompleted(PlayerEntity player);
	public abstract void handleComplete(PlayerEntity player);
	public abstract IQuestObjective[] getObjectives(PlayerEntity player);
}
