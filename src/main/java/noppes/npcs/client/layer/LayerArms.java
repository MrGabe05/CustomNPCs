package noppes.npcs.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import noppes.npcs.ModelPartData;
import noppes.npcs.client.model.Model2DRenderer;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.constants.EnumParts;

public class LayerArms extends LayerInterface{
	private Model2DRenderer lClaw;
	private Model2DRenderer rClaw;
	
	public LayerArms(RenderNPCInterface render) {
		super(render);
		createParts();
	}

	private void createParts(){
		lClaw = new Model2DRenderer(base, 0, 16, 4, 4);
		lClaw.setPos(3F, 14f, -2);
		lClaw.yRot = (float) (Math.PI / -2);
		lClaw.setScale(0.25f);

		rClaw = new Model2DRenderer(base, 0, 16, 4, 4);
		rClaw.setPos(-2F, 14f, -2);
		rClaw.yRot = (float) (Math.PI / -2);
		rClaw.setScale(0.25f);
	}
	@Override
	public void render(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
		ModelPartData data = playerdata.getPartData(EnumParts.CLAWS);
		if(data == null)
			return;
		preRender(data);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));
		if(data.pattern == 0 || data.pattern == 1){
			mStack.pushPose();
			base.leftArm.translateAndRotate(mStack);
			lClaw.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
			mStack.popPose();
		}
		if(data.pattern == 0 || data.pattern == 2){
			mStack.pushPose();
			base.rightArm.translateAndRotate(mStack);
			rClaw.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
			mStack.popPose();
		}
	}

	@Override
	public void rotate(MatrixStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

	}

}
