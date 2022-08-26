package noppes.npcs.blocks;

import net.minecraft.block.*;
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
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.blocks.tiles.TileWaypoint;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

import javax.annotation.Nullable;

public class BlockWaypoint extends BlockInterface{

	public BlockWaypoint() {
        super(Block.Properties.copy(Blocks.BARRIER).sound(SoundType.METAL));
	}

    @Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
    	if(level.isClientSide)
    		return ActionResultType.PASS;
		ItemStack currentItem = player.inventory.getSelected();
		if (currentItem != null	&& currentItem.getItem() == CustomItems.wand && CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.EDIT_BLOCKS)) {
			SPacketGuiOpen.sendOpenGui(player, EnumGuiType.Waypoint, null, pos);
        	return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
    }
    
    @Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
		if(!level.isClientSide && entity instanceof PlayerEntity){
			SPacketGuiOpen.sendOpenGui((PlayerEntity) entity, EnumGuiType.Waypoint, null, pos);
    	}
    }
    
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileWaypoint();
	}

	@Override
    public BlockRenderType getRenderShape(BlockState state){
        return BlockRenderType.MODEL;
    }
}
