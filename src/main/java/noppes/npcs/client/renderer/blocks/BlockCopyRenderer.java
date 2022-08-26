package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.schematics.Schematic;

public class BlockCopyRenderer extends BlockRendererInterface<TileCopy>{
	private final static ItemStack item = new ItemStack(CustomBlocks.copy);
	public static Schematic schematic = null;
	public static BlockPos pos = null;

    public BlockCopyRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
	public void render(TileCopy tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
        matrixStack.pushPose();
        RenderSystem.color4f(1, 1, 1, 1);

        //RenderHelper.enableStandardItemLighting();
        RenderSystem.disableBlend();
        drawSelectionBox(matrixStack, buffer, new BlockPos(tile.width, tile.height, tile.length));
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
		Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemCameraTransforms.TransformType.NONE, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
        matrixStack.popPose();
	}

	
    public void drawSelectionBox(MatrixStack matrixStack, IRenderTypeBuffer buffer, BlockPos pos){
        AxisAlignedBB bb = new AxisAlignedBB(BlockPos.ZERO, pos);
        matrixStack.translate(0.001f, 0.001f, 0.001f);
        WorldRenderer.renderLineBox(matrixStack, buffer.getBuffer(RenderType.lines()), bb, 1, 0, 0, 1);
    }
}
