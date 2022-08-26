package noppes.npcs.schematics;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SchematicWrapper {
	public static final int buildSize = 10000;
	
	private BlockPos offset = BlockPos.ZERO;
	private BlockPos start = BlockPos.ZERO;

	public ISchematic schema;

	public int buildPos, size;
	
	public int rotation = 0;
	
	private World level;

	public boolean isBuilding = false;
	public boolean firstLayer = true;
	
	private Map<ChunkPos, CompoundNBT>[] tileEntities;

	public SchematicWrapper(ISchematic schematic){
		this.schema = schematic;
		
		size = schematic.getWidth() * schematic.getHeight() * schematic.getLength();

		tileEntities = new Map[schematic.getHeight()];
		for (int i = 0; i < schematic.getBlockEntitySize(); i++) {
			CompoundNBT teTag = schematic.getBlockEntity(i);
			int x = teTag.getInt("x");
			int y = teTag.getInt("y");
			int z = teTag.getInt("z");
			Map<ChunkPos, CompoundNBT> map = tileEntities[y];
			if(map == null)
				tileEntities[y] = map = new HashMap<ChunkPos, CompoundNBT>();
			map.put(new ChunkPos(x, z), teTag);
		}
	}
	
	public void load(Schematic s){

	}

	public void init(BlockPos pos, World level, int rotation) {
    	start = pos;
    	this.level = level;
    	this.rotation = rotation;
	}
    
    public void offset(int x, int y, int z){
    	offset = new BlockPos(x, y, z);
    }
	
	public void build(){
		if(level == null || !isBuilding)//you should init first
			return;
		
		long endPos = buildPos + buildSize;
		if(endPos > size)
			endPos = size;
		
		for(;buildPos < endPos; buildPos++){
			int x = (int) (buildPos % schema.getWidth());
			int z = (int)((buildPos - x) / schema.getWidth()) % schema.getLength();
			int y = (int)(((buildPos - x) / schema.getWidth()) - z) / schema.getLength();
			if(firstLayer)
				place(x, y, z, 1);
			else
				place(x, y, z, 2);
		}
		if(buildPos >= size){
			if(firstLayer){
				firstLayer = false;
				buildPos = 0;
			}
			else{
				isBuilding = false;
			}
		}		
	}
    
    /**
     * @param flag 0:any, 1:normal, 2:not normal
     */
    public void place(int x, int y, int z, int flag){
    	BlockState state = schema.getBlockState(x, y, z);
    	if(state == null || flag == 1 && !state.isCollisionShapeFullBlock(EmptyBlockReader.INSTANCE, BlockPos.ZERO) && state.getBlock() != Blocks.AIR || flag == 2 && (state.isCollisionShapeFullBlock(EmptyBlockReader.INSTANCE, BlockPos.ZERO) || state.getBlock() == Blocks.AIR))
    		return;
    	int rotation = this.rotation / 90;
    	BlockPos pos = start.offset(rotatePos(x, y, z, rotation));
    	state = rotationState(state, rotation);
    	level.setBlock(pos, state, 2);
    	if(state.getBlock() instanceof ITileEntityProvider){
    		TileEntity tile = level.getBlockEntity(pos);
    		if(tile != null){
	        	CompoundNBT comp = getBlockEntity(x, y, z, pos);
	        	if(comp != null)
	        		tile.load(state, comp);
    		}
    	}
//        Chunk chunk = level.getChunkFromBlockCoords(pos);
//        chunk.setBlock(pos, b.getStateFromMeta(blockDataArray[i]));
//        chunk.setChunkModified();
    }

    
    public BlockState rotationState(BlockState state, int rotation){
    	if(rotation == 0)
    		return state;
		Iterator<Property<?>> set = state.getProperties().iterator();
    	while(set.hasNext()){
			Property prop = set.next();
    		if(!(prop instanceof DirectionProperty))
    			continue;
			Direction direction = (Direction) state.getValue(prop);
			if(direction == Direction.UP || direction == Direction.DOWN)
				continue;
			for(int i = 0; i < rotation; i++){
				direction = direction.getClockWise();
			}
			return state.setValue(prop, direction);
    	}
    	
    	return state;
    }
    
    public CompoundNBT getBlockEntity(int x, int y, int z, BlockPos pos){
    	if(y >= tileEntities.length || tileEntities[y] == null)
    		return null;
    	CompoundNBT compound = tileEntities[y].get(new ChunkPos(x, z));
    	if(compound == null)
    		return null;
    	compound = (CompoundNBT) compound.copy();
    	compound.putInt("x", pos.getX());
    	compound.putInt("y", pos.getY());
    	compound.putInt("z", pos.getZ());
    	return compound;
    }
    
    public CompoundNBT getNBTSmall(){
    	CompoundNBT compound = new CompoundNBT();
    	compound.putShort("Width", schema.getWidth());
    	compound.putShort("Height", schema.getHeight());
    	compound.putShort("Length", schema.getLength());
    	compound.putString("SchematicName", schema.getName());

		ListNBT list = new ListNBT();
		
    	for(int i = 0; i < size && i < 25000; i++){
    		BlockState state = schema.getBlockState(i);
    		if(state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.STRUCTURE_VOID)
        		list.add(new CompoundNBT());
    		else 
    			list.add(NBTUtil.writeBlockState(schema.getBlockState(i)));
    	}
		compound.put("Data", list);
		return compound;
    }
    
    public BlockPos rotatePos(int x, int y, int z, int rotation){
    	if(rotation == 1)
    		return new BlockPos(schema.getLength() - z - 1, y, x);
    	else if(rotation == 2)
    		return new BlockPos(schema.getWidth() - x - 1, y, schema.getLength() - z - 1);
    	else if(rotation == 3)
    		return new BlockPos(z, y, schema.getWidth() - x - 1);
    	return new BlockPos(x, y, z);
    }

	public int getPercentage() {
		double l = (buildPos + (firstLayer?0:size));
		return (int) (l / size * 50);
	}
	
}
