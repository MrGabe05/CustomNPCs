package noppes.npcs.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import noppes.npcs.CustomItems;
import noppes.npcs.EventHooks;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.server.SPacketGuiOpen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockScripted extends BlockInterface {
    public static final VoxelShape AABB = VoxelShapes.create(new AxisAlignedBB(0.001f, 0.001f, 0.001f, 0.998f, 0.998f, 0.998f));

	public BlockScripted() {
		super(Block.Properties.copy(Blocks.STONE).sound(SoundType.STONE).strength(5.0F, 10));
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileScripted();
	}

    @Override 
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context){
    	return AABB;
    }

    @Override 
    public VoxelShape getCollisionShape(BlockState blockState, IBlockReader level, BlockPos pos, ISelectionContext context){
    	TileScripted tile = (TileScripted) level.getBlockEntity(pos);
    	if(tile != null && tile.isPassible)
    		return VoxelShapes.empty();
        return AABB;
    }
    
    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
    	if(level.isClientSide)
    		return ActionResultType.SUCCESS;
		ItemStack currentItem = player.inventory.getSelected();
		if (currentItem != null	&& (currentItem.getItem() == CustomItems.wand || currentItem.getItem() == CustomItems.scripter)) {
            PlayerData data = PlayerData.get(player);
            data.scriptBlockPos = pos;
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.ScriptBlock, null, pos);
        	return ActionResultType.SUCCESS;
		}
		Vector3d vec = ray.getLocation();
        float x = (float)(vec.x - (double)pos.getX());
        float y = (float)(vec.y - (double)pos.getY());
        float z = (float)(vec.z - (double)pos.getZ());
    	TileScripted tile = (TileScripted) level.getBlockEntity(pos);
    	return EventHooks.onScriptBlockInteract(tile, player, ray.getDirection().get3DDataValue(), x, y, z)?ActionResultType.FAIL : ActionResultType.SUCCESS;
    }

    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
        if(!level.isClientSide && entity instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) entity;
            PlayerData data = PlayerData.get(player);
            data.scriptBlockPos = pos;
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.ScriptBlock, null, pos);
    	}
    }

    @Override    
    public void entityInside(BlockState state, World level, BlockPos pos, Entity entityIn) {
		if(level.isClientSide)
			return;
    	TileScripted tile = (TileScripted) level.getBlockEntity(pos);
    	EventHooks.onScriptBlockCollide(tile, entityIn);
    }
    
    @Override    
    public void handleRain(World level, BlockPos pos) {
		if(level.isClientSide)
			return;
    	TileScripted tile = (TileScripted) level.getBlockEntity(pos);
    	EventHooks.onScriptBlockRainFill(tile);
    }
    
    @Override       
    public void fallOn(World level, BlockPos pos, Entity entity, float fallDistance){
		if(level.isClientSide)
			return;
    	TileScripted tile = (TileScripted) level.getBlockEntity(pos);
    	fallDistance = EventHooks.onScriptBlockFallenUpon(tile, entity, fallDistance);
    	super.fallOn(level, pos, entity, fallDistance);
    }

//    @Override
//    public boolean isFullCube(BlockState state){
//        return false;
//    }
    
    @Override    
    public void attack(BlockState state, World level, BlockPos pos, PlayerEntity player) {
		if(level.isClientSide)
			return;
    	TileScripted tile = (TileScripted) level.getBlockEntity(pos);
    	EventHooks.onScriptBlockClicked(tile, player);
    }
    
    @Override 
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving){
		if(!level.isClientSide){
	    	TileScripted tile = (TileScripted) level.getBlockEntity(pos);
	    	EventHooks.onScriptBlockBreak(tile);
		}
    	super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World level, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid){
		if(!level.isClientSide){
	    	TileScripted tile = (TileScripted) level.getBlockEntity(pos);
			if(EventHooks.onScriptBlockHarvest(tile, player))
				return false;			
		}
    	return super.removedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
        return Collections.emptyList();
    }
    
    @Override
    public void onBlockExploded(BlockState state, World level, BlockPos pos, Explosion explosion){
		if(!level.isClientSide){
	    	TileScripted tile = (TileScripted) level.getBlockEntity(pos);
	    	if(EventHooks.onScriptBlockExploded(tile))
	    		return;
		}
    	super.onBlockExploded(state, level, pos, explosion);
    }
    
    @Override
    public void neighborChanged(BlockState state, World level, BlockPos pos, Block neighborBlock, BlockPos pos2, boolean isMoving) {
    	if(level.isClientSide)
    		return;
    	TileScripted tile = (TileScripted) level.getBlockEntity(pos);
    	EventHooks.onScriptBlockNeighborChanged(tile, pos2);
    	
    	int power = 0;
        for (Direction enumfacing : Direction.values()){
        	int p = level.getSignal(pos.relative(enumfacing), enumfacing);
        	if(p > power)
        		power = p;
        }
        if (tile.prevPower != power && tile.powering <= 0){
        	tile.newPower = power;
        }
    }

    @Override
    public boolean isSignalSource(BlockState state){
        return true;
    }

    
    @Override
    public int getSignal(BlockState state, IBlockReader worldIn, BlockPos pos, Direction side){
        return this.getDirectSignal(state, worldIn, pos, side);
    }
    
    @Override
    public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side){
        return ((TileScripted) level.getBlockEntity(pos)).activePowering;
    }

    @Override
    public boolean isLadder(BlockState state, IWorldReader level, BlockPos pos, LivingEntity entity){
    	return ((TileScripted) level.getBlockEntity(pos)).isLadder;
    }
    
    @Override
    public boolean canCreatureSpawn(BlockState state, IBlockReader level, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType){
    	return true;
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader level, BlockPos pos){
    	TileScripted tile = (TileScripted) level.getBlockEntity(pos);
    	if(tile == null)
    		return 0;
    	return tile.lightValue;
    }

    @Override
    public boolean isPathfindable(BlockState state, IBlockReader level, BlockPos pos, PathType type) {
        return ((TileScripted) level.getBlockEntity(pos)).isPassible;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader level, BlockPos pos, Entity entity){
    	return super.canEntityDestroy(state, level, pos, entity);
    }

    @Override
    public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos) {
        float f = ((TileScripted) level.getBlockEntity(pos)).blockHardness;
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = net.minecraftforge.common.ForgeHooks.canHarvestBlock(state, player, level, pos) ? 30 : 100;
            return player.getDigSpeed(state, pos) / f / (float)i;
        }
    }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader level, BlockPos pos, Explosion explosion){
        return ((TileScripted) level.getBlockEntity(pos)).blockResistance;
    }
}
