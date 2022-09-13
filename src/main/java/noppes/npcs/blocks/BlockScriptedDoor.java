package noppes.npcs.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.EventHooks;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class BlockScriptedDoor extends BlockNpcDoorInterface{
	//public static final IntegerProperty MODEL = IntegerProperty.create("model", 0, 6);
	
	public BlockScriptedDoor() {
		super(Block.Properties.copy(Blocks.IRON_DOOR).strength(5.0F, 10));
	}


    @Override
    public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(CustomBlocks.scripted_door_item);
    }

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileScriptedDoor();		
	}

	@Override
    public BlockRenderType getRenderShape(BlockState state){
        return BlockRenderType.INVISIBLE;
    }
	
	@Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
		if(level.isClientSide)
            return ActionResultType.SUCCESS;
		BlockPos blockpos1 = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        BlockState iblockstate1 = pos.equals(blockpos1) ? state : level.getBlockState(blockpos1);

        if (iblockstate1.getBlock() != this){
            return ActionResultType.FAIL;
        }
    	ItemStack currentItem = player.inventory.getSelected();
    	
		if (currentItem != null	&& (currentItem.getItem() == CustomItems.wand || currentItem.getItem() == CustomItems.scripter || currentItem.getItem() == CustomBlocks.scripted_door_item)) {
            PlayerData data = PlayerData.get(player);
            data.scriptBlockPos = blockpos1;
		    SPacketGuiOpen.sendOpenGui(player, EnumGuiType.ScriptDoor, null, blockpos1);
            return ActionResultType.SUCCESS;
		}

    	TileScriptedDoor tile = (TileScriptedDoor) level.getBlockEntity(blockpos1);
        Vector3d vec = ray.getLocation();
        float x = (float)(vec.x - (double)pos.getX());
        float y = (float)(vec.y - (double)pos.getY());
        float z = (float)(vec.z - (double)pos.getZ());
    	if(EventHooks.onScriptBlockInteract(tile, player, ray.getDirection().get3DDataValue(), x, y, z))
            return ActionResultType.FAIL;
    	
    	setOpen(level, iblockstate1, blockpos1, iblockstate1.getValue(DoorBlock.OPEN).equals(false));
        return ActionResultType.SUCCESS;
	}

	@Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos pos2, boolean isMoving) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos blockpos1 = pos.below();
            BlockState iblockstate1 = worldIn.getBlockState(blockpos1);

            if (iblockstate1.getBlock() != this)
            {
                worldIn.removeBlock(pos, false);
            }
            else if (neighborBlock != this)
            {
                this.neighborChanged(iblockstate1, worldIn, blockpos1, neighborBlock, blockpos1, isMoving);
            }
        }
        else
        {
            BlockPos blockpos2 = pos.above();
            BlockState iblockstate2 = worldIn.getBlockState(blockpos2);

            if (iblockstate2.getBlock() != this)
            {
                worldIn.removeBlock(pos, false);
            }
            else
            {
            	TileScriptedDoor tile = (TileScriptedDoor) worldIn.getBlockEntity(pos);
            	if(!worldIn.isClientSide)
            		EventHooks.onScriptBlockNeighborChanged(tile, pos2);
            	
                boolean flag = worldIn.hasNeighborSignal(pos) || worldIn.hasNeighborSignal(blockpos2);

                if ((flag || neighborBlock.defaultBlockState().isSignalSource()) && neighborBlock != this && flag != iblockstate2.getValue(POWERED).booleanValue())
                {
                    worldIn.setBlock(blockpos2, iblockstate2.setValue(POWERED, Boolean.valueOf(flag)), 2);

                    if (flag != state.getValue(OPEN).booleanValue()){
                    	setOpen(worldIn, state, pos, flag);
                    }
                }
                
            	int power = 0;
                for (Direction enumfacing : Direction.values()){
                	int p = worldIn.getSignal(pos.relative(enumfacing), enumfacing);
                	if(p > power)
                		power = p;
                }
            	tile.newPower = power;
            }
        }
    }
	
	@Override
	public void setOpen(World worldIn, BlockState state, BlockPos pos, boolean open){
		TileScriptedDoor tile = (TileScriptedDoor) worldIn.getBlockEntity(pos);
		
		if(EventHooks.onScriptBlockDoorToggle(tile))
			return;
    	super.setOpen(worldIn, state, pos, open);
        //worldIn.setBlockEntity(pos, tile);
	}
	
	@Override    
    public void attack(BlockState state, World level, BlockPos pos, PlayerEntity playerIn) {
		if(level.isClientSide)
			return;
		BlockPos blockpos1 = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        BlockState iblockstate1 = pos.equals(blockpos1) ? state : level.getBlockState(blockpos1);
        if (iblockstate1.getBlock() != this) {
            return;
        }
        TileScriptedDoor tile = (TileScriptedDoor) level.getBlockEntity(blockpos1);
        EventHooks.onScriptBlockClicked(tile, playerIn);
		
    }
	
	@Override 
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving){
        if(state.getBlock() == newState.getBlock()){
            return;
        }

		BlockPos blockpos1 = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        BlockState iblockstate1 = pos.equals(blockpos1) ? state : level.getBlockState(blockpos1);


        if(!level.isClientSide && iblockstate1.getBlock() == this){
        	TileScriptedDoor tile = (TileScriptedDoor) level.getBlockEntity(pos);
        	EventHooks.onScriptBlockBreak(tile);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World level, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid){
		if(!level.isClientSide){
	    	TileScriptedDoor tile = (TileScriptedDoor) level.getBlockEntity(pos);
			if(EventHooks.onScriptBlockHarvest(tile, player))
				return false;			
		}
    	return super.removedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override    
    public void entityInside(BlockState state, World level, BlockPos pos, Entity entityIn) {
		if(level.isClientSide)
			return;
		TileScriptedDoor tile = (TileScriptedDoor) level.getBlockEntity(pos);
    	EventHooks.onScriptBlockCollide(tile, entityIn);
    }

	@Override
	public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player){
		BlockPos blockpos1 = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
		BlockState iblockstate1 = pos.equals(blockpos1) ? state : level.getBlockState(blockpos1);
        if (player.abilities.instabuild && iblockstate1.getValue(HALF) == DoubleBlockHalf.LOWER && iblockstate1.getBlock() == this){
            level.removeBlock(blockpos1, false);
        }
    }

    @Override
    public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos) {
        float f = ((TileScriptedDoor) level.getBlockEntity(pos)).blockHardness;
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = net.minecraftforge.common.ForgeHooks.canHarvestBlock(state, player, level, pos) ? 30 : 100;
            return player.getDigSpeed(state, pos) / f / (float)i;
        }
    }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader level, BlockPos pos, Explosion explosion){
        return ((TileScriptedDoor) level.getBlockEntity(pos)).blockResistance;
    }
}
