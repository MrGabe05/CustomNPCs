package noppes.npcs.entity.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.base.MoreObjects;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Event;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.EntityNPCInterface;

public class DataScript implements IScriptHandler{
	private List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
	
	private String scriptLanguage = "ECMAScript";
	private final EntityNPCInterface npc;
	private boolean enabled = false;
	
	public long lastInited = -1;
	
	public DataScript(EntityNPCInterface npc) {
		this.npc = npc;
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

	@Override
	public void runScript(EnumScriptType type, Event event){
		if(!isEnabled())
			return;
		if(ScriptController.Instance.lastLoaded > lastInited){
			lastInited = ScriptController.Instance.lastLoaded;
			if(type != EnumScriptType.INIT)
				EventHooks.onNPCInit(npc);
		}
		for(ScriptContainer script : scripts){
			script.run(type, event);
		}
	}
	
	public boolean isEnabled(){
		return enabled && ScriptController.HasStart && !npc.level.isClientSide;
	}

	@Override
	public boolean isClient() {
		return npc.isClientSide();
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
		BlockPos pos = npc.blockPosition();
		return MoreObjects.toStringHelper(npc).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
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
