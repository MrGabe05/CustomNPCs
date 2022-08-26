package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import noppes.npcs.blocks.BlockCarpentryBench;
import noppes.npcs.blocks.tiles.TileBlockAnvil;
import noppes.npcs.client.model.blocks.ModelCarpentryBench;

public class BlockCarpentryBenchRenderer extends TileEntityRenderer<TileBlockAnvil> {

	private final ModelCarpentryBench model = new ModelCarpentryBench();
	private static final ResourceLocation TEXTURE = new ResourceLocation("customnpcs", "textures/models/carpentrybench.png");
	private static final RenderType type = RenderType.entityCutout(TEXTURE);

	public BlockCarpentryBenchRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(TileBlockAnvil te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
		int rotation = 0;
		if(te.getBlockPos() != BlockPos.ZERO){
			rotation = te.getLevel().getBlockState(te.getBlockPos()).getValue(BlockCarpentryBench.ROTATION);
		}
		matrixStack.pushPose();
		RenderSystem.disableBlend();
		RenderSystem.enableLighting();
		matrixStack.translate((float) 0.5f, (float) 1.4f, (float) 0.5f);
		matrixStack.scale(0.95f, 0.95f, 0.95f);
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(90 * rotation));
		model.renderToBuffer(matrixStack, buffer.getBuffer(type), light, overlay, 1, 1, 1, 1);
		matrixStack.popPose();
	}
}
