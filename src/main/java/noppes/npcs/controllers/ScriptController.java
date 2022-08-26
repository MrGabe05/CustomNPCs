package noppes.npcs.controllers;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.wrapper.WorldWrapper;
import noppes.npcs.controllers.data.ForgeScriptData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.NBTJsonUtil.JsonException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptController{
	
	public static ScriptController Instance;
	public static boolean HasStart = false;
	private ScriptEngineManager manager;
	public Map<String, String> languages = new HashMap<String, String>();
	public Map<String, ScriptEngineFactory> factories = new HashMap<String, ScriptEngineFactory>();
	public Map<String, String> scripts = new HashMap<String, String>();
	public PlayerScriptData playerScripts = new PlayerScriptData(null);
	public ForgeScriptData forgeScripts = new ForgeScriptData();
	public long lastLoaded = 0;
	public long lastPlayerUpdate = 0;
	public File dir;
	public CompoundNBT compound = new CompoundNBT();
	
	private boolean loaded = false;
	public boolean shouldSave = false;
	
	public ScriptController(){
		loaded = false;
		Instance = this;
		if(!CustomNpcs.NashorArguments.isEmpty()) {
			System.setProperty("nashorn.args", CustomNpcs.NashorArguments);
		}
		//ClassLoader classLoader = MinecraftForge.class.getClassLoader();
		LogWriter.info("Script Engines Available:");
		manager = new ScriptEngineManager();
		try {
			if(manager.getEngineByName("ecmascript") == null) {
				//Launch.classLoader.addClassLoaderExclusion("jdk.nashorn."); might need to fix this
				//Launch.classLoader.addClassLoaderExclusion("jdk.internal.dynalink");
				Class c = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
				ScriptEngineFactory factory = (ScriptEngineFactory) c.newInstance();
				factory.getScriptEngine();
				LogWriter.info(factory.getLanguageName() + ": " + ".js");
				manager.registerEngineName("ecmascript", factory);
				manager.registerEngineExtension("js", factory);
				manager.registerEngineMimeType("application/ecmascript", factory);
				languages.put(factory.getLanguageName(), ".js");
				factories.put(factory.getLanguageName().toLowerCase(), factory);
			} 
		}
		catch (Throwable e) {
			
		}
		try {
			Class c = Class.forName("org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory");
			ScriptEngineFactory factory = (ScriptEngineFactory) c.newInstance();
			factory.getScriptEngine();
			LogWriter.info(factory.getLanguageName() + ": " + ".ktl");
			manager.registerEngineName("kotlin", factory);
			manager.registerEngineExtension("ktl", factory);
			manager.registerEngineMimeType("application/kotlin", factory);
			languages.put(factory.getLanguageName(), ".ktl");
			factories.put(factory.getLanguageName().toLowerCase(), factory);
		} catch (Throwable e) {

		}
		try {
			Class c = Class.forName("noppes.scriptengines.ScriptEngines");
			List<ScriptEngineFactory> seFactories = (List<ScriptEngineFactory>) c.getDeclaredField("factories").get(null);
			for(ScriptEngineFactory fac : seFactories){
				if(fac.getExtensions().size() == 0 || languages.containsKey(fac.getLanguageName()))
					continue;

				if(!(fac.getScriptEngine() instanceof Invocable) && !fac.getLanguageName().equals("lua"))
					continue;
				String ext = "." + fac.getExtensions().get(0).toLowerCase();
				LogWriter.info(fac.getLanguageName() + ": " + ext);
				languages.put(fac.getLanguageName(), ext);
				factories.put(fac.getLanguageName().toLowerCase(), fac);
			}
		} catch (Throwable e) {

		}

		for(ScriptEngineFactory fac : manager.getEngineFactories()){
			try {
				if(fac.getExtensions().size() == 0 || languages.containsKey(fac.getLanguageName()))
					continue;
				
				if(!(fac.getScriptEngine() instanceof Invocable) && !fac.getLanguageName().equals("lua"))
					continue;
				String ext = "." + fac.getExtensions().get(0).toLowerCase();
				LogWriter.info(fac.getLanguageName() + ": " + ext);
				languages.put(fac.getLanguageName(), ext);
				factories.put(fac.getLanguageName().toLowerCase(), fac);
			}
			catch(Throwable e) {
				LogWriter.except(e);
			}
		}
	}

	public void loadCategories(){
		dir = new File(CustomNpcs.getWorldSaveDirectory(), "scripts");
		if(!dir.exists())
			dir.mkdirs();
		if(!worldDataFile().exists())
			shouldSave = true;
		WorldWrapper.tempData.clear();
		
		scripts.clear();
		for(String language : languages.keySet()){
			String ext = languages.get(language);
			File scriptDir = new File(dir,language.toLowerCase());
			if(!scriptDir.exists())
				scriptDir.mkdir();
			else
				loadDir(scriptDir, "", ext);
		}
		lastLoaded = System.currentTimeMillis();
	}
	
	private void loadDir(File dir, String name, String ext){
		for(File file : dir.listFiles()){
			String filename = name + file.getName().toLowerCase();
			if(file.isDirectory()){
				loadDir(file, filename + "/", ext);
				continue;
			}
			if(!filename.endsWith(ext))
				continue;
			try {
				scripts.put(filename, readFile(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean loadStoredData(){
		this.compound = new CompoundNBT();
    	File file = worldDataFile();
        try {
        	if(!file.exists())
        		return false;
        	this.compound = NBTJsonUtil.LoadFile(file);
    		shouldSave = false;
		} 
        catch (Exception e) {
			LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
        	return false;
        }
        return true;
	}
	
	private File worldDataFile(){
		return new File(dir, "world_data.json");
	}
	
	private File playerScriptsFile(){
		return new File(dir, "player_scripts.json");
	}
	
	private File forgeScriptsFile(){
		return new File(dir, "forge_scripts.json");
	}
	
	public boolean loadPlayerScripts(){
		this.playerScripts.clear();
    	File file = playerScriptsFile();
        try {
        	if(!file.exists())
        		return false;
        	this.playerScripts.load(NBTJsonUtil.LoadFile(file));
		} 
        catch (Exception e) {
			LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
        	return false;
        }
        return true;
	}
	
	public void setPlayerScripts(CompoundNBT compound){
    	this.playerScripts.load(compound);
    	
    	File file = playerScriptsFile();
		try {
			NBTJsonUtil.SaveFile(file, compound);
			lastPlayerUpdate = System.currentTimeMillis();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonException e) {
			e.printStackTrace();
		}
	}
	
	public boolean loadForgeScripts(){
		this.forgeScripts.clear();
    	File file = forgeScriptsFile();
        try {
        	if(!file.exists())
        		return false;
        	this.forgeScripts.load(NBTJsonUtil.LoadFile(file));
		} 
        catch (Exception e) {
			LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
        	return false;
        }
        return true;
	}
	
	public void setForgeScripts(CompoundNBT compound){
    	this.forgeScripts.load(compound);
    	
    	File file = forgeScriptsFile();
		try {
			NBTJsonUtil.SaveFile(file, compound);
			this.forgeScripts.lastInited = -1;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonException e) {
			e.printStackTrace();
		}
	}
	
	private String readFile(File file) throws IOException {
	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}

	public ScriptEngine getEngineByName(String language) {
		ScriptEngineFactory fac = factories.get(language.toLowerCase());
		if(fac == null)
			return null;
		return fac.getScriptEngine();
	}

	public ListNBT nbtLanguages() {
		ListNBT list = new ListNBT();
		for(String language : languages.keySet()){
			CompoundNBT compound = new CompoundNBT();
			ListNBT scripts = new ListNBT();
			for(String script : getScripts(language)){
				scripts.add(StringNBT.valueOf(script));
			}
			compound.put("Scripts", scripts);
			compound.putString("Language", language);
			list.add(compound);
		}
		return list;
	}
	
	private List<String> getScripts(String language){
		List<String> list = new ArrayList<String>();
		String ext = languages.get(language);
		if(ext == null)
			return list;
		for(String script : scripts.keySet()){
			if(script.endsWith(ext)){
				list.add(script);
			}
		}
		return list;
	}

	@SubscribeEvent
	public void saveWorld(WorldEvent.Save event){
		if(!shouldSave || !(event.getWorld() instanceof ServerWorld) || event.getWorld().dimensionType() != DimensionType.DEFAULT_OVERWORLD)
			return;
		
		try {
			NBTJsonUtil.SaveFile(worldDataFile(), compound.copy());
		} 
		catch (Exception e) {
			LogWriter.except(e);
		} 
		
		shouldSave = false;
	}
}
