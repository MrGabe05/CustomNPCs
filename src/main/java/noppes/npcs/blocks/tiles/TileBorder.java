package noppes.npcs.blocks.tiles;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.CustomBlocks;
import noppes.npcs.blocks.BlockBorder;
import noppes.npcs.controllers.data.Availability;

public class TileBorder extends TileNpcEntity implements Predicate, ITickableTileEntity {
	public Availability availability = new Availability();
	public AxisAlignedBB boundingbox;
	public int rotation = 0;
	public int height = 10;
	public String message = "availability.areaNotAvailble";

	public TileBorder(){
		super(CustomBlocks.tile_border);
	}

    @Override
    public void load(BlockState state, CompoundNBT compound){
		super.load(state, compound);
        readExtraNBT(compound);
        if(getLevel() != null)
			getLevel().setBlockAndUpdate(this.getBlockPos(), CustomBlocks.border.defaultBlockState().setValue(BlockBorder.ROTATION, rotation));
    }
    
    public void readExtraNBT(CompoundNBT compound){
        availability.load(compound.getCompound("BorderAvailability"));
        rotation = compound.getInt("BorderRotation");
        height = compound.getInt("BorderHeight");
        message = compound.getString("BorderMessage");
    }

    @Override
    public CompoundNBT save(CompoundNBT compound){
    	writeExtraNBT(compound);
    	return super.save(compound);
    }
    
    public void writeExtraNBT(CompoundNBT compound){
    	compound.put("BorderAvailability", availability.save(new CompoundNBT()));
    	compound.putInt("BorderRotation", rotation);
    	compound.putInt("BorderHeight", height);
    	compound.putString("BorderMessage", message);
    }
    
    @Override
    public void tick() {
    	if(level.isClientSide)
    		return;
    	AxisAlignedBB box = new AxisAlignedBB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + height + 1, worldPosition.getZ() + 1);
    	List<Entity> list = level.getEntitiesOfClass(Entity.class, box, this);
    	for(Entity entity : list){
    		if(entity instanceof EnderPearlEntity){
    			EnderPearlEntity pearl = (EnderPearlEntity) entity;
    			if(pearl.getOwner() instanceof PlayerEntity && !availability.isAvailable((PlayerEntity)pearl.getOwner()))
    				entity.removed = true;
    			continue;
    		}
    		PlayerEntity player = (PlayerEntity) entity;
    		if(availability.isAvailable(player))
    			continue;
    		BlockPos pos2 = new BlockPos(worldPosition);
    		if(rotation == 2){
    			pos2 = pos2.south();
    		}
    		else if(rotation == 0){
    			pos2 = pos2.north();
    		}
    		else if(rotation == 1){
    			pos2 = pos2.east();
    		}
    		else if(rotation == 3){
    			pos2 = pos2.west();
    		}
    		while(!level.isEmptyBlock(pos2)){
    			pos2 = pos2.above();
    		}
    		player.teleportTo(pos2.getX() + 0.5, pos2.getY(), pos2.getZ() + 0.5);
    		if(!message.isEmpty())
    			player.displayClientMessage(new TranslationTextComponent(message), true);
    	}
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
    	handleUpdateTag(level.getBlockState(pkt.getPos()), pkt.getTag());
    }
    
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT compound){
    	rotation = compound.getInt("Rotation");
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
    	compound.putInt("Rotation", rotation);
    	return compound;
    }

	public boolean isEntityApplicable(Entity var1) {
		return var1 instanceof ServerPlayerEntity || var1 instanceof EnderPearlEntity;
	}

	@Override
	public boolean apply(Object ob) {
		return isEntityApplicable((Entity) ob);
	}
}
