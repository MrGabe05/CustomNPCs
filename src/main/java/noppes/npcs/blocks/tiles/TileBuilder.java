package noppes.npcs.blocks.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomBlocks;
import noppes.npcs.NBTTags;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobBuilder;
import noppes.npcs.schematics.SchematicWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TileBuilder extends TileEntity implements ITickableTileEntity{
	private SchematicWrapper schematic = null;
	public int rotation = 0;
	public int yOffest = 0;
	public boolean enabled = false;
	public boolean started = false;
	public boolean finished = false;
	public Availability availability = new Availability();
	private Stack<Integer> positions = new Stack<Integer>();
	private Stack<Integer> positionsSecond = new Stack<Integer>();
	
	public static BlockPos DrawPos = null;
	public static boolean Compiled = false;
	
	private int ticks = 20;

	public TileBuilder(){
		super(CustomBlocks.tile_builder);
	}

	@Override
    public void load(BlockState state, CompoundNBT compound){
		super.load(state, compound);
        if(compound.contains("SchematicName")){
        	schematic = SchematicController.Instance.load(compound.getString("SchematicName"));
        }
        
        Stack<Integer> positions = new Stack<Integer>();
        positions.addAll(NBTTags.getIntegerList(compound.getList("Positions", 10)));
        this.positions = positions;

        positions = new Stack<Integer>();
        positions.addAll(NBTTags.getIntegerList(compound.getList("PositionsSecond", 10)));
        this.positionsSecond = positions;
        
        readPartNBT(compound);
    }

    public void readPartNBT(CompoundNBT compound){
        rotation = compound.getInt("Rotation");
        yOffest = compound.getInt("YOffset");
        enabled = compound.getBoolean("Enabled");
        started = compound.getBoolean("Started");
        finished = compound.getBoolean("Finished");
        availability.load(compound.getCompound("Availability"));
    }

    @Override
    public CompoundNBT save(CompoundNBT compound){
    	super.save(compound);
    	if(schematic != null){
    		compound.putString("SchematicName", schematic.schema.getName());
    	}
    	compound.put("Positions", NBTTags.nbtIntegerCollection(new ArrayList<Integer>(positions)));
    	compound.put("PositionsSecond", NBTTags.nbtIntegerCollection(new ArrayList<Integer>(positionsSecond)));
    	writePartNBT(compound);
    	return compound;
    }

    public CompoundNBT writePartNBT(CompoundNBT compound){
    	compound.putInt("Rotation", rotation);
    	compound.putInt("YOffset", yOffest);
    	compound.putBoolean("Enabled", enabled);
    	compound.putBoolean("Started", started);
    	compound.putBoolean("Finished", finished);
    	compound.put("Availability", availability.save(new CompoundNBT()));
    	return compound;
    }

    @OnlyIn(value = Dist.CLIENT)
    public void setDrawSchematic(SchematicWrapper schematics){
    	this.schematic = schematics;
    }
    
    public void setSchematic(SchematicWrapper schematics){
    	this.schematic = schematics;
    	if(schematics == null){
    		positions.clear();
    		positionsSecond.clear();
    		return;
    	}
    	Stack<Integer> positions = new Stack<Integer>();
		for(int y = 0; y < schematics.schema.getHeight(); y++){
			for(int z = 0; z < schematics.schema.getLength() / 2; z++){
				for(int x = 0; x < schematics.schema.getWidth() / 2; x++){
					positions.add(0, xyzToIndex(x, y, z));
				}
			}
			for(int z = 0; z < schematics.schema.getLength() / 2; z++){
				for(int x = schematics.schema.getWidth() / 2; x < schematics.schema.getWidth(); x++){
					positions.add(0, xyzToIndex(x, y, z));
				}
			}
			for(int z = schematics.schema.getLength() / 2; z < schematics.schema.getLength(); z++){
				for(int x = 0; x < schematics.schema.getWidth() / 2; x++){
					positions.add(0, xyzToIndex(x, y, z));
				}
			}
			for(int z = schematics.schema.getLength() / 2; z < schematics.schema.getLength(); z++){
				for(int x = schematics.schema.getWidth() / 2; x < schematics.schema.getWidth(); x++){
					positions.add(0, xyzToIndex(x, y, z));
				}
			}
		}
		this.positions = positions;
		positionsSecond.clear();
    }
	
	public int xyzToIndex(int x, int y, int z){
		return (y * schematic.schema.getLength() + z) * schematic.schema.getWidth() + x;
	}
    
    public SchematicWrapper getSchematic(){
    	return schematic;
    }
    
    public boolean hasSchematic(){
    	return schematic != null;
    }

	@Override
	public void tick() {
		if(this.level.isClientSide || !hasSchematic() || finished)
			return;
		ticks--;
		if(ticks > 0)
			return;
		ticks = 200;
		if(positions.isEmpty() && positionsSecond.isEmpty()){
			finished = true;
			return;
		}
		
		if(!started){
			for(PlayerEntity player : getPlayerList()){
				if(availability.isAvailable(player)){
					started = true;
					break;
				}
			}
			if(!started)
				return;
		}
		
		List<EntityNPCInterface> list = level.getEntitiesOfClass(EntityNPCInterface.class, new AxisAlignedBB(getBlockPos(), getBlockPos()).inflate(32, 32, 32));
		for(EntityNPCInterface npc : list){
			if(npc.job.getType() == JobType.BUILDER){
				JobBuilder job = (JobBuilder) npc.job;
				if(job.build == null){
					job.build = this;
				}
				
			}
		}
	}
	
	private List<PlayerEntity> getPlayerList(){
		return level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1).inflate(10, 10, 10));
	}
	
	public Stack<BlockData> getBlock(){
		if(!enabled || finished || !hasSchematic())
			return null;
		boolean bo = positions.isEmpty();
		Stack<BlockData> list = new Stack<BlockData>();
		int size = schematic.schema.getWidth() * schematic.schema.getLength() / 4;
		if(size > 30)
			size = 30;
		for(int i = 0; i < size; i++){
			if(positions.isEmpty() && !bo || positionsSecond.isEmpty() && bo)
				return list;
			
			int pos = bo?positionsSecond.pop():positions.pop();
			if(pos >= schematic.size){
				continue;
			}

			int x = (int) (pos % schematic.schema.getWidth());
			int z = (int)((pos - x)/schematic.schema.getWidth()) % schematic.schema.getLength();
			int y = (int)(((pos - x)/schematic.schema.getWidth()) - z) / schematic.schema.getLength();
			BlockState state = schematic.schema.getBlockState(x, y, z);
	    	if(!state.isCollisionShapeFullBlock(EmptyBlockReader.INSTANCE, BlockPos.ZERO) && !bo && state.getBlock() != Blocks.AIR){
	    		positionsSecond.add(0, pos);
	    		continue;
	    	}
			
			BlockPos blockPos = getBlockPos().offset(1, yOffest, 1).offset(schematic.rotatePos(x, y, z, rotation));
			
			BlockState original = level.getBlockState(blockPos);
			if(Block.getId(state) == Block.getId(original)) //If block is already set ignore
				continue;
			
			state = schematic.rotationState(state, rotation);
			CompoundNBT tile = null;
	    	if(state.getBlock() instanceof ITileEntityProvider){
	    		tile = schematic.getBlockEntity(x, y, z, blockPos);
	    	}
	    	list.add(0, new BlockData(blockPos, state, tile));
		}
		return list;
	}
	
	public static void SetDrawPos(BlockPos pos){
		DrawPos = pos;
		Compiled = false;
	}
}
