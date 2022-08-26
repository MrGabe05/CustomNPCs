package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileDoor;

import java.util.Random;

public class BlockDoorRenderer extends BlockRendererInterface<TileDoor> {
	
	private static Random random = new Random();

    public BlockDoorRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TileDoor tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
        BlockState original = tile.getLevel().getBlockState(tile.getBlockPos());
        if(original.isAir()){
            return;
        }

        BlockPos lowerPos = tile.getBlockPos();

        if(original.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER){
            lowerPos = tile.getBlockPos().below();
        }

        BlockPos upperPos = lowerPos.above();

        TileDoor lowerTile = (TileDoor) tile.getLevel().getBlockEntity(lowerPos);
        TileDoor upperTile = (TileDoor) tile.getLevel().getBlockEntity(upperPos);

        if(lowerTile==null || upperTile==null)
            return;

        BlockState lowerState = lowerTile.getBlockState();
        BlockState upperState = upperTile.getBlockState();


        Block b = lowerTile.blockModel;

        if (overrideModel()) {
            b = CustomBlocks.scripted_door;
        }
        BlockState state = b.defaultBlockState();

        state = state.setValue(DoorBlock.HALF, original.getValue(DoorBlock.HALF));
        state = state.setValue(DoorBlock.FACING, lowerState.getValue(DoorBlock.FACING));
        state = state.setValue(DoorBlock.OPEN, lowerState.getValue(DoorBlock.OPEN));
        state = state.setValue(DoorBlock.HINGE, upperState.getValue(DoorBlock.HINGE));
        state = state.setValue(DoorBlock.POWERED, upperState.getValue(DoorBlock.POWERED));

        matrixStack.pushPose();

        //RenderHelper.enableStandardItemLighting();
        //matrixStack.translate(0.5, 0, 0.5);

        //matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90));
        renderBlock(matrixStack, buffer, tile, lowerState.getBlock(), state, light, overlay);

        matrixStack.popPose();
    }

	
	private void renderBlock(MatrixStack matrixStack, IRenderTypeBuffer buffer, TileDoor tile, Block b, BlockState state, int light, int overlay){
        //this.bind(AtlasTexture.LOCATION_BLOCKS);
        //RenderSystem.translatef(-0.5F, -0, 0.5F);
        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        IBakedModel ibakedmodel = dispatcher.getBlockModel(state);
        if(ibakedmodel == null){
            dispatcher.renderSingleBlock(state, matrixStack, buffer, light, overlay);
        }
        else{
            dispatcher.getModelRenderer().renderModel(matrixStack.last(), buffer.getBuffer(RenderTypeLookup.getRenderType(state, false)), state, ibakedmodel, 1, 1, 1, light, overlay, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
        }
	}
	
	private boolean overrideModel(){
		ItemStack held = Minecraft.getInstance().player.getMainHandItem();
		if(held == null)
			return false;
		
		return held.getItem() == CustomItems.wand || held.getItem() == CustomItems.scripter || held.getItem() == CustomBlocks.scripted_door_item;
	}
}
