package noppes.npcs.blocks.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;

public class TileColorable extends TileNpcEntity {
	
	public int color = 14;
	public int rotation;

    public TileColorable(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound){
        super.load(state, compound);
        color = compound.getInt("BannerColor");
        rotation = compound.getInt("BannerRotation");
    }

    @Override
    public CompoundNBT save(CompoundNBT compound){
    	compound.putInt("BannerColor", color);
    	compound.putInt("BannerRotation", rotation);
    	return super.save(compound);
    }
	
    public boolean canUpdate(){
        return false;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
    	CompoundNBT compound = pkt.getTag();
    	load(this.getBlockState(), compound);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket(){
    	return new SUpdateTileEntityPacket(worldPosition, 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag(){
    	CompoundNBT compound = new CompoundNBT();
    	save(compound);
    	compound.remove("Items");
    	compound.remove("ExtraData");
    	return compound;
    }
    
	@Override
    public AxisAlignedBB getRenderBoundingBox(){
		return new AxisAlignedBB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1);
    }
	
	public int powerProvided(){
		return 0;
	}
}
