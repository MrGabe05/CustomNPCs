package noppes.npcs.api.wrapper;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import noppes.npcs.api.block.IBlockFluidContainer;

public class BlockFluidContainerWrapper extends BlockWrapper implements IBlockFluidContainer{
	private final IFluidBlock block;

	public BlockFluidContainerWrapper(World level, Block block, BlockPos pos) {
		super(level, block, pos);
		this.block = (IFluidBlock) block;
	}
	
	@Override
	public float getFluidPercentage(){
		return block.getFilledPercentage(level.getMCWorld(), pos);
	}

	@Override
	public float getFuildDensity(){
		return block.getFluid().getAttributes().getDensity(level.getMCWorld(), pos);
	}

	@Override
	public float getFuildTemperature(){
		return block.getFluid().getAttributes().getTemperature(level.getMCWorld(), pos);
	}

	@Override
	public String getFluidName(){
		return block.getFluid().getAttributes().getTranslationKey();
	}
}
