package noppes.npcs.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BlockNpcDoorInterface extends DoorBlock implements ITileEntityProvider{
	
	public BlockNpcDoorInterface(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
		level.removeBlockEntity(pos);
    }

	@Override
	public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
		return Collections.emptyList();
	}

	@Override
	public boolean hasTileEntity(BlockState state){
		return true;
	}

	@Override
	public void playerDestroy(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
		p_180657_2_.awardStat(Stats.BLOCK_MINED.get(this));
		p_180657_2_.causeFoodExhaustion(0.005F);
		dropResources(p_180657_4_, p_180657_1_, p_180657_3_, p_180657_5_, p_180657_2_, p_180657_6_);
	}
//	@Override
//	public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//		BlockState iblockstate1;
//
//		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
//
//			iblockstate1 = worldIn.getBlockState(pos.above());
//
//			if (iblockstate1.getBlock() == this) {
//				state = state.setValue(HINGE, iblockstate1.getValue(HINGE)).with(POWERED, iblockstate1.getValue(POWERED));
//			}
//		}
//		else {
//			iblockstate1 = worldIn.getBlockState(pos.below());
//
//			if (iblockstate1.getBlock() == this) {
//				state = state.setValue(FACING,iblockstate1.getValue(FACING)).with(OPEN, iblockstate1.getValue(OPEN));
//			}
//		}
//
//		return state;
//	}
}
