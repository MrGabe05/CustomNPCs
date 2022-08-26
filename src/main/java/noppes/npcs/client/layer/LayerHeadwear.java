package noppes.npcs.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.model.part.head.ModelHeadwear;
import noppes.npcs.entity.EntityCustomNpc;

public class LayerHeadwear extends LayerInterface implements LayerPreRender{
	
	public LayerHeadwear(LivingRenderer render) {
		super(render);
	}
	@Override
	public void render(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
		if(CustomNpcs.HeadWearType != 1 || npc.textureLocation == null)
			return;
		float red = 1, blue = 1, green = 1;
    	if(npc.hurtTime <= 0 && npc.deathTime <= 0){
    		int color = npc.display.getTint();
        	red = (color >> 16 & 255) / 255f;
        	green = (color >> 8  & 255) / 255f;
        	blue = (color & 255) / 255f;
    		//RenderSystem.color4f(red, green, blue, 1);
    	}
		base.head.translateAndRotate(mStack);
		IVertexBuilder ivertex = typeBuffer.getBuffer(RenderType.entityTranslucent(npc.textureLocation));
		ModelHeadwear.getModel(npc.textureLocation, base).render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red, green, blue, 1);
	}

	@Override
	public void rotate(MatrixStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void preRender(EntityCustomNpc npc) {
		base.hat.visible = CustomNpcs.HeadWearType != 1;
		ModelHeadwear.getModel(npc.textureLocation, base).config =  null;
	}

}
