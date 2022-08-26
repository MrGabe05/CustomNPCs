package noppes.npcs.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class BlockCopy extends BlockInterface{
	public BlockCopy() {
        super(Block.Properties.copy(Blocks.BARRIER).sound(SoundType.STONE));
	}

    @Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
    	if(level.isClientSide)
    		return ActionResultType.PASS;
    	
		ItemStack currentItem = player.inventory.getSelected();
		if (currentItem != null	&& currentItem.getItem() == CustomItems.wand) {
			SPacketGuiOpen.sendOpenGui(player, EnumGuiType.CopyBlock, null, pos);
		}
		return ActionResultType.SUCCESS;
    }

    @Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
    	if(!context.getLevel().isClientSide){
			SPacketGuiOpen.sendOpenGui(context.getPlayer(), EnumGuiType.CopyBlock, null, context.getClickedPos());
    	}
    	return defaultBlockState();
    }
    
//    @Override
//    public boolean isFullCube(BlockState state){
//        return false;
//    }

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileCopy();
	}

}
