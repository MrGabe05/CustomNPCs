package noppes.npcs.schematics;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public interface ISchematic {

	public short getWidth();

	public short getHeight();

	public short getLength();

	public int getBlockEntitySize();
	
	public CompoundNBT getBlockEntity(int i);

	public String getName();

	public BlockState getBlockState(int x, int y, int z);

	public BlockState getBlockState(int i);
	
	public CompoundNBT getNBT();
}
