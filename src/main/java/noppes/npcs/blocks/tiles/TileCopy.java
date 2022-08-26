package noppes.npcs.blocks.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import noppes.npcs.CustomBlocks;

public class TileCopy extends TileEntity {

	public short length = 10;
	public short width = 10;
	public short height = 10;
	
	public String name = "";

	public TileCopy(){
		super(CustomBlocks.tile_copy);
	}

	@Override
    public void load(BlockState state, CompoundNBT compound){
		super.load(state, compound);

    	length = compound.getShort("Length");
    	width = compound.getShort("Width");
    	height = compound.getShort("Height");
    	
    	name = compound.getString("Name");
    }
    

    @Override
    public CompoundNBT save(CompoundNBT compound){
    	compound.putShort("Length", length);
    	compound.putShort("Width", width);
    	compound.putShort("Height", height);
    	
    	compound.putString("Name", name);
    	return super.save(compound);
    }
    
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT compound){
    	length = compound.getShort("Length");
    	width = compound.getShort("Width");
    	height = compound.getShort("Height");
    }    

	@Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
		handleUpdateTag(level.getBlockState(pkt.getPos()), pkt.getTag());
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
    	compound.putShort("Length", length);
    	compound.putShort("Width", width);
    	compound.putShort("Height", height);
    	return compound;
    }

	@Override
    public AxisAlignedBB getRenderBoundingBox(){
		return new AxisAlignedBB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + width + 1, worldPosition.getY() + height + 1, worldPosition.getZ() + length + 1);
    }
}
