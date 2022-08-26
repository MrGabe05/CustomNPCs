package noppes.npcs.blocks.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomBlocks;

public class TileDoor extends TileNpcEntity implements ITickableTileEntity {
	public int tickCount=0;
	public Block blockModel = CustomBlocks.scripted_door;
	public boolean needsClientUpdate = false;
	
	public TileEntity renderTile;
	public boolean renderTileErrored = true;
	public ITickableTileEntity renderTileUpdate = null;

	public TileDoor(TileEntityType<?> p_i48289_1_) {
		super(p_i48289_1_);
	}


	@Override
    public void load(BlockState state, CompoundNBT compound){
		super.load(state, compound);
		setDoorNBT(compound);
    }
	
	public void setDoorNBT(CompoundNBT compound){
		blockModel = (Block) ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compound.getString("ScriptDoorBlockModel")));
		if(blockModel == null || !(blockModel instanceof DoorBlock))
			blockModel = CustomBlocks.scripted_door;
		renderTileUpdate = null;
		renderTile = null;
		renderTileErrored = false;
	}
	
	@Override
    public CompoundNBT save(CompoundNBT compound){
		getDoorNBT(compound);
    	return super.save(compound);
    }
	
	
	public CompoundNBT getDoorNBT(CompoundNBT compound){
		compound.putString("ScriptDoorBlockModel", ForgeRegistries.BLOCKS.getKey(blockModel) + "");
		
		return compound;
	}
	
	public void setItemModel(Block block) {
		if(block == null || !(block instanceof DoorBlock))
			block = CustomBlocks.scripted_door;
		if(blockModel == block)
			return;
		blockModel = block;
		needsClientUpdate = true;
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
		if(tickCount >= 10){
			tickCount = 0;
			if(needsClientUpdate){
	    		setChanged();
	    		BlockState state = level.getBlockState(worldPosition);
	    		level.setBlockAndUpdate(this.worldPosition, state);
				needsClientUpdate = false;
			}
		}
	}
	

	@Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
		handleUpdateTag(level.getBlockState(pkt.getPos()), pkt.getTag());
    }
    
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT compound){
    	setDoorNBT(compound);
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
    	getDoorNBT(compound);
    	return compound;
    }
}
