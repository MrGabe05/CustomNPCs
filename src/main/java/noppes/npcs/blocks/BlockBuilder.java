package noppes.npcs.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class BlockBuilder extends BlockInterface{
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);
	public BlockBuilder() {
        super(Block.Properties.copy(Blocks.BARRIER).sound(SoundType.STONE));
	}

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

	@Override
    public BlockRenderType getRenderShape(BlockState state){
        return BlockRenderType.MODEL;
    }
	
    @Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
    	if(level.isClientSide)
    		return ActionResultType.SUCCESS;
    	
		ItemStack currentItem = player.inventory.getSelected();
		if (currentItem.getItem() == CustomItems.wand || currentItem.getItem() == CustomBlocks.builder_item) {
			SPacketGuiOpen.sendOpenGui(player, EnumGuiType.BuilderBlock, null, pos);
		}
		return ActionResultType.SUCCESS;
    }

    @Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
        int var6 = MathHelper.floor((double)(context.getPlayer().yRot / 90.0F) + 0.5D) & 3;

    	if(!context.getLevel().isClientSide){
			SPacketGuiOpen.sendOpenGui(context.getPlayer(), EnumGuiType.BuilderBlock, null, context.getClickedPos());
    	}
    	return defaultBlockState().setValue(ROTATION, var6);
    }

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileBuilder();
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
    	if(TileBuilder.DrawPos != null && TileBuilder.DrawPos.equals(pos)){
    		TileBuilder.SetDrawPos(null);
    	}
		super.onRemove(state, level, pos, newState, isMoving);
    }
}
