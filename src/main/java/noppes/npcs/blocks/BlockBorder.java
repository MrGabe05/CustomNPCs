package noppes.npcs.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileBorder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

import javax.annotation.Nullable;

public class BlockBorder extends BlockInterface{
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);
	public BlockBorder() {
		super(Block.Properties.copy(Blocks.BARRIER).sound(SoundType.STONE));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(ROTATION);
	}

    @Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
		ItemStack currentItem = player.inventory.getSelected();
		if (!level.isClientSide && currentItem.getItem() == CustomItems.wand) {
			SPacketGuiOpen.sendOpenGui(player, EnumGuiType.Border, null, pos);
        	return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
    }
    
    @Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		if(context.getPlayer() != null){
			int l = MathHelper.floor((double)(context.getPlayer().yRot * 4.0F / 360.0F) + 0.5D) & 3;
			l %= 4;
			return this.defaultBlockState().setValue(ROTATION, l);
		}
		return super.getStateForPlacement(context);
    }

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
		TileBorder tile = (TileBorder) level.getBlockEntity(pos);

		TileBorder adjacent = getTile(level, pos.west());
		if(adjacent == null)
			adjacent = getTile(level, pos.south());
		if(adjacent == null)
			adjacent = getTile(level, pos.north());
		if(adjacent == null)
			adjacent = getTile(level, pos.east());

		if(adjacent != null){
			CompoundNBT compound = new CompoundNBT();
			adjacent.writeExtraNBT(compound);
			tile.readExtraNBT(compound);
		}

		tile.rotation = state.getValue(ROTATION);
		if(!level.isClientSide && entity instanceof PlayerEntity){
			SPacketGuiOpen.sendOpenGui((PlayerEntity) entity, EnumGuiType.Border, null, pos);
		}
	}
    
    private TileBorder getTile(World level, BlockPos pos){
    	TileEntity tile = level.getBlockEntity(pos);
    	if(tile != null && tile instanceof TileBorder)
    		return (TileBorder) tile;
    	return null;
    }

    @Override   
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

//    @Override
//    public boolean isFullCube(BlockState state){
//        return false;
//    }

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileBorder();
	}


}
