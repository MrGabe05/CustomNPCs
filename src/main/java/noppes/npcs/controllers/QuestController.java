package noppes.npcs.controllers;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.api.handler.IQuestHandler;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.handler.data.IQuestCategory;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncRemove;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.util.NBTJsonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class QuestController implements IQuestHandler{
	public HashMap<Integer,QuestCategory> categoriesSync = new HashMap<Integer, QuestCategory>();
	public HashMap<Integer,QuestCategory> categories = new HashMap<Integer, QuestCategory>();
	public HashMap<Integer,Quest> quests = new HashMap<Integer, Quest>();

	public static QuestController instance = new QuestController();

	private int lastUsedCatID = 0;
	private int lastUsedQuestID = 0;
	
	public QuestController(){
		instance = this;
	}
	
	public void load(){
		categories.clear();
		quests.clear();

		lastUsedCatID = 0;
		lastUsedQuestID = 0;
		
		try {
	        File file = new File(CustomNpcs.getWorldSaveDirectory(), "quests.dat");
	        if(file.exists()){
	        	loadCategoriesOld(file);
	        	file.delete();
	        	file = new File(CustomNpcs.getWorldSaveDirectory(), "quests.dat_old");
	        	if(file.exists())
	        		file.delete();
	        	return;
	        }
		} catch (Exception e) {
			LogWriter.except(e);
		}
		
		File dir = getDir();
		if(!dir.exists()){
			dir.mkdir();
		}
		else{
			for(File file : dir.listFiles()){
				if(!file.isDirectory())
					continue;
				QuestCategory category = loadCategoryDir(file);
				Iterator<Integer> ite = category.quests.keySet().iterator();
				while(ite.hasNext()){
					int id = ite.next();
					if(id > lastUsedQuestID)
						lastUsedQuestID = id;
					Quest quest = category.quests.get(id);
					if(quests.containsKey(id)){
						LogWriter.error("Duplicate id " + quest.id + " from category " + category.title);
						ite.remove();
					}
					else{
						quests.put(id, quest);
					}
				}
				lastUsedCatID++;
				category.id = lastUsedCatID;
				categories.put(category.id, category);
			}
		}
	}
	private QuestCategory loadCategoryDir(File dir) {
		QuestCategory category = new QuestCategory();
		category.title = dir.getName();
		for(File file : dir.listFiles()){
			if(!file.isFile() || !file.getName().endsWith(".json"))
				continue;
			try{
				Quest quest = new Quest(category);
				quest.id = Integer.parseInt(file.getName().substring(0, file.getName().length() - 5));
				quest.readNBTPartial(NBTJsonUtil.LoadFile(file));
				category.quests.put(quest.id, quest);
			}
			catch(Exception e){
				LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
			}
		}
		return category;
	}
	private void loadCategoriesOld(File file) throws Exception{		
        CompoundNBT nbttagcompound1 = CompressedStreamTools.readCompressed(new FileInputStream(file));
        lastUsedCatID = nbttagcompound1.getInt("lastID");
        lastUsedQuestID = nbttagcompound1.getInt("lastQuestID");
        ListNBT list = nbttagcompound1.getList("Data", 10);
        if(list != null){
	        for(int i = 0; i < list.size(); i++)
	        {
	            QuestCategory category = new QuestCategory();
	            category.readNBT(list.getCompound(i));
	            categories.put(category.id,category);
	            saveCategory(category);
                Iterator<Map.Entry<Integer, Quest>> ita = category.quests.entrySet().iterator();
                while(ita.hasNext()){
                	Map.Entry<Integer, Quest> entry = ita.next();
            		Quest quest = entry.getValue();
            		quest.id = entry.getKey();
                	if(quests.containsKey(quest.id))
                		ita.remove();
                	else{
                		saveQuest(category, quest);
                	}
                }
	        }
        }
	}


	public void removeCategory(int category){
		QuestCategory cat = categories.get(category);
		if(cat == null)
			return;
		File dir = new File(getDir(), cat.title);
		if(!dir.delete())
			return;
		for(int dia : cat.quests.keySet())
			quests.remove(dia);
		categories.remove(category);
		Packets.sendAll(new PacketSyncRemove(category, SyncType.QUEST_CATEGORY));
	}
	
	public void saveCategory(QuestCategory category){
		category.title = NoppesStringUtils.cleanFileName(category.title);
		if(categories.containsKey(category.id)){
			QuestCategory currentCategory = categories.get(category.id);
			if(!currentCategory.title.equals(category.title)){
				while(containsCategoryName(category))
					category.title += "_";
				File newdir = new File(getDir(), category.title);
				File olddir = new File(getDir(), currentCategory.title);
				if(newdir.exists())
					return;
				if(!olddir.renameTo(newdir))
					return;
			}
			category.quests = currentCategory.quests;
		}
		else{
			if(category.id < 0){
				lastUsedCatID++;
				category.id = lastUsedCatID;
			}
			while(containsCategoryName(category))
				category.title += "_";
			File dir = new File(getDir(), category.title);
			if(!dir.exists())
				dir.mkdirs();
		}
		categories.put(category.id, category);
		Packets.sendAll(new PacketSyncUpdate(category.id, SyncType.QUEST_CATEGORY, category.writeNBT(new CompoundNBT())));
	}
	public boolean containsCategoryName(QuestCategory category) {
		for(QuestCategory cat : categories.values()){
			if(cat.id != category.id && cat.title.equalsIgnoreCase(category.title))
				return true;
		}
		return false;
	}
	
	public boolean containsQuestName(QuestCategory category, Quest quest) {
		for(Quest q : category.quests.values()){
			if(q.id != quest.id && q.title.equalsIgnoreCase(quest.title))
				return true;
		}
		return false;
	}
	
	public void saveQuest(QuestCategory category, Quest quest){
		if(category == null)
			return;

		while(containsQuestName(quest.category, quest)){
			quest.title = quest.title + "_";
		}
		
		if(quest.id < 0){
			lastUsedQuestID++;
			quest.id = lastUsedQuestID;
		}
    	quests.put(quest.id, quest);
    	category.quests.put(quest.id, quest);
    	
    	File dir = new File(getDir(), category.title);
    	if(!dir.exists())
    		dir.mkdirs();

    	File file = new File(dir, quest.id + ".json_new");
    	File file2 = new File(dir, quest.id + ".json");
    	
    	try {
			NBTJsonUtil.SaveFile(file, quest.writeToNBTPartial(new CompoundNBT()));
			if(file2.exists())
				file2.delete();
			file.renameTo(file2);
			Packets.sendAll(new PacketSyncUpdate(category.id, SyncType.QUEST, quest.save(new CompoundNBT())));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeQuest(Quest quest) {
		File file = new File(new File(getDir(), quest.category.title), quest.id + ".json");
		if(!file.delete())
			return;
		quests.remove(quest.id);
		quest.category.quests.remove(quest.id);
		Packets.sendAll(new PacketSyncRemove(quest.id, SyncType.QUEST));
	}

	private File getDir(){
		return new File(CustomNpcs.getWorldSaveDirectory(), "quests");
	}

	@Override
	public List<IQuestCategory> categories() {
		return new ArrayList(categories.values());
	}

	@Override
	public IQuest get(int id) {
		return quests.get(id);
	}
	
}
