package noppes.npcs.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import noppes.npcs.blocks.tiles.TileMailbox;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiOpen;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class BlockMailbox extends BlockInterface{
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);

    public final int type;
	
	public BlockMailbox(int type) {
        super(Block.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).strength(5.0F, 10));
        this.type = type;
	}

    @Override
    public String getDescriptionId() {
        return "block.customnpcs.npcmailbox";
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        return false;
    }
    
    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
    	if(!level.isClientSide){
            Packets.send((ServerPlayerEntity)player, new PacketGuiOpen(EnumGuiType.PlayerMailbox, pos));
    	}
		return ActionResultType.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        int l = MathHelper.floor((double)(context.getPlayer().yRot * 4.0F / 360.0F) + 0.5D) & 3;
        return defaultBlockState().setValue(ROTATION, l % 4);
    }
    
    //@Override
    //public boolean isFullCube(BlockState state){
    //    return false;
    //}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileMailbox().setModel(type);
	}
}
