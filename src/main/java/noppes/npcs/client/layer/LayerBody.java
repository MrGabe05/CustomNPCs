package noppes.npcs.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.model.ModelPlaneNpcsRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import noppes.npcs.ModelPartData;
import noppes.npcs.client.model.Model2DRenderer;
import noppes.npcs.constants.EnumParts;

public class LayerBody extends LayerInterface{
	private Model2DRenderer lWing;
	private Model2DRenderer rWing;

	private Model2DRenderer breasts;
	private ModelRenderer breasts2;
	private ModelRenderer breasts3;

	private ModelPlaneNpcsRenderer skirt;

	private Model2DRenderer fin;
	
	public LayerBody(LivingRenderer render) {
		super(render);
		createParts();
	}

	private void createParts(){		
		lWing = new Model2DRenderer(base, 56, 16, 8, 16);
		lWing.mirror = true;
		lWing.setPos(2F, 2.5f, 1F);
		lWing.setRotationOffset(8, 14, 0);
        setRotation(lWing, 0.7141593F, -0.5235988F, -0.5090659F);

		
        rWing = new Model2DRenderer(base, 56, 16, 8, 16);
        rWing.setPos(-2F, 2.5f, 1F);
        rWing.setRotationOffset(-8, 14, 0);
        setRotation(rWing, 0.7141593F, 0.5235988F, 0.5090659F);


		breasts = new Model2DRenderer(base, 20f, 22, 8, 3);
		breasts.setPos(-3.6F, 5.2f, -3f);
		breasts.setScale(0.17f, 0.19f);
		breasts.setThickness(1);

		breasts2 = new ModelRenderer(base.texWidth, base.texHeight, 0, 0);
		Model2DRenderer bottom = new Model2DRenderer(base, 20f, 22, 8, 4);
		bottom.setPos(-3.6F, 5f, -3.1f);
		bottom.setScale(0.225f, 0.20f);
		bottom.setThickness(2f);
		bottom.xRot = -(float) (Math.PI / 10);
		breasts2.addChild(bottom);

		breasts3 = new ModelRenderer(base.texWidth, base.texHeight, 0, 0);

		Model2DRenderer right = new Model2DRenderer(base, 20f, 23, 3, 2);
		right.setPos(-3.8F, 5.3f, -3.6f);
		right.setScale(0.12f, 0.14f);
		right.setThickness(1.75f);
		breasts3.addChild(right);
		
		Model2DRenderer right2 = new Model2DRenderer(base, 20f, 22, 3, 1);
		right2.setPos(-3.79F, 4.1f, -3.14f);
		right2.setScale(0.06f, 0.07f);
		right2.setThickness(1.75f);
		right2.xRot = (float) (Math.PI / 9);
		breasts3.addChild(right2);
		
		Model2DRenderer right3 = new Model2DRenderer(base, 20f, 24, 3, 1);
		right3.setPos(-3.79F, 5.3f, -3.6f);
		right3.setScale(0.06f, 0.07f);
		right3.setThickness(1.75f);
		right3.xRot = (float) (-Math.PI / 9);
		breasts3.addChild(right3);
		
		Model2DRenderer right4 = new Model2DRenderer(base, 21f, 23, 1, 2);
		right4.setPos(-1.8f, 5.3f, -3.14f);
		right4.setScale(0.12f, 0.14f);
		right4.setThickness(1.75f);
		right4.yRot = (float) (Math.PI / 9);
		breasts3.addChild(right4);

		Model2DRenderer left = new Model2DRenderer(base, 25f, 23, 3, 2);
		left.setPos(0.8F, 5.3f, -3.6f);
		left.setScale(0.12f, 0.14f);
		left.setThickness(1.75f);
		breasts3.addChild(left);
		
		Model2DRenderer left2 = new Model2DRenderer(base, 25f, 22, 3, 1);
		left2.setPos(0.81F, 4.1f, -3.18f);
		left2.setScale(0.06f, 0.07f);
		left2.setThickness(1.75f);
		left2.xRot = (float) (Math.PI / 9);
		breasts3.addChild(left2);
		
		Model2DRenderer left3 = new Model2DRenderer(base, 25f, 24, 3, 1);
		left3.setPos(0.81F, 5.3f, -3.6f);
		left3.setScale(0.06f, 0.07f);
		left3.setThickness(1.75f);
		left3.xRot = (float) (-Math.PI / 9);
		breasts3.addChild(left3);
		
		Model2DRenderer left4 = new Model2DRenderer(base, 24f, 23, 1, 2);
		left4.setPos(0.8f, 5.3f, -3.6f);
		left4.setScale(0.12f, 0.14f);
		left4.setThickness(1.75f);
		left4.yRot = (float) (-Math.PI / 9);
		breasts3.addChild(left4);
		
		skirt = new ModelPlaneNpcsRenderer(base, 58, 18);
		skirt.addSidePlane(0, 0, 0, 9, 2);
		
		ModelPlaneNpcsRenderer part1 = new ModelPlaneNpcsRenderer(base, 58, 18);
		part1.addSidePlane(2, 0, 0, 9, 2);
		part1.yRot = -(float) (Math.PI/2);
		skirt.addChild(part1);

		skirt.setPos(2.4F, 8.8F, 0F);
		setRotation(skirt, 0.3F, -0.2f, -0.2F);

		fin = new Model2DRenderer(base, 56, 20, 8, 12);
		fin.setPos(-0.5F, 12, 10);
		fin.setScale(0.74f);
		fin.yRot = (float)Math.PI / 2;
	}
	@Override
	public void render(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
		base.body.translateAndRotate(mStack);
		renderSkirt(mStack, typeBuffer, lightmapUV);
		renderWings(mStack, typeBuffer, lightmapUV);
		renderFin(mStack, typeBuffer, lightmapUV);
		renderBreasts(mStack, typeBuffer, lightmapUV);
	}
	private void renderWings(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV){
		ModelPartData data = playerdata.getPartData(EnumParts.WINGS);
		if(data == null)
			return;
		preRender(data);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));
		rWing.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		lWing.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
	}
	
	private void renderSkirt(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV){
		ModelPartData data = playerdata.getPartData(EnumParts.SKIRT);
		if(data == null)
			return;
		preRender(data);

		mStack.pushPose();
		mStack.scale(1.7f, 1.04f, 1.6f);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));
		for(int i = 0; i < 10; i++){
			mStack.mulPose(Vector3f.YP.rotationDegrees(36));
			skirt.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		mStack.popPose();
	}
	
	private void renderFin(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV){
		ModelPartData data = playerdata.getPartData(EnumParts.FIN);
		if(data == null)
			return;
		preRender(data);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));
		fin.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
	}
	
	private void renderBreasts(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV){
		ModelPartData data = playerdata.getPartData(EnumParts.BREASTS);
		if(data == null)
			return;
		data.playerTexture = true;
		preRender(data);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));
		if(data.type == 0)
			breasts.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		if(data.type == 1)
			breasts2.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		if(data.type == 2)
			breasts3.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
	}

	@Override
	public void rotate(MatrixStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		rWing.xRot = 0.7141593F;
		rWing.zRot = 0.5090659F;
		
		lWing.xRot = 0.7141593F;
		lWing.zRot = -0.5090659F;

		float motion = Math.abs(MathHelper.sin(limbSwing * 0.033F + (float)Math.PI) * 0.4F) * limbSwingAmount;
		if(!npc.isOnGround() || motion > 0.01){
			float speed = 0.55f + 0.5f * motion;
            float y = MathHelper.sin(ageInTicks * 0.55F);

            rWing.zRot += y * 0.5f * speed;
            rWing.xRot += y * 0.5f * speed;

            lWing.zRot -= y * 0.5f * speed;
            lWing.xRot += y * 0.5f * speed;
		}
		else{
	        lWing.zRot += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
	        rWing.zRot -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
	        lWing.xRot += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
	        rWing.xRot += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		}
		
		setRotation(skirt, 0.3F, -0.2f, -0.2F);
    	skirt.xRot += base.leftArm.xRot * 0.04f;
    	skirt.zRot += base.leftArm.xRot * 0.06f;
        skirt.zRot -= MathHelper.cos(ageInTicks * 0.09F) * 0.04F - 0.05F;
	}

}
