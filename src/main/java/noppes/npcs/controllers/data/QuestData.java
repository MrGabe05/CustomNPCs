package noppes.npcs.controllers.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.api.event.QuestEvent;

public class QuestData {
	public Quest quest;
	public boolean isCompleted;
	public CompoundNBT extraData = new CompoundNBT();
	public QuestData(Quest quest){
		this.quest = quest;
	}
	public void addAdditionalSaveData(CompoundNBT nbttagcompound){
		nbttagcompound.putBoolean("QuestCompleted", isCompleted);
		nbttagcompound.put("ExtraData", extraData);
	}
	public void readAdditionalSaveData(CompoundNBT nbttagcompound){
		isCompleted = nbttagcompound.getBoolean("QuestCompleted");
		extraData = nbttagcompound.getCompound("ExtraData");
	}
}
