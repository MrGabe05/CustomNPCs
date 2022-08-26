package noppes.npcs.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.NBTTags;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.NBTJsonUtil.JsonException;

public class LinkedNpcController {
	public static LinkedNpcController Instance;
	public List<LinkedData> list = new ArrayList<LinkedData>();
	
	public LinkedNpcController(){
		Instance = this;
		load();
	}
	private void load(){
		try {
	        loadNpcs();
		} catch (Exception e) {
			LogWriter.except(e);
		}
	}
	
	public File getDir(){
		File dir = new File(CustomNpcs.getWorldSaveDirectory(), "linkednpcs");
		if(!dir.exists())
			dir.mkdir();
		return dir;
	}
	
	private void loadNpcs(){
		LogWriter.info("Loading Linked Npcs");
		File dir = getDir();
		if(dir.exists()){
			List<LinkedData> list = new ArrayList<LinkedData>();
			for(File file : dir.listFiles()){
				if(file.getName().endsWith(".json")){
					try{
						CompoundNBT compound = NBTJsonUtil.LoadFile(file);;
						LinkedData linked = new LinkedData();
						linked.setNBT(compound);
						list.add(linked);
					}
					catch(Exception e){
						LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
					}
				}
			}
			
			this.list = list;
		}
		LogWriter.info("Done loading Linked Npcs");
	}
	
	public void save(){
		for(LinkedData npc : list){
			try {
				saveNpc(npc);
			} catch (IOException e) {
				LogWriter.except(e);
			}
		}
	}
	
	private void saveNpc(LinkedData npc) throws IOException {
		File file = new File(getDir(), npc.name + ".json_new");
        File file1 = new File(getDir(), npc.name + ".json");	
		try{
			NBTJsonUtil.SaveFile(file, npc.getNBT());
			if(file1.exists()){
				file1.delete();
			}
			file.renameTo(file1);
			
		} catch (JsonException e) {
			LogWriter.except(e);
		}
	}

	public static class LinkedData{
		public String name = "LinkedNpc";
		public long time = 0;
		public CompoundNBT data = new CompoundNBT();
		
		public LinkedData(){
			time = System.currentTimeMillis();
		}
		
		public void setNBT(CompoundNBT compound){
			name = compound.getString("LinkedName");
			data = compound.getCompound("NPCData");
		}
		
		public CompoundNBT getNBT(){
			CompoundNBT compound = new CompoundNBT();
			compound.putString("LinkedName", name);
			compound.put("NPCData", data);
			return compound;
		}
	}

	public void loadNpcData(EntityNPCInterface npc) {
		if(npc.linkedName.isEmpty())
			return;
		LinkedData data = getData(npc.linkedName);
		if(data == null){
			npc.linkedLast = 0;
			npc.linkedName = "";
			npc.linkedData = null;
		}
		else{
			npc.linkedData = data;
			if(npc.getX() == 0 && npc.getY() == 0 && npc.getZ() == 0)
				return;
			npc.linkedLast = data.time;
			List<int[]> points = npc.ais.getMovingPath();

			CompoundNBT compound = NBTTags.NBTMerge(readNpcData(npc), data.data);
						
			npc.display.readToNBT(compound);
			npc.stats.readToNBT(compound);
			npc.advanced.readToNBT(compound);
			npc.inventory.load(compound);
			if(compound.contains("ModelData"))
				((EntityCustomNpc)npc).modelData.load(compound.getCompound("ModelData"));
			
			npc.ais.readToNBT(compound);
			npc.transform.readToNBT(compound);
			npc.ais.setMovingPath(points);

			npc.updateClient = true;
		}
	}
	
	private void cleanTags(CompoundNBT compound){
		compound.remove("MovingPathNew");
	}
	
	public LinkedData getData(String name){
		for(LinkedData data : list){
			if(data.name.equalsIgnoreCase(name))
				return data;
		}
		return null;
	}
	
	private CompoundNBT readNpcData(EntityNPCInterface npc){
		CompoundNBT compound = new CompoundNBT();
		npc.display.save(compound);
		npc.inventory.save(compound);
		npc.stats.save(compound);
		npc.ais.save(compound);
		npc.advanced.save(compound);
		npc.transform.save(compound);
		compound.put("ModelData", ((EntityCustomNpc)npc).modelData.save());
		return compound;
	}
	
	public void saveNpcData(EntityNPCInterface npc) {
		CompoundNBT compound = readNpcData(npc);
		cleanTags(compound);
		
		if(npc.linkedData.data.equals(compound))
			return;
		
		npc.linkedData.data = compound;
		npc.linkedData.time = System.currentTimeMillis();
		save();
	}
	public void removeData(String name) {
		Iterator<LinkedData> ita = list.iterator();
		while(ita.hasNext()){
			if(ita.next().name.equalsIgnoreCase(name))
				ita.remove();
		}
		save();
	}
	
	public void addData(String name){
		if(getData(name) != null || name.isEmpty())
			return;
		
		LinkedData data = new LinkedData();
		data.name = name;
		list.add(data);
		save();
	}
}
