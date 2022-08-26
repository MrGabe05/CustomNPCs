package noppes.npcs.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.blocks.tiles.TileRedstoneBlock;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

import javax.annotation.Nullable;

public class BlockNpcRedstone extends BlockInterface{
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public BlockNpcRedstone(){
        super(Block.Properties.copy(Blocks.STONE).lightLevel((state) -> 12).strength(50.0F, 2000));
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
    	if(level.isClientSide)
    		return ActionResultType.SUCCESS;
		ItemStack currentItem = player.inventory.getSelected();
		if (currentItem != null	&& currentItem.getItem() == CustomItems.wand && CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.EDIT_BLOCKS)) {
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.RedstoneBlock, null, pos);
        	return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
    }
    
    @Override
    public void onPlace(BlockState state, World par1World, BlockPos pos, BlockState stateNew, boolean bo){
        par1World.updateNeighborsAt(pos, this);
        par1World.updateNeighborsAt(pos.below(), this);
        par1World.updateNeighborsAt(pos.above(), this);
        par1World.updateNeighborsAt(pos.west(), this);
        par1World.updateNeighborsAt(pos.east(), this);
        par1World.updateNeighborsAt(pos.south(), this);
        par1World.updateNeighborsAt(pos.north(), this);
    }

    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
    	if(!level.isClientSide && entity instanceof PlayerEntity){
            SPacketGuiOpen.sendOpenGui((PlayerEntity)entity, EnumGuiType.RedstoneBlock, null, pos);
    	}
    }

    @Override
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
    	onPlace(state, level, pos, state, isMoving);
    }
    
    @Override
    public int getSignal(BlockState state, IBlockReader worldIn, BlockPos pos, Direction side){
        return isActivated(state);
    }
    
    @Override
    public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side){
        return isActivated(state);
    }
    
    @Override
    public boolean isSignalSource(BlockState state){
        return true;
    }


    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
    
	public int isActivated(BlockState state){
		return (Boolean)state.getValue(ACTIVE)?15 : 0;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileRedstoneBlock();
	}

    @Override   
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}
}
