package noppes.npcs.blocks.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.BlockNpcRedstone;
import noppes.npcs.controllers.data.Availability;

import java.util.List;

public class TileRedstoneBlock extends TileNpcEntity implements ITickableTileEntity {
	public int onRange = 12;
	public int offRange = 20;

	public int onRangeX = 12;
	public int onRangeY = 12;
	public int onRangeZ = 12;

	public int offRangeX = 20;
	public int offRangeY = 20;
	public int offRangeZ = 20;
	
	public boolean isDetailed = false;

	public Availability availability = new Availability();
	
	public boolean isActivated = false;
	
	private int ticks = 10;

	public TileRedstoneBlock(){
		super(CustomBlocks.tile_redstoneblock);
	}

	@Override
	public void tick(){
		if(this.level.isClientSide)
			return;
		ticks--;
		if(ticks > 0)
			return;
		ticks = onRange > 10? 20 : 10;
		Block block = this.getBlockState().getBlock();
		if(block == null || block instanceof BlockNpcRedstone == false){
			return;
		}

		if(CustomNpcs.FreezeNPCs){
			if(isActivated)
				setActive(block,false);
			return;
		}
		if(!isActivated){
			int x = isDetailed?onRangeX:onRange;
			int y = isDetailed?onRangeY:onRange;
			int z = isDetailed?onRangeZ:onRange;
			List<PlayerEntity> list = getPlayerList(x,y,z);
			if(list.isEmpty())
				return;
			for(PlayerEntity player : list){
				if(availability.isAvailable(player)){
					setActive(block,true);
					return;
				}
			}
		}
		else{
			int x = isDetailed?offRangeX:offRange;
			int y = isDetailed?offRangeY:offRange;
			int z = isDetailed?offRangeZ:offRange;
			List<PlayerEntity> list = getPlayerList(x,y,z);
			for(PlayerEntity player : list){
				if(availability.isAvailable(player))
					return;
			}
			setActive(block,false);
		}
	
	}

	private void setActive(Block block, boolean bo){
		isActivated = bo;
		BlockState state = block.defaultBlockState().setValue(BlockNpcRedstone.ACTIVE, isActivated);
		level.setBlock(worldPosition, state, 2);
		setChanged();
		level.sendBlockUpdated(worldPosition, state, state, 3);
		block.onPlace(state, level, worldPosition, state, false);
	}
	
	private List<PlayerEntity> getPlayerList(int x, int y, int z){
		return level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1).inflate(x, y, z));
	}
	
	@Override
    public void load(BlockState state, CompoundNBT compound){
		super.load(state, compound);
    	onRange = compound.getInt("BlockOnRange");
    	offRange = compound.getInt("BlockOffRange");
    	
    	isDetailed = compound.getBoolean("BlockIsDetailed");
    	if(compound.contains("BlockOnRangeX")){
    		isDetailed = true;
    		onRangeX = compound.getInt("BlockOnRangeX");
    		onRangeY = compound.getInt("BlockOnRangeY");
    		onRangeZ = compound.getInt("BlockOnRangeZ");

    		offRangeX = compound.getInt("BlockOffRangeX");
    		offRangeY = compound.getInt("BlockOffRangeY");
    		offRangeZ = compound.getInt("BlockOffRangeZ");
    	}

    	if(compound.contains("BlockActivated"))
    		isActivated = compound.getBoolean("BlockActivated");
    	
    	availability.load(compound);
    }

	@Override
    public CompoundNBT save(CompoundNBT compound){
    	compound.putInt("BlockOnRange", onRange);
    	compound.putInt("BlockOffRange", offRange);
    	compound.putBoolean("BlockActivated", isActivated);
    	compound.putBoolean("BlockIsDetailed", isDetailed);

    	if(isDetailed){
	    	compound.putInt("BlockOnRangeX", onRangeX);
	    	compound.putInt("BlockOnRangeY", onRangeY);
	    	compound.putInt("BlockOnRangeZ", onRangeZ);

	    	compound.putInt("BlockOffRangeX", offRangeX);
	    	compound.putInt("BlockOffRangeY", offRangeY);
	    	compound.putInt("BlockOffRangeZ", offRangeZ);
    	}
    	
    	
    	availability.save(compound);
    	return super.save(compound);
    }
}
