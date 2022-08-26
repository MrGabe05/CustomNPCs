package noppes.npcs.blocks.tiles;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileNpcEntity extends TileEntity{
	public Map<String,Object> tempData = new HashMap<String,Object>();

    public TileNpcEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound){
        super.load(state, compound);
        CompoundNBT extraData = compound.getCompound("ExtraData");
        if(!extraData.isEmpty()){
        	getTileData().put("CustomNPCsData", extraData);
        }
    }

	@Override
    public CompoundNBT save(CompoundNBT compound){
    	return super.save(compound);
    }
}