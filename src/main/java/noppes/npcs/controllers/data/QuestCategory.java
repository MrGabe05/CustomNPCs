package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.handler.data.IQuestCategory;

public class QuestCategory implements IQuestCategory{
	public HashMap<Integer,Quest> quests;
	public int id = -1;
	public String title = "";
	
	public QuestCategory(){
		quests = new HashMap<Integer, Quest>();
	}

	public void readNBT(CompoundNBT nbttagcompound) {
        id = nbttagcompound.getInt("Slot");
        title = nbttagcompound.getString("Title");
        ListNBT dialogsList = nbttagcompound.getList("Dialogs", 10);
        if(dialogsList != null){
            for(int ii = 0; ii < dialogsList.size(); ii++)
            {
                CompoundNBT nbttagcompound2 = dialogsList.getCompound(ii);
                Quest quest = new Quest(this);
                quest.readNBT(nbttagcompound2);
            	quests.put(quest.id, quest);
            }
        }
	}

	public CompoundNBT writeNBT(CompoundNBT nbttagcompound) {
		nbttagcompound.putInt("Slot", id);
		nbttagcompound.putString("Title", title);
        ListNBT dialogs = new ListNBT();
        for(int dialogId : quests.keySet()){
        	Quest quest = quests.get(dialogId);
        	dialogs.add(quest.save(new CompoundNBT()));
        }
        
        nbttagcompound.put("Dialogs", dialogs);
        
        return nbttagcompound;
	}

	@Override
	public List<IQuest> quests() {
		return new ArrayList<IQuest>(quests.values());
	}

	@Override
	public String getName() {
		return title;
	}

	@Override
	public IQuest create() {
		return new Quest(this);
	}
}
