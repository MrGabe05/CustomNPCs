package noppes.npcs.client.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.shared.common.util.LogWriter;

public class PresetController {

	public HashMap<String,Preset> presets = new HashMap<String,Preset>();
	
	private File dir;
	public static PresetController instance;
	
	public PresetController(File dir){
		instance = this;
		this.dir = dir;
		load();
	}

	public Preset getPreset(String username) {
		if(presets.isEmpty())
			load();
		return presets.get(username.toLowerCase());
	}
	
	public void load(){
		CompoundNBT compound = loadPreset();
		HashMap<String,Preset> presets = new HashMap<String, Preset>();
		if(compound != null){
			ListNBT list = compound.getList("Presets", 10);
			for(int i = 0; i < list.size(); i++){
				CompoundNBT comp = list.getCompound(i);
				Preset preset = new Preset();
				preset.load(comp);
				presets.put(preset.name.toLowerCase(), preset);
			}
		}
		Preset.FillDefault(presets);
		this.presets = presets;
	}

	private CompoundNBT loadPreset(){
		String filename = "presets.dat";
		try {
	        File file = new File(dir, filename);
	        if(!file.exists()){
				return null;
	        }
	        return CompressedStreamTools.readCompressed(new FileInputStream(file));
		} catch (Exception e) {
			LogWriter.except(e);
		}
		try {
	        File file = new File(dir, filename+"_old");
	        if(!file.exists()){
				return null;
	        }
	        return CompressedStreamTools.readCompressed(new FileInputStream(file));
	        
		} catch (Exception e) {
			LogWriter.except(e);
		}
		return null;
	}
	
	public void save(){
		CompoundNBT compound = new CompoundNBT();
		ListNBT list = new ListNBT();
		for(Preset preset : presets.values()){
			list.add(preset.save());
		}
		
		compound.put("Presets", list);
		savePreset(compound);
	}

	private void savePreset(CompoundNBT compound){
		String filename = "presets.dat";
		try {
            File file = new File(dir, filename+"_new");
            File file1 = new File(dir, filename+"_old");
            File file2 = new File(dir, filename);
            CompressedStreamTools.writeCompressed(compound, new FileOutputStream(file));
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

	public void addPreset(Preset preset) {
		while(presets.containsKey(preset.name.toLowerCase())){
			preset.name += "_";
		}
		presets.put(preset.name.toLowerCase(), preset);
		save();
	}

	public void removePreset(String preset) {
		if(preset == null)
			return;
		presets.remove(preset.toLowerCase());
		save();
	}
}
