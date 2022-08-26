package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.google.common.base.MoreObjects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Event;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;

public class ForgeScriptData implements IScriptHandler {

	private List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
	
	private String scriptLanguage = "ECMAScript";
	
	public long lastInited = -1;
	public boolean hadInteract = true;
	private boolean enabled = false;
	
	
	public void clear() {
		scripts = new ArrayList<ScriptContainer>();
	}

	public void load(CompoundNBT compound) {
		scripts = NBTTags.GetScript(compound.getList("Scripts", 10), this);
		scriptLanguage = compound.getString("ScriptLanguage");
		enabled = compound.getBoolean("ScriptEnabled");
	}

	public CompoundNBT save(CompoundNBT compound) {
		compound.put("Scripts", NBTTags.NBTScript(scripts));
		compound.putString("ScriptLanguage", scriptLanguage);
		compound.putBoolean("ScriptEnabled", enabled);		
		return compound;
	}

	public void runScript(EnumScriptType type, Event event){
		//not used
	}

	public void runScript(String type, Event event){
		if(!isEnabled())
			return;
		CustomNpcs.Server.submit(() -> {
			if(ScriptController.Instance.lastLoaded > lastInited){
				lastInited = ScriptController.Instance.lastLoaded;
				if(!type.equals("init"))
					EventHooks.onForgeInit(this);
			}
			for(ScriptContainer script : scripts){
				script.run(type, event);
			}
		});
	}
	
	public boolean isEnabled(){
		return enabled && ScriptController.HasStart && scripts.size() > 0;
	}

	@Override
	public boolean isClient() {
		return false;
	}

	@Override
	public boolean getEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean bo) {
		enabled = bo;
	}

	@Override
	public String getLanguage() {
		return scriptLanguage;
	}

	@Override
	public void setLanguage(String lang) {
		scriptLanguage = lang;
	}

	@Override
	public List<ScriptContainer> getScripts() {
		return scripts;
	}

	@Override
	public String noticeString() {
		return "ForgeScript";
	}

	@Override
	public Map<Long, String> getConsoleText(){
		Map<Long, String> map = new TreeMap<Long, String>();
		int tab = 0;
		for(ScriptContainer script : getScripts()){
			tab++;
			for(Entry<Long, String> entry : script.console.entrySet()){
				map.put(entry.getKey(), " tab " + tab + ":\n" + entry.getValue());
			}
		}
		return map;
	}

	@Override
	public void clearConsole() {
		for(ScriptContainer script : getScripts()){
			script.console.clear();
		}
	}

}
