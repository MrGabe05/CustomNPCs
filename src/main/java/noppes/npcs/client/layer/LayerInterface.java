package noppes.npcs.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartData;
import noppes.npcs.entity.EntityCustomNpc;

public abstract class LayerInterface extends LayerRenderer{
	protected LivingRenderer render;
	protected EntityCustomNpc npc;
	protected ModelData playerdata;
	public BipedModel base;

	private int color;
	
	public LayerInterface(LivingRenderer render){
		super(render);
		this.render = render;
		base = (BipedModel) render.getModel();
	}
	
	public void setColor(ModelPartData data, LivingEntity entity){
	}

	protected float red(){
		if(npc.hurtTime > 0 || npc.deathTime > 0){
			return 1;
		}
		return (color >> 16 & 255) / 255f;
	}

	protected float green(){
		if(npc.hurtTime > 0 || npc.deathTime > 0){
			return 0;
		}
		return (color >> 8  & 255) / 255f;
	}

	protected float blue(){
		if(npc.hurtTime > 0 || npc.deathTime > 0){
			return 0;
		}
		return (color & 255) / 255f;
	}

	protected float alpha(){
		if(npc.hurtTime > 0 || npc.deathTime > 0){
			return 0.3f;
		}
		boolean flag = !npc.isInvisible();
		boolean flag1 = !flag && !npc.isInvisibleTo(Minecraft.getInstance().player);
		return flag1? 0.15f : 0.99f;
	}
	
	public void preRender(ModelPartData data){
    	if(npc.hurtTime > 0 || npc.deathTime > 0){
    		return;
    	}
    	color = data.color;
    	if(npc.display.getTint() != 0xFFFFFF){
        	if(data.color != 0xFFFFFF)
        		color = blend(data.color, npc.display.getTint(), 0.5f);
        	else
        		color = npc.display.getTint();
    	}
	}
	
	public int blend (int color1, int color2, float ratio) {
	    if (ratio >= 1f) 
	        return color2;
	    
	    if (ratio <= 0f)
	        return color1;

	    int aR = ((color1 & 0xff0000) >> 16);
	    int aG = ((color1 & 0xff00) >> 8);
	    int aB = (color1 & 0xff);

	    int bR = ((color2 & 0xff0000) >> 16);
	    int bG = ((color2 & 0xff00) >> 8);
	    int bB = (color2 & 0xff);

	    int R = (int) ((aR + (bR - aR) * ratio));
	    int G = (int) ((aG + (bG - aG) * ratio));
	    int B = (int) ((aB + (bB - aB) * ratio));

	    return R << 16 | G << 8 | B;
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, Entity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch){
		npc = (EntityCustomNpc)entity;
		if(npc.isInvisibleTo(Minecraft.getInstance().player))
			return;
		playerdata = npc.modelData;


		base = (BipedModel) render.getModel();
		rotate(matrixStackIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);

		matrixStackIn.pushPose();
        if (entity.isInvisible()){
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.15F);
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(770, 771);
			RenderSystem.alphaFunc(516, 0.003921569F);
        }
    	if(npc.hurtTime > 0 || npc.deathTime > 0){
			RenderSystem.color4f(1, 0, 0, 0.3f);
    	}
        if (npc.isCrouching()){
			//matrixStackIn.translate(0.0F, 0.2F, 0.0F);
        }
		RenderSystem.enableRescaleNormal();
		render(matrixStackIn, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
		RenderSystem.disableRescaleNormal();

        if (entity.isInvisible()){
			RenderSystem.disableBlend();
			RenderSystem.alphaFunc(516, 0.1F);
			RenderSystem.depthMask(true);
        }
		matrixStackIn.popPose();
	}

	public RenderType getRenderType(ModelPartData data){
		ResourceLocation resource = npc.textureLocation;
		if(!data.playerTexture){
			resource = data.getResource();
		}
		return RenderType.entityTranslucent(resource);

//		boolean flag = !npc.isInvisible();
//		boolean flag1 = !flag && !npc.isInvisibleTo(Minecraft.getInstance().player);
//
//		if (flag1) {
//			//return RenderType.itemEntityTranslucentCull(resource);
//		} else if (flag) {
//			return RenderType.entityTranslucent(resource);
//		}
	}

	public void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}

//	@Override
//	public boolean shouldCombineTextures() {
//		return false;
//	}

	public abstract void render(MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);
	public abstract void rotate(MatrixStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);
	
}
