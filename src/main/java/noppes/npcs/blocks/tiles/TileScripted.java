package noppes.npcs.blocks.tiles;

import com.google.common.base.MoreObjects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.block.ITextPlane;
import noppes.npcs.api.wrapper.BlockScriptedWrapper;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptBlockHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TileScripted extends TileNpcEntity implements ITickableTileEntity, IScriptBlockHandler {
	public List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
	
	public String scriptLanguage = "ECMAScript";
	public boolean enabled = false;

	private IBlock blockDummy = null;
	public DataTimers timers = new DataTimers(this);
	
	public long lastInited = -1;
	
	private short tickCount = 0;

	public ItemStack itemModel = new ItemStack(CustomBlocks.scripted);
	public Block blockModel = null;
	
	public boolean needsClientUpdate = false;

	public int powering = 0;
	public int activePowering = 0;
	public int newPower = 0; //used for block redstone event
	public int prevPower = 0; //used for block redstone event

	public boolean isPassible = false;
	public boolean isLadder = false;
	public int lightValue = 0;

	public float blockHardness = 5;
	public float blockResistance = 10;

	public int rotationX = 0, rotationY = 0, rotationZ = 0;
	public float scaleX = 1, scaleY = 1, scaleZ = 1;
	
	public TileEntity renderTile;
	public boolean renderTileErrored = true;
	public ITickableTileEntity renderTileUpdate = null;

	public TextPlane text1 = new TextPlane();
	public TextPlane text2 = new TextPlane();
	public TextPlane text3 = new TextPlane();
	public TextPlane text4 = new TextPlane();
	public TextPlane text5 = new TextPlane();
	public TextPlane text6 = new TextPlane();

	public TileScripted(){
		super(CustomBlocks.tile_scripted);
	}
	
	public IBlock getBlock(){
		if(blockDummy == null)
			blockDummy = new BlockScriptedWrapper(getLevel(), CustomBlocks.scripted, getBlockPos());
		return blockDummy;
	}
	
	@Override
    public void load(BlockState state, CompoundNBT compound){
		super.load(state, compound);
		setNBT(compound);
		setDisplayNBT(compound);
		timers.load(compound);
    }
	
	public void setNBT(CompoundNBT compound){
		scripts = NBTTags.GetScript(compound.getList("Scripts", 10), this);
		scriptLanguage = compound.getString("ScriptLanguage");
		enabled = compound.getBoolean("ScriptEnabled");
		activePowering = powering = compound.getInt("BlockPowering");
		prevPower = compound.getInt("BlockPrevPower");
		
		if(compound.contains("BlockHardness")){
			blockHardness = compound.getFloat("BlockHardness");
			blockResistance = compound.getFloat("BlockResistance");
		}
		
	}
	public void setDisplayNBT(CompoundNBT compound){
		itemModel = ItemStack.of(compound.getCompound("ScriptBlockModel"));
		if(itemModel.isEmpty())
			itemModel = new ItemStack(CustomBlocks.scripted);
		if(compound.contains("ScriptBlockModelBlock"))
			blockModel = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compound.getString("ScriptBlockModelBlock")));
		renderTileUpdate = null;
		renderTile = null;
		renderTileErrored = false;
		
		lightValue = compound.getInt("LightValue");
		isLadder = compound.getBoolean("IsLadder");
		isPassible = compound.getBoolean("IsPassible");

		rotationX = compound.getInt("RotationX");
		rotationY = compound.getInt("RotationY");
		rotationZ = compound.getInt("RotationZ");

		scaleX = compound.getFloat("ScaleX");
		scaleY = compound.getFloat("ScaleY");
		scaleZ = compound.getFloat("ScaleZ");

		if(scaleX <= 0)
			scaleX = 1;
		if(scaleY <= 0)
			scaleY = 1;
		if(scaleZ <= 0)
			scaleZ = 1;

		if(compound.contains("Text3")) {
			text1.setNBT(compound.getCompound("Text1"));
			text2.setNBT(compound.getCompound("Text2"));
			text3.setNBT(compound.getCompound("Text3"));
			text4.setNBT(compound.getCompound("Text4"));
			text5.setNBT(compound.getCompound("Text5"));
			text6.setNBT(compound.getCompound("Text6"));
		}
	}

	@Override
    public CompoundNBT save(CompoundNBT compound){
		getNBT(compound);
		getDisplayNBT(compound);
		timers.save(compound);
    	return super.save(compound);
    }
	
	public CompoundNBT getNBT(CompoundNBT compound){
		compound.put("Scripts", NBTTags.NBTScript(scripts));
		compound.putString("ScriptLanguage", scriptLanguage);
		compound.putBoolean("ScriptEnabled", enabled);
		compound.putInt("BlockPowering", powering);
		compound.putInt("BlockPrevPower", prevPower);
		compound.putFloat("BlockHardness", blockHardness);
		compound.putFloat("BlockResistance", blockResistance);
		return compound;
	}
	
	public CompoundNBT getDisplayNBT(CompoundNBT compound){
		CompoundNBT itemcompound = new CompoundNBT();
		itemModel.save(itemcompound);
		if(blockModel != null){
	        ResourceLocation resourcelocation = ForgeRegistries.BLOCKS.getKey(blockModel);
	        compound.putString("ScriptBlockModelBlock", resourcelocation == null ? "" : resourcelocation.toString());
		}
		compound.put("ScriptBlockModel", itemcompound);
		compound.putInt("LightValue", lightValue);
		compound.putBoolean("IsLadder", isLadder);
		compound.putBoolean("IsPassible", isPassible);

		compound.putInt("RotationX", rotationX);
		compound.putInt("RotationY", rotationY);
		compound.putInt("RotationZ", rotationZ);

		compound.putFloat("ScaleX", scaleX);
		compound.putFloat("ScaleY", scaleY);
		compound.putFloat("ScaleZ", scaleZ);

		compound.put("Text1", text1.getNBT());
		compound.put("Text2", text2.getNBT());
		compound.put("Text3", text3.getNBT());
		compound.put("Text4", text4.getNBT());
		compound.put("Text5", text5.getNBT());
		compound.put("Text6", text6.getNBT());
		
		return compound;
	}

	private boolean isEnabled() {
		return enabled && ScriptController.HasStart && !level.isClientSide;
	}

	@Override
	public void tick() {
		if(renderTileUpdate != null){
			try{
				renderTileUpdate.tick();
			}
			catch(Exception e){
				renderTileUpdate = null;
			}
		}
		tickCount++;
        if (prevPower != newPower && powering <= 0){
        	EventHooks.onScriptBlockRedstonePower(this, prevPower, newPower);
        	prevPower = newPower;
        }
        
		timers.update();
		if(tickCount >= 10){
			EventHooks.onScriptBlockUpdate(this);
			tickCount = 0;
			if(needsClientUpdate){
	    		setChanged();
	    		BlockState state = level.getBlockState(worldPosition);
	    		level.sendBlockUpdated(worldPosition, state, state, 3);
				needsClientUpdate = false;
			}
		}
	}

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
    	handleUpdateTag(level.getBlockState(pkt.getPos()), pkt.getTag());
    }
    
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag){
    	int light = lightValue;
    	setDisplayNBT(tag);
    	if(light != lightValue)
    		level.getLightEngine().checkBlock(worldPosition);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket(){
    	return new SUpdateTileEntityPacket(worldPosition, 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag(){
    	CompoundNBT compound = new CompoundNBT();
    	compound.putInt("x", this.worldPosition.getX());
    	compound.putInt("y", this.worldPosition.getY());
    	compound.putInt("z", this.worldPosition.getZ());
    	getDisplayNBT(compound);
    	return compound;
    }

	public void setItemModel(ItemStack item, Block b) {
		if(item == null || item.isEmpty()){
			item = new ItemStack(CustomBlocks.scripted);
		}
		if(NoppesUtilPlayer.compareItems(item, itemModel, false, false) && b != blockModel)
			return;
		
		itemModel = item;
		blockModel = b;
		needsClientUpdate = true;
	}

	public void setLightValue(int value){
		if(value == lightValue)
			return;
		lightValue = ValueUtil.CorrectInt(value, 0, 15);
		needsClientUpdate = true;
	}

	public void setRedstonePower(int strength){
		if(powering == strength)
			return;
		//using activePowering to prevent the RedstonePower script event from going crazy
		prevPower = activePowering = ValueUtil.CorrectInt(strength, 0, 15);
        level.updateNeighborsAt(worldPosition, CustomBlocks.scripted);
        powering = activePowering;
	}
	
	public void setScale(float x, float y, float z){
		if(scaleX == x && scaleY == y && scaleZ == z)
			return;
		scaleX = ValueUtil.correctFloat(x, 0, 10);
		scaleY = ValueUtil.correctFloat(y, 0, 10);
		scaleZ = ValueUtil.correctFloat(z, 0, 10);
		needsClientUpdate = true;
	}
	
	public void setRotation(int x, int y, int z){
		if(rotationX == x && rotationY == y && rotationZ == z)
			return;
		rotationX = ValueUtil.CorrectInt(x, 0, 359);
		rotationY = ValueUtil.CorrectInt(y, 0, 359);
		rotationZ = ValueUtil.CorrectInt(z, 0, 359);
		needsClientUpdate = true;
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
	public String noticeString() {
		BlockPos pos = getBlockPos();
		return MoreObjects.toStringHelper(this).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
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
	
    @OnlyIn(Dist.CLIENT)
	@Override
    public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox(){
    	return VoxelShapes.block().bounds().move(getBlockPos());
    }
    
    public class TextPlane implements ITextPlane {
    	public boolean textHasChanged = true;
        public TextBlock textBlock;
        
    	public String text = "";
    	public int rotationX = 0, rotationY = 0, rotationZ = 0;
    	public float offsetX = 0, offsetY = 0f, offsetZ = 0.5f;
    	public float scale = 1;
    	
		@Override
		public String getText() {
			return text;
		}
		@Override
		public void setText(String text) {
			if(this.text.equals(text))
				return;
			this.text = text;
			textHasChanged = true;
			needsClientUpdate = true;
		}
		@Override
		public int getRotationX() {
			return rotationX;
		}
		@Override
		public int getRotationY() {
			return rotationY;
		}
		@Override
		public int getRotationZ() {
			return rotationZ;
		}
		@Override
		public void setRotationX(int x) {
			x = ValueUtil.CorrectInt(x % 360, 0, 359);
			if(rotationX == x)
				return;
			rotationX = x;
			needsClientUpdate = true;
		}
		@Override
		public void setRotationY(int y) {
			y = ValueUtil.CorrectInt(y % 360, 0, 359);
			if(rotationY == y)
				return;
			rotationY = y;
			needsClientUpdate = true;
		}
		@Override
		public void setRotationZ(int z) {
			z = ValueUtil.CorrectInt(z % 360, 0, 359);
			if(rotationZ == z)
				return;
			rotationZ = z;
			needsClientUpdate = true;
		}
		@Override
		public float getOffsetX() {
			return offsetX;
		}
		@Override
		public float getOffsetY() {
			return offsetY;
		}
		@Override
		public float getOffsetZ() {
			return offsetZ;
		}
		@Override
		public void setOffsetX(float x) {
			x = ValueUtil.correctFloat(x, -1, 1);
			if(offsetX == x)
				return;
			offsetX = x;
			needsClientUpdate = true;
		}
		@Override
		public void setOffsetY(float y) {
			y = ValueUtil.correctFloat(y, -1, 1);
			if(offsetY == y)
				return;
			offsetY = y;
			needsClientUpdate = true;
		}
		@Override
		public void setOffsetZ(float z) {
			z = ValueUtil.correctFloat(z, -1, 1);
			if(offsetZ == z)
				return;
			System.out.println(rotationZ);
			offsetZ = z;
			needsClientUpdate = true;
		}
		@Override
		public float getScale() {
			return scale;
		}
		@Override
		public void setScale(float scale) {
			if(this.scale == scale)
				return;
			this.scale = scale;
			needsClientUpdate = true;
		}
		
		public CompoundNBT getNBT() {
			CompoundNBT compound = new CompoundNBT();
			compound.putString("Text", text);
			compound.putInt("RotationX", rotationX);
			compound.putInt("RotationY", rotationY);
			compound.putInt("RotationZ", rotationZ);
			compound.putFloat("OffsetX", offsetX);
			compound.putFloat("OffsetY", offsetY);
			compound.putFloat("OffsetZ", offsetZ);
			compound.putFloat("Scale", scale);
			return compound;
		}
		
		public void setNBT(CompoundNBT compound) {
			setText(compound.getString("Text"));
			rotationX = compound.getInt("RotationX");
			rotationY = compound.getInt("RotationY");
			rotationZ = compound.getInt("RotationZ");
			offsetX = compound.getFloat("OffsetX");
			offsetY = compound.getFloat("OffsetY");
			offsetZ = compound.getFloat("OffsetZ");
			scale = compound.getFloat("Scale");
		}
    }
}
