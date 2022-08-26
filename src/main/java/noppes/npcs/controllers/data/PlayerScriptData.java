package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.base.MoreObjects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Event;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;

public class PlayerScriptData implements IScriptHandler{
	private List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
	
	private String scriptLanguage = "ECMAScript";
	private PlayerEntity player;
	private IPlayer playerAPI;
	private long lastPlayerUpdate = 0;
	
	public long lastInited = -1;
	public boolean hadInteract = true;
	private boolean enabled = false;

	private static Map<Long, String> console = new TreeMap<Long, String>();
	private static List<Integer> errored = new ArrayList<Integer>();
	
	
	public PlayerScriptData(PlayerEntity player) {
		this.player = player;
	}
	
	public void clear() {
		console = new TreeMap<Long, String>();
		errored = new ArrayList<Integer>();
		scripts = new ArrayList<ScriptContainer>();
	}

	public void load(CompoundNBT compound) {
		scripts = NBTTags.GetScript(compound.getList("Scripts", 10), this);
		scriptLanguage = compound.getString("ScriptLanguage");
		enabled = compound.getBoolean("ScriptEnabled");		
		console = NBTTags.GetLongStringMap(compound.getList("ScriptConsole", 10));
	}

	public CompoundNBT save(CompoundNBT compound) {
		compound.put("Scripts", NBTTags.NBTScript(scripts));
		compound.putString("ScriptLanguage", scriptLanguage);
		compound.putBoolean("ScriptEnabled", enabled);
		compound.put("ScriptConsole", NBTTags.NBTLongStringMap(console));
		return compound;
	}

	@Override
	public void runScript(EnumScriptType type, Event event){
		if(!isEnabled())
			return;
		if(ScriptController.Instance.lastLoaded > lastInited || ScriptController.Instance.lastPlayerUpdate > lastPlayerUpdate){
			lastInited = ScriptController.Instance.lastLoaded;
			errored.clear();
			if(player != null) {
				scripts.clear();
				for(ScriptContainer script : ScriptController.Instance.playerScripts.scripts){
					ScriptContainer s = new ScriptContainer(this);
					s.load(script.save(new CompoundNBT()));
					scripts.add(s);
				}
			}
			lastPlayerUpdate = ScriptController.Instance.lastPlayerUpdate;
			if(type != EnumScriptType.INIT)
				EventHooks.onPlayerInit(this);
		}
		for(int i = 0; i < scripts.size(); i++){
			ScriptContainer script = scripts.get(i);
			if(errored.contains(i))
				continue;
			script.run(type, event);
			if(script.errored){
				errored.add(i);
			}
			for(Entry<Long, String> entry : script.console.entrySet()){
				if(!console.containsKey(entry.getKey()))
					console.put(entry.getKey(), " tab " + (i + 1) + ":\n" + entry.getValue());
			}
			script.console.clear();
		}
	}
	
	public boolean isEnabled(){
		return ScriptController.Instance.playerScripts.enabled && ScriptController.HasStart && (player == null || !player.level.isClientSide);
	}

	@Override
	public boolean isClient() {
		return player.level.isClientSide();
	}

	@Override
	public boolean getEnabled() {
		return ScriptController.Instance.playerScripts.enabled;
	}

	@Override
	public void setEnabled(boolean bo) {
		enabled = bo;
	}

	@Override
	public String getLanguage() {
		return ScriptController.Instance.playerScripts.scriptLanguage;
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
		if(player == null) {
			return "Global script";
		}
		BlockPos pos = player.blockPosition();
		return MoreObjects.toStringHelper(player).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
	}
	
	public IPlayer getPlayer(){
		if(playerAPI == null)
			playerAPI = (IPlayer) NpcAPI.Instance().getIEntity(player);
		return playerAPI;
	}

	@Override
	public Map<Long, String> getConsoleText(){
		return console;
	}

	@Override
	public void clearConsole() {
		console.clear();
	}

}
