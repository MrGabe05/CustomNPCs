package noppes.npcs.controllers.data;

import java.util.HashMap;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.EventHooks;
import noppes.npcs.constants.EnumQuestCompletion;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketAchievement;
import noppes.npcs.packets.client.PacketChat;
import noppes.npcs.quests.QuestInterface;

public class PlayerQuestData {
	public HashMap<Integer,QuestData> activeQuests = new HashMap<Integer,QuestData>();
	public HashMap<Integer,Long> finishedQuests = new HashMap<Integer,Long>();
	
	public void loadNBTData(CompoundNBT mainCompound) {
		if(mainCompound == null)
			return;
		CompoundNBT compound = mainCompound.getCompound("QuestData");
		
        ListNBT list = compound.getList("CompletedQuests", 10);
        if(list != null){
        	HashMap<Integer,Long> finishedQuests = new HashMap<Integer,Long>();
            for(int i = 0; i < list.size(); i++)
            {
                CompoundNBT nbttagcompound = list.getCompound(i);
                finishedQuests.put(nbttagcompound.getInt("Quest"),nbttagcompound.getLong("Date"));
            }
            this.finishedQuests = finishedQuests;
        }
		
        ListNBT list2 = compound.getList("ActiveQuests", 10);
        if(list2 != null){
        	HashMap<Integer,QuestData> activeQuests = new HashMap<Integer,QuestData>();
            for(int i = 0; i < list2.size(); i++)
            {
                CompoundNBT nbttagcompound = list2.getCompound(i);
                int id = nbttagcompound.getInt("Quest");
                Quest quest = QuestController.instance.quests.get(id);
                if(quest == null)
                	continue;
                QuestData data = new QuestData(quest);
                data.readAdditionalSaveData(nbttagcompound);
                activeQuests.put(id,data);
            }
            this.activeQuests = activeQuests;
        }
        
	}

	public void saveNBTData(CompoundNBT maincompound) {
		CompoundNBT compound = new CompoundNBT();
		ListNBT list = new ListNBT();
		for(int quest : finishedQuests.keySet()){
			CompoundNBT nbttagcompound = new CompoundNBT();
			nbttagcompound.putInt("Quest", quest);
			nbttagcompound.putLong("Date", finishedQuests.get(quest));
			list.add(nbttagcompound);
		}
		
		compound.put("CompletedQuests", list);
		
		ListNBT list2 = new ListNBT();
		for(int quest : activeQuests.keySet()){
			CompoundNBT nbttagcompound = new CompoundNBT();
			nbttagcompound.putInt("Quest", quest);
			activeQuests.get(quest).addAdditionalSaveData(nbttagcompound);
			list2.add(nbttagcompound);
		}
		
		compound.put("ActiveQuests", list2);
		
		maincompound.put("QuestData", compound);
	}

	public QuestData getQuestCompletion(PlayerEntity player,EntityNPCInterface npc) {
		for(QuestData data : activeQuests.values()){
			Quest quest = data.quest;
			if(quest != null && quest.completion == EnumQuestCompletion.Npc && quest.completerNpc.equals(npc.getName().getString()) && quest.questInterface.isCompleted(player)){
				return data;
			}
		}
		return null;
	}

	public boolean checkQuestCompletion(PlayerEntity player, int type) {
		boolean bo = false;
		for(QuestData data : this.activeQuests.values()){
			if(data.quest.type != type && type >= 0)
				continue;
			
			QuestInterface inter =  data.quest.questInterface;
			
			if(inter.isCompleted(player)){
				if(!data.isCompleted){
					if(!data.quest.complete(player,data)){
						Packets.send((ServerPlayerEntity)player, new PacketAchievement(new TranslationTextComponent("quest.completed"), new TranslationTextComponent(data.quest.title), 2));
						Packets.send((ServerPlayerEntity)player, new PacketChat(new TranslationTextComponent("quest.completed").append(": ").append(new TranslationTextComponent(data.quest.title))));
					}
					data.isCompleted = true;
					bo = true;
					
					EventHooks.onQuestFinished(PlayerData.get(player).scriptData, data.quest);
				}
			}
			else
				data.isCompleted = false;
		}
		return bo;
		
	}
	
}
