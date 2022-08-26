package noppes.npcs.blocks.tiles;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import noppes.npcs.CustomBlocks;

public class TileBlockAnvil extends TileNpcEntity {

	public TileBlockAnvil(){
		super(CustomBlocks.tile_anvil);
	}
	
    public boolean canUpdate(){
        return false;
    }

//	@Override
//	public void updateEntity(){
//	
//	}
	
//	@Override
//    public void load(CompoundNBT par1CompoundNBT)
//    {
//    	super.load(par1CompoundNBT);
//    }
//
//	@Override
//    public void save(CompoundNBT par1CompoundNBT)
//    {
//    	super.save(par1CompoundNBT);
//    }
}
