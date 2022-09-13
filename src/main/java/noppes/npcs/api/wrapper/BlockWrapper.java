package noppes.npcs.api.wrapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.api.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.blocks.BlockScripted;
import noppes.npcs.blocks.BlockScriptedDoor;
import noppes.npcs.blocks.tiles.TileNpcEntity;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LRUHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BlockWrapper implements IBlock{
	private static final Map<String, BlockWrapper> blockCache = new LRUHashMap<String, BlockWrapper>(400);
	protected final IWorld level;
	protected final Block block;
	protected final BlockPos pos;
	protected final BlockPosWrapper bPos;
	protected TileEntity tile;
	protected TileNpcEntity storage;
	
	private final IData tempdata = new IData() {
		
		@Override
		public void remove(String key) {
			if(storage == null)
				return;
			storage.tempData.remove(key);
		}
		
		@Override
		public void put(String key, Object value) {
			if(storage == null)
				return;
			storage.tempData.put(key, value);
		}
		
		@Override
		public boolean has(String key) {
			if(storage == null)
				return false;
			return storage.tempData.containsKey(key);
		}
		
		@Override
		public Object get(String key) {
			if(storage == null)
				return null;
			return storage.tempData.get(key);
		}
		
		@Override
		public void clear() {
			if(storage == null)
				return;
			storage.tempData.clear();
		}

		@Override
		public String[] getKeys() {
			return storage.tempData.keySet().toArray(new String[storage.tempData.size()]);
		}
	};
	
	private final IData storeddata = new IData() {

		@Override
		public void put(String key, Object value) {
			CompoundNBT compound = getNBT();
			if(compound == null)
				return;
			if(value instanceof Number)
				compound.putDouble(key, ((Number) value).doubleValue());
			else if(value instanceof String)
				compound.putString(key, (String)value);
		}

		@Override
		public Object get(String key) {
			CompoundNBT compound = getNBT();
			if(compound == null)
				return null;
			if(!compound.contains(key))
				return null;
			INBT base = compound.get(key);
			if(base instanceof NumberNBT)
				return ((NumberNBT)base).getAsDouble();
			return base.getAsString();
		}

		@Override
		public void remove(String key) {
			CompoundNBT compound = getNBT();
			if(compound == null)
				return;
			compound.remove(key);
		}

		@Override
		public boolean has(String key) {
			CompoundNBT compound = getNBT();
			if(compound == null)
				return false;
			return compound.contains(key);
		}

		@Override
		public void clear() {
			if(tile == null)
				return;
			tile.getTileData().put("CustomNPCsData", new CompoundNBT());
		}
		
		private CompoundNBT getNBT(){
			if(tile == null)
				return null;			
			CompoundNBT compound = tile.getTileData().getCompound("CustomNPCsData");
			if(compound.isEmpty() && !tile.getTileData().contains("CustomNPCsData")){
				tile.getTileData().put("CustomNPCsData", compound);
			}
			return compound;
		}

		@Override
		public String[] getKeys() {
			CompoundNBT compound = getNBT();
			if(compound == null)
				return new String[0];
			return compound.getAllKeys().toArray(new String[compound.getAllKeys().size()]);
		}
	};
	
	protected BlockWrapper(World level, Block block, BlockPos pos){
		this.level = NpcAPI.Instance().getIWorld((ServerWorld)level);
		this.block = block;
		this.pos = pos;
		this.bPos = new BlockPosWrapper(pos);
		this.setTile(level.getBlockEntity(pos));
	}

	@Override
	public int getX(){
		return pos.getX();
	}

	@Override
	public int getY(){
		return pos.getY();
	}

	@Override
	public int getZ(){
		return pos.getZ();
	}

	@Override
	public IPos getPos() {
		return bPos;
	}

	@Override
	public Object getProperty(String name) {
		BlockState state = getMCBlockState();
		for(Property p : state.getProperties()){
			if(p.getName().equalsIgnoreCase(name)){
				return state.getValue(p);
			}
		}
		throw new CustomNPCsException("Unknown property: " + name);
	}

	@Override
	public void setProperty(String name, Object val) {
		if(!(val instanceof Comparable)){
			throw new CustomNPCsException("Not a valid property value: " + val);
		}
		BlockState state = getMCBlockState();
		for(Property<?> p : state.getProperties()){
			if(p.getName().equalsIgnoreCase(name)){
				//state.setValue(p, (Comparable)val); //TODO gives compile error
				return;
			}
		}
		throw new CustomNPCsException("Unknown property: " + name);
	}

	@Override
	public String[] getProperties() {
		Collection<Property<?>> props = getMCBlockState().getProperties();
		List<String> list = new ArrayList<>();
		for(Property prop : props){
			list.add(prop.getName());
		}
		return list.toArray(new String[list.size()]);
	}

	@Override
	public void remove(){
		level.getMCWorld().removeBlock(pos, false);
	}

	@Override
	public boolean isRemoved(){
		BlockState state = level.getMCWorld().getBlockState(pos);
		if(state == null)
			return true;
		return state.getBlock() != block;
	}

	@Override
	public boolean isAir(){
		return block.isAir(level.getMCWorld().getBlockState(pos), level.getMCWorld(), pos);
	}
	
	@Override
	public BlockWrapper setBlock(String name){
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
		if(block == null)
			return this;
		level.getMCWorld().setBlock(pos, block.defaultBlockState(), 2);
		return new BlockWrapper(level.getMCWorld(), block, pos);
	}
	
	@Override
	public BlockWrapper setBlock(IBlock block){
		level.getMCWorld().setBlock(pos, block.getMCBlock().defaultBlockState(), 2);
		return new BlockWrapper(level.getMCWorld(), block.getMCBlock(), pos);
	}

	@Override
	public boolean isContainer(){
		if(tile == null || !(tile instanceof IInventory))
			return false;
		return ((IInventory)tile).getContainerSize() > 0;
	}
	
	@Override
	public IContainer getContainer(){
		if(!isContainer())
			throw new CustomNPCsException("This block is not a container");
		return NpcAPI.Instance().getIContainer((IInventory) tile);
	}

	@Override
	public IData getTempdata(){
		return tempdata;
	}

	@Override
	public IData getStoreddata(){
		return storeddata;
	}

	@Override
	public String getName(){
		return ForgeRegistries.BLOCKS.getKey(block).toString();
	}

	@Override
	public String getDisplayName() {
		if(tile == null || !(tile instanceof INameable))
			return getName();
		return ((INameable)tile).getDisplayName().getString();
	}
	
	@Override
	public IWorld getWorld() {
		return level;
	}

	@Override
	public Block getMCBlock(){
		return block;
	}

	@Deprecated
	public static IBlock createNew(World level, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		String key = state + pos.toString();
		BlockWrapper b = blockCache.get(key);
		if(b != null){
			b.setTile(level.getBlockEntity(pos));
			return b;
		}
		
		if(block instanceof BlockScripted)
			b = new BlockScriptedWrapper(level, block, pos);
		else if(block instanceof BlockScriptedDoor)
			b = new BlockScriptedDoorWrapper(level, block, pos);
		else if(block instanceof IFluidBlock)
			b = new BlockFluidContainerWrapper(level, block, pos);
		else
			b = new BlockWrapper(level, block, pos);
		blockCache.put(key, b);	
		
		return b;
	}
	
	public static void clearCache(){
		blockCache.clear();
	}

	@Override
	public boolean hasTileEntity() {
		return tile != null;
	}
	
	protected void setTile(TileEntity tile){
		this.tile = tile;
		if(tile instanceof TileNpcEntity)
			storage = (TileNpcEntity)tile;
	}
	
	@Override
	public INbt getBlockEntityNBT() {
		CompoundNBT compound = new CompoundNBT();
		tile.save(compound);
		return NpcAPI.Instance().getINbt(compound);
	}

	@Override
	public void setTileEntityNBT(INbt nbt){
		tile.load(getMCBlockState(), nbt.getMCNBT());
		tile.setChanged();
		BlockState state = this.level.getMCWorld().getBlockState(pos);
		level.getMCWorld().sendBlockUpdated(pos, state, state, 3);
	}

	@Override
	public TileEntity getMCTileEntity() {
		return tile;
	}

	@Override
	public BlockState getMCBlockState() {
		return this.level.getMCWorld().getBlockState(pos);
	}

	@Override
	public void blockEvent(int type, int data) {
		level.getMCWorld().blockEvent(pos, getMCBlock(), type, data);
	}

	@Override
	public void interact(int side) {
		PlayerEntity player = EntityNPCInterface.GenericPlayer;
		World w = level.getMCWorld();
		player.setLevel(w);
		player.setPos(pos.getX(), pos.getY(), pos.getZ());
		getMCBlockState().use(w, EntityNPCInterface.CommandPlayer, Hand.MAIN_HAND, new BlockRayTraceResult(Vector3d.ZERO, Direction.from3DDataValue(side), pos, true));
	}
}
