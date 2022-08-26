package noppes.npcs.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.api.constants.MarkType;
import noppes.npcs.client.model.Model2DRenderer;
import noppes.npcs.controllers.data.MarkData.Mark;

public class MarkRenderer {
	public static ResourceLocation markExclamation = new ResourceLocation("customnpcs", "textures/marks/exclamation.png");
	public static ResourceLocation markQuestion = new ResourceLocation("customnpcs", "textures/marks/question.png");
	public static ResourceLocation markPointer = new ResourceLocation("customnpcs", "textures/marks/pointer.png");
	public static ResourceLocation markCross = new ResourceLocation("customnpcs", "textures/marks/cross.png");
	public static ResourceLocation markSkull = new ResourceLocation("customnpcs", "textures/marks/skull.png");
	public static ResourceLocation markStar = new ResourceLocation("customnpcs", "textures/marks/star.png");
	
	public static int displayList = -1;
	public static Model2DRenderer renderer = new Model2DRenderer(0, 0, 32, 32, 32, 32);

	public static void render(RenderLivingEvent.Post event, Mark mark){
		MatrixStack matrixStack = event.getMatrixStack();
		matrixStack.pushPose();

		int color = mark.color;
		float red = (float)(color >> 16 & 255) / 255.0F;
		float green = (float)(color >> 8 & 255) / 255.0F;
		float blue = (float)(color & 255) / 255.0F;

		ResourceLocation location = markExclamation;
		if(mark.type == MarkType.QUESTION)
			location = markQuestion;
		else if(mark.type == MarkType.POINTER)
			location = markPointer;
		else if(mark.type == MarkType.CROSS)
			location = markCross;
		else if(mark.type == MarkType.SKULL)
			location = markSkull;
		else if(mark.type == MarkType.STAR)
			location = markStar;

		matrixStack.translate(0, event.getEntity().getBbHeight() + 0.6, 0);
		matrixStack.mulPose(Vector3f.XN.rotationDegrees(180));
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(event.getEntity().yHeadRot));
		matrixStack.translate(-0.5f, 0, 0);
		renderer.render(matrixStack, event.getBuffers().getBuffer(RenderType.entityCutout(location)), event.getLight(), OverlayTexture.NO_OVERLAY, red, green, blue, 1);
		matrixStack.popPose();
	}
	
//	public static void render(LivingEntity entity, double x, double y, double z, Mark mark){
//		Minecraft mc = Minecraft.getInstance();
//		RenderSystem.pushMatrix();
//        int color = mark.color;
//        float red = (float)(color >> 16 & 255) / 255.0F;
//        float blue = (float)(color >> 8 & 255) / 255.0F;
//        float green = (float)(color & 255) / 255.0F;
//        RenderSystem.color4f(red, blue, green, 1);
//		RenderSystem.translated(x, y + entity.getBbHeight() + 0.6, z);
//		RenderSystem.rotatef(-entity.yHeadRot, 0, 1, 0);
//
//		if(mark.type == MarkType.EXCLAMATION)
//			Minecraft.getInstance().getTextureManager().bind(markExclamation);
//		else if(mark.type == MarkType.QUESTION)
//			Minecraft.getInstance().getTextureManager().bind(markQuestion);
//		else if(mark.type == MarkType.POINTER)
//			Minecraft.getInstance().getTextureManager().bind(markPointer);
//		else if(mark.type == MarkType.CROSS)
//			Minecraft.getInstance().getTextureManager().bind(markCross);
//		else if(mark.type == MarkType.SKULL)
//			Minecraft.getInstance().getTextureManager().bind(markSkull);
//		else if(mark.type == MarkType.STAR)
//			Minecraft.getInstance().getTextureManager().bind(markStar);
//
//		if(displayList >= 0){
//			GL11.glCallList(displayList);
//		}
//		else{
//			displayList = GL11.glGenLists(1);
//			GL11.glNewList(displayList, GL11.GL_COMPILE);
//			RenderSystem.translatef(-0.5f, 0, 0);
//	        Model2DRenderer.renderItemIn2D(Tessellator.getInstance().getBuilder(), 0f, 0f, 1f, 1f, 32, 32, 0.0625F);
//			GL11.glEndList();
//		}
//		RenderSystem.popMatrix();
//	}
}
