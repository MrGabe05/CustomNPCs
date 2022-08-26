package noppes.npcs.controllers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.handler.IFactionHandler;
import noppes.npcs.api.handler.data.IFaction;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncRemove;
import noppes.npcs.packets.client.PacketSyncUpdate;

public class FactionController implements IFactionHandler{
	public HashMap<Integer,Faction> factionsSync = new HashMap<Integer,Faction>();
	
	public HashMap<Integer,Faction> factions = new HashMap<Integer,Faction>();
	
	public static FactionController instance = new FactionController();

	private int lastUsedID = 0;
	
	public FactionController(){
		instance = this;
		factions.put(0,new Faction(0,"Friendly", 0x00DD00, 2000));
		factions.put(1,new Faction(1,"Neutral", 0xF2DD00, 1000));
		factions.put(2,new Faction(2,"Aggressive", 0xDD0000, 0));
	}

	public void load(){
		factions = new HashMap<Integer, Faction>();
		lastUsedID = 0;
		try{
			File saveDir = CustomNpcs.getWorldSaveDirectory();
			if(saveDir == null){
				return;
			}
			try {
		        File file = new File(saveDir, "factions.dat");
		        if(file.exists()){
		        	loadFactionsFile(file);
		        }
			} catch (Exception e) {
				try {
			        File file = new File(saveDir, "factions.dat_old");
			        if(file.exists()){
			        	loadFactionsFile(file);
			        }
			        
				} catch (Exception ee) {
				}
			}
		}
		finally {
			EventHooks.onGlobalFactionsLoaded(this);
			if(factions.isEmpty()){
				factions.put(0,new Faction(0,"Friendly", 0x00DD00, 2000));
				factions.put(1,new Faction(1,"Neutral", 0xF2DD00, 1000));
				factions.put(2,new Faction(2,"Aggressive", 0xDD0000, 0));
			}
		}
	}

	private void loadFactionsFile(File file) throws IOException{
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
		loadFactions(var1);
		var1.close();
	}

	public void loadFactions(DataInputStream stream) throws IOException{
		HashMap<Integer,Faction> factions = new HashMap<Integer,Faction>();
        CompoundNBT nbttagcompound1 = CompressedStreamTools.read(stream);
        lastUsedID = nbttagcompound1.getInt("lastID");
        ListNBT list = nbttagcompound1.getList("NPCFactions", 10);

	    if(list != null){
	        for(int i = 0; i < list.size(); i++)
	        {
	            CompoundNBT nbttagcompound = list.getCompound(i);
	            Faction faction = new Faction();
	            faction.readNBT(nbttagcompound);
	            factions.put(faction.id,faction);
	        }
	    }
    	this.factions = factions;
	}
	
	public CompoundNBT getNBT(){
        ListNBT list = new ListNBT();
        for(int slot : factions.keySet()){
        	Faction faction = factions.get(slot);
            CompoundNBT nbtfactions = new CompoundNBT();
            faction.writeNBT(nbtfactions);
        	list.add(nbtfactions);
        }
        CompoundNBT nbttagcompound = new CompoundNBT();
        nbttagcompound.putInt("lastID", lastUsedID);
        nbttagcompound.put("NPCFactions", list);
        return nbttagcompound;
	}
	
	public void saveFactions(){
		try {
			File saveDir = CustomNpcs.getWorldSaveDirectory();
            File file = new File(saveDir, "factions.dat_new");
            File file1 = new File(saveDir, "factions.dat_old");
            File file2 = new File(saveDir, "factions.dat");
            CompressedStreamTools.writeCompressed(getNBT(), new FileOutputStream(file));
            if(file1.exists())
            {
                file1.delete();
            }
            file2.renameTo(file1);
            if(file2.exists())
            {
                file2.delete();
            }
            file.renameTo(file2);
            if(file.exists())
            {
                file.delete();
            }
		} catch (Exception e) {
			LogWriter.except(e);
		}
	}

	public Faction getFaction(int faction) {
		return factions.get(faction);
	}

	public void saveFaction(Faction faction) {		
		if(faction.id < 0){
			faction.id = getUnusedId();
			while(hasName(faction.name))
				faction.name += "_";
		}
		else{
			Faction existing = factions.get(faction.id);
			if(existing != null && !existing.name.equals(faction.name))
				while(hasName(faction.name))
					faction.name += "_";
		}
		factions.remove(faction.id);
		factions.put(faction.id, faction);
		Packets.sendAll(new PacketSyncUpdate(faction.id, SyncType.FACTION, faction.writeNBT(new CompoundNBT())));
		saveFactions();
	}
	
	public int getUnusedId(){
		if(lastUsedID == 0){
			for(int catid : factions.keySet())
				if(catid > lastUsedID)
					lastUsedID = catid;
		}
		lastUsedID++;
		return lastUsedID;
	}
	
	public IFaction delete(int id) {
		if(id < 0 || factions.size() <= 1)
			return null;
		Faction faction = factions.remove(id);
		if(faction == null)
			return null;
		saveFactions();
		faction.id = -1;
		Packets.sendAll(new PacketSyncRemove(id, SyncType.FACTION));
		return faction;
	}
	
	public int getFirstFactionId() {
		return factions.keySet().iterator().next();
	}
	
	public Faction getFirstFaction() {
		return factions.values().iterator().next();
	}
	
	public boolean hasName(String newName) {
		if(newName.trim().isEmpty())
			return true;
		for(Faction faction : factions.values())
			if(faction.name.equals(newName))
				return true;
		return false;
	}
	
	public Faction getFactionFromName(String factioname){
	    for (Map.Entry<Integer,Faction>entryfaction : factions.entrySet()){
	        if (entryfaction.getValue().name.equalsIgnoreCase(factioname)){
	            return entryfaction.getValue();
	        }
	    }
	    return null;
	}
	
	public String[] getNames() {
		String[] names = new String[factions.size()];
		int i = 0;
		for(Faction faction : factions.values()){
			names[i] = faction.name.toLowerCase();
			i++;
		}
		return names;
	}
	
	@Override
	public List<IFaction> list() {
		return new ArrayList<IFaction>(factions.values());
	}
	
	@Override
	public IFaction create(String name, int color) {
		Faction faction = new Faction();
		while(hasName(name)){
			name += "_";
		}
		faction.name = name;
		faction.color = color;
		saveFaction(faction);
		return faction;
	}

	@Override
	public IFaction get(int id) {
		return factions.get(id);
	}
}
