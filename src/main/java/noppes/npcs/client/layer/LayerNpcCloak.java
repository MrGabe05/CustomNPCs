package noppes.npcs.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class LayerNpcCloak extends LayerInterface{


	public LayerNpcCloak(LivingRenderer render) {
		super(render);
	}

	@Override
    public void render(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
		if(npc.textureCloakLocation == null){
			if(npc.display.getCapeTexture() == null || npc.display.getCapeTexture().isEmpty() || !(base instanceof PlayerModel))
				return;
			npc.textureCloakLocation = new ResourceLocation(npc.display.getCapeTexture());
		}

        mStack.pushPose();
        mStack.translate(0.0D, 0.0D, 0.125D);
        double d0 = MathHelper.lerp(partialTicks, npc.prevChasingPosX, npc.chasingPosX) - MathHelper.lerp(partialTicks, npc.xo, npc.getX());
        double d1 = MathHelper.lerp(partialTicks, npc.prevChasingPosY, npc.chasingPosY) - MathHelper.lerp(partialTicks, npc.yo, npc.getY());
        double d2 = MathHelper.lerp(partialTicks, npc.prevChasingPosZ, npc.chasingPosZ) - MathHelper.lerp(partialTicks, npc.zo, npc.getZ());
        float f = npc.yBodyRotO + (npc.yBodyRot - npc.yBodyRotO);
        double d3 = MathHelper.sin(f * ((float)Math.PI / 180F));
        double d4 = -MathHelper.cos(f * ((float)Math.PI / 180F));
        float f1 = (float)d1 * 10.0F;
        f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
        float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
        f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
        float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
        f3 = MathHelper.clamp(f3, -20.0F, 20.0F);
        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        f1 = f1 + MathHelper.sin(MathHelper.lerp(partialTicks, npc.walkDistO, npc.walkDist) * 6.0F) * 32.0F * partialTicks;
        if (npc.isCrouching()) {
            f1 += 25.0F;
        }

        mStack.mulPose(Vector3f.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
        mStack.mulPose(Vector3f.ZP.rotationDegrees(f3 / 2.0F));
        mStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - f3 / 2.0F));
        IVertexBuilder ivertexbuilder = typeBuffer.getBuffer(RenderType.entityTranslucent(npc.textureCloakLocation));


        ((PlayerModel) base).renderCloak(mStack, ivertexbuilder, lightmapUV, OverlayTexture.NO_OVERLAY);
        mStack.popPose();

	}

	@Override
	public void rotate(MatrixStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		// TODO Auto-generated method stub
		
	}
}
