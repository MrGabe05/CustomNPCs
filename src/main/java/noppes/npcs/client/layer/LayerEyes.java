package noppes.npcs.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector4f;

public class LayerEyes extends LayerInterface{
	RenderType type = RenderType.create("mpmeyes", DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, 7, 256, RenderType.State.builder().setCullState(new RenderState.CullState(true)).setTextureState(new RenderState.TextureState()).setDiffuseLightingState(new RenderState.DiffuseLightingState(true)).setLightmapState(new RenderState.LightmapState(true)).createCompositeState(true));

	public LayerEyes(LivingRenderer render) {
		super(render);
	}

	@Override
	public void render(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
		if(!playerdata.eyes.isEnabled())
			return;
		mStack.pushPose();
		base.head.translateAndRotate(mStack);
		float scale = 0.0625f;
		//mStack.translate(player.posX, player.getY(), player.posZ);

		//mStack.translate(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ);
		mStack.scale(scale, scale, -scale);
		mStack.translate(0, (playerdata.eyes.type == 1?1:2) - playerdata.eyes.eyePos, 0);
		IVertexBuilder ivertex = typeBuffer.getBuffer(type);
		MatrixStack.Entry entry = mStack.last();
		Matrix4f matrix = entry.pose();
		drawLeft(ivertex, matrix, lightmapUV);
		drawRight(ivertex, matrix, lightmapUV);
		drawBrowsLeft(ivertex, matrix, lightmapUV);
		drawBrowsRight(ivertex, matrix, lightmapUV);
		mStack.popPose();
	}


	private void drawLeft(IVertexBuilder ivertex, Matrix4f matrix, int lightmapUV){
		if(playerdata.eyes.pattern == 2)
			return;
		drawRect(ivertex, matrix, lightmapUV, 3, -5, 1, -4, 0xF6F6F6, 4.01f, false);
		drawRect(ivertex, matrix, lightmapUV, 2, -5, 1, -4, playerdata.eyes.color, 4.011f, playerdata.eyes.type == 1);
		if(playerdata.eyes.glint && npc.isAlive()){
			drawRect(ivertex, matrix, lightmapUV, 1.5f, -4.9f, 1.9f, -4.5f, 0xFFFFFFFF, 4.012f, false);
		}
		if(playerdata.eyes.type == 1){
			drawRect(ivertex, matrix, lightmapUV, 3, -4, 1, -3, 0xFFFFFF, 4.01f, true);
			drawRect(ivertex, matrix, lightmapUV, 2, -4, 1, -3, playerdata.eyes.color, 4.011f, false);
		}
	}

	private void drawRight(IVertexBuilder ivertex, Matrix4f matrix, int lightmapUV){
		if(playerdata.eyes.pattern == 1)
			return;
		drawRect(ivertex, matrix, lightmapUV, -3, -5, -1, -4, 0xF6F6F6, 4.01f, false);
		drawRect(ivertex, matrix, lightmapUV, -2, -5, -1, -4, playerdata.eyes.color, 4.011f, playerdata.eyes.type == 1);
		if(playerdata.eyes.glint && npc.isAlive()){
			drawRect(ivertex, matrix, lightmapUV, -1.5f, -4.9f, -1.1f, -4.5f, 0xFFFFFFFF, 4.012f, false);
		}
		if(playerdata.eyes.type == 1){
			drawRect(ivertex, matrix, lightmapUV, -3, -4, -1, -3, 0xFFFFFF, 4.01f, true);
			drawRect(ivertex, matrix, lightmapUV, -2, -4, -1, -3, playerdata.eyes.color, 4.011f, false);
		}
	}

	private void drawBrowsLeft(IVertexBuilder ivertex, Matrix4f matrix, int lightmapUV){
		if(playerdata.eyes.pattern == 2)
			return;
		float offsetY = 0;
		if(playerdata.eyes.blinkStart > 0 && npc.isAlive()){
			float f = (System.currentTimeMillis() - playerdata.eyes.blinkStart) / 150f;
			if(f > 1)
				f = 2 - f;
			if(f < 0){
				playerdata.eyes.blinkStart = 0;
				f = 0;
			}
			f = 1;
			offsetY = (playerdata.eyes.type == 1?2:1) * f;
			drawRect(ivertex, matrix, lightmapUV, 3, -5, 1, -5 + offsetY, playerdata.eyes.skinColor, 4.013f, false);
		}
		if(playerdata.eyes.browThickness > 0){
			float thickness = playerdata.eyes.browThickness / 10f;
			drawRect(ivertex, matrix, lightmapUV, 1, -5 + offsetY, 3, -5 - thickness + offsetY, playerdata.eyes.browColor, 4.014f, false);
		}
	}

	private void drawBrowsRight(IVertexBuilder ivertex, Matrix4f matrix, int lightmapUV){
		if(playerdata.eyes.pattern == 1)
			return;
		float offsetY = 0;
		if(playerdata.eyes.blinkStart > 0 && npc.isAlive()){
			float f = (System.currentTimeMillis() - playerdata.eyes.blinkStart) / 150f;
			if(f > 1)
				f = 2 - f;
			if(f < 0){
				playerdata.eyes.blinkStart = 0;
				f = 0;
			}
			f = 1;
			offsetY = (playerdata.eyes.type == 1?2:1) * f;
			drawRect(ivertex, matrix, lightmapUV, -3, -5, -1, -5 + offsetY, playerdata.eyes.skinColor, 4.013f, false);
		}
		if(playerdata.eyes.browThickness > 0){
			float thickness = playerdata.eyes.browThickness / 10f;
			drawRect(ivertex, matrix, lightmapUV, -3, -5 + offsetY, -1, -5 - thickness + offsetY, playerdata.eyes.browColor, 4.014f, false);
		}
	}

	public void drawRect(IVertexBuilder ivertex, Matrix4f matrix, int lightmapUV, float x, float y, float x2, float y2, int color, float z, boolean darken)
	{
		float j1;

		if (x < x2)
		{
			j1 = x;
			x = x2;
			x2 = j1;
		}

		if (y < y2)
		{
			j1 = y;
			y = y2;
			y2 = j1;
		}

		if(npc.deathTime > 0 || npc.hurtTime > 0){
			color = blend(color, 0xFF0000, 0.5f);
		}
		float f1 = (float)(color >> 16 & 255) / 255.0F;
		float f2 = (float)(color >> 8 & 255) / 255.0F;
		float f3 = (float)(color & 255) / 255.0F;
		if(darken){
			f1 *= 0.96f;
			f2 *= 0.96f;
			f3 *= 0.96f;
		}
		//RenderSystem.color4f(1, 1, 1, 1);
		draw(ivertex, matrix, lightmapUV, x, y, z,f1, f2, f3);
		draw(ivertex, matrix, lightmapUV, x, y2, z,f1, f2, f3);
		draw(ivertex, matrix, lightmapUV, x2, y2, z,f1, f2, f3);
		draw(ivertex, matrix, lightmapUV, x2, y, z,f1, f2, f3);
	}

	private void draw(IVertexBuilder ivertex, Matrix4f matrix, int lightmapUV, float x, float y, float z, float red, float green, float blue){
		Vector4f v = new Vector4f(x, y, z, 1.0F);
		v.transform(matrix);
		ivertex.vertex(v.x(), v.y(), v.z()).color(red, green, blue, 1).uv2(lightmapUV).endVertex();
	}

	@Override
	public void rotate(MatrixStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		
	}

}
