package noppes.npcs.blocks.tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.base.MoreObjects;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Event;
import noppes.npcs.CustomBlocks;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.wrapper.BlockScriptedDoorWrapper;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptBlockHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.data.DataTimers;

public class TileScriptedDoor extends TileDoor implements ITickableTileEntity, IScriptBlockHandler{
	public List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
	public boolean shouldRefreshData = false;
	
	public String scriptLanguage = "ECMAScript";
	public boolean enabled = false;

	private IBlock blockDummy = null;
	public DataTimers timers = new DataTimers(this);
	
	public long lastInited = -1;
	
	private short tickCount = 0;

	public int newPower = 0; //used for block redstone event
	public int prevPower = 0; //used for block redstone event

	public float blockHardness = 5;
	public float blockResistance = 10;

	public TileScriptedDoor(){
		super(CustomBlocks.tile_scripteddoor);
	}
		
	public IBlock getBlock(){
		if(blockDummy == null)
			blockDummy = new BlockScriptedDoorWrapper(getLevel(), CustomBlocks.scripted_door, getBlockPos());
		return blockDummy;
	}
    
	@Override
    public void load(BlockState state, CompoundNBT compound){
		super.load(state, compound);
		setNBT(compound);
		timers.load(compound);
    }
	
	public void setNBT(CompoundNBT compound){
		scripts = NBTTags.GetScript(compound.getList("Scripts", 10), this);
		scriptLanguage = compound.getString("ScriptLanguage");
		enabled = compound.getBoolean("ScriptEnabled");
		prevPower = compound.getInt("BlockPrevPower");

		if(compound.contains("BlockHardness")){
			blockHardness = compound.getFloat("BlockHardness");
			blockResistance = compound.getFloat("BlockResistance");
		}
	}
	
	@Override
    public CompoundNBT save(CompoundNBT compound){
		getNBT(compound);
		timers.save(compound);
    	return super.save(compound);
    }
	
	public CompoundNBT getNBT(CompoundNBT compound){
		compound.put("Scripts", NBTTags.NBTScript(scripts));
		compound.putString("ScriptLanguage", scriptLanguage);
		compound.putBoolean("ScriptEnabled", enabled);
		compound.putInt("BlockPrevPower", prevPower);
		compound.putFloat("BlockHardness", blockHardness);
		compound.putFloat("BlockResistance", blockResistance);
		return compound;
	}

	@Override
	public void runScript(EnumScriptType type, Event event) {
		if(!isEnabled())
			return;
		if(ScriptController.Instance.lastLoaded > lastInited){
			lastInited = ScriptController.Instance.lastLoaded;
			if(type != EnumScriptType.INIT)
				EventHooks.onScriptBlockInit(this);
		}
		
		for(ScriptContainer script : scripts){			
			script.run(type, event);
		}
	}

	private boolean isEnabled() {
		return enabled && ScriptController.HasStart && !level.isClientSide;
	}

	@Override
	public void tick() {
		super.tick();
		tickCount++;

        if (prevPower != newPower){
        	EventHooks.onScriptBlockRedstonePower(this, prevPower, newPower);
        	prevPower = newPower;
        }
        
		timers.update();
		if(tickCount >= 10){
			EventHooks.onScriptBlockUpdate(this);
			tickCount = 0;
		}
	}

	@Override
	public boolean isClient() {
		return getLevel().isClientSide;
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
		BlockPos pos = getBlockPos();
		return MoreObjects.toStringHelper(this).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
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
