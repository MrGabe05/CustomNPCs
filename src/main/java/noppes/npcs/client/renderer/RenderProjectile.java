package noppes.npcs.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.entity.EntityProjectile;

@OnlyIn(Dist.CLIENT)
public class RenderProjectile<T extends EntityProjectile> extends EntityRenderer<T> {
	

	public boolean renderWithColor = true;
	private static final ResourceLocation field_110780_a = new ResourceLocation("textures/entity/arrow.png");
	private static final ResourceLocation field_110798_h = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private boolean crash = false;
    private boolean crash2 = false;

	public RenderProjectile(EntityRendererManager manager) {
		super(manager);
	}

    @Override
    public void render(T projectile, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        Minecraft mc = Minecraft.getInstance();
        matrixStack.pushPose();
        float scale = (float) projectile.getSize() / 10.0F;
        ItemStack item = projectile.getItemDisplay();
        matrixStack.scale(scale, scale, scale);

        if (projectile.isArrow()) {
            //RenderType type = RenderType.itemEntityTranslucentCull(this.getTextureLocation(projectile));

            matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, projectile.yRotO, projectile.yRot) - 90.0F));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(MathHelper.lerp(partialTicks, projectile.xRotO, projectile.xRot)));

            float f9 = (float)projectile.arrowShake - partialTicks;
            if (f9 > 0.0F) {
                float f10 = -MathHelper.sin(f9 * 3.0F) * f9;
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(f10));
            }

            matrixStack.mulPose(Vector3f.XP.rotationDegrees(45.0F));
            matrixStack.scale(0.05625F, 0.05625F, 0.05625F);
            matrixStack.translate(-4.0D, 0.0D, 0.0D);
            IVertexBuilder ivertexbuilder = buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(projectile)));
            MatrixStack.Entry matrixstack$entry = matrixStack.last();
            Matrix4f matrix4f = matrixstack$entry.pose();
            Matrix3f matrix3f = matrixstack$entry.normal();
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLight);

            for(int j = 0; j < 4; ++j) {
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLight);
                this.drawVertex(matrix4f, matrix3f, ivertexbuilder, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight);
                this.drawVertex(matrix4f, matrix3f, ivertexbuilder, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight);
                this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLight);
            }
        } else if (projectile.is3D()) {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, projectile.yRotO, projectile.yRot) - 180.0F));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, projectile.xRotO, projectile.xRot)));

            matrixStack.translate(0, -0.125f, 0.25f);
            if (item.getItem() instanceof BlockItem && Block.byItem(item.getItem()).defaultBlockState().getRenderShape() == BlockRenderType.ENTITYBLOCK_ANIMATED){
                matrixStack.translate(0.0F, 0.1875F, -0.3125F);
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(20.0F));
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(45.0F));
                float f8 = 0.375F;
                matrixStack.scale(-f8, -f8, f8);
            }
            if(!crash) {
                try {
                    mc.getItemRenderer().renderStatic(item, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
                }
                catch(Throwable e){
                    crash = true;
                }
            }
            else if(!crash2) {//some mods like techguns dont like to be rendered in THIRD_PERSON_RIGHT_HAND so try rendering NONE
                try {
                    mc.getItemRenderer().renderStatic(item, ItemCameraTransforms.TransformType.NONE, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
                }
                catch(Throwable ee){
                    crash2 = true;
                }
            }
            else {
                mc.getItemRenderer().renderStatic(new ItemStack(Blocks.DIRT), ItemCameraTransforms.TransformType.GROUND, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
            }
        }
        else{
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            matrixStack.mulPose(this.entityRenderDispatcher.camera.rotation());
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            //mc.getItemRenderer().renderStatic(item, ItemCameraTransforms.TransformType.NONE);
            mc.getItemRenderer().renderStatic(item, ItemCameraTransforms.TransformType.GROUND, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);

        }
        if (projectile.is3D() && projectile.glows()) {
            //RenderSystem.disableLighting();
        }
        matrixStack.popPose();
        //RenderSystem.enableLighting();
    }

    protected ResourceLocation func_110779_a(EntityProjectile projectile) {
        return projectile.isArrow() ? field_110780_a : AtlasTexture.LOCATION_BLOCKS;
    }

    @Override
	public ResourceLocation getTextureLocation(T par1Entity) {
        return par1Entity.isArrow() ? field_110780_a : AtlasTexture.LOCATION_BLOCKS;
	}

    public void drawVertex(Matrix4f matrix, Matrix3f normals, IVertexBuilder vertexBuilder, int offsetX, int offsetY, int offsetZ, float textureX, float textureY, int p_229039_9_, int p_229039_10_, int p_229039_11_, int packedLightIn) {
        vertexBuilder.vertex(matrix, (float)offsetX, (float)offsetY, (float)offsetZ).color(255, 255, 255, 255).uv(textureX, textureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normals, (float)p_229039_9_, (float)p_229039_11_, (float)p_229039_10_).endVertex();
    }
}
