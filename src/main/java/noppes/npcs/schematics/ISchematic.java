package noppes.npcs.schematics;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public interface ISchematic {

	short getWidth();

	short getHeight();

	short getLength();

	int getBlockEntitySize();
	
	CompoundNBT getBlockEntity(int i);

	String getName();

	BlockState getBlockState(int x, int y, int z);

	BlockState getBlockState(int i);
	
	CompoundNBT getNBT();
}
