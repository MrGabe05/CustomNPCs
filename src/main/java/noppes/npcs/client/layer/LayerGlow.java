package noppes.npcs.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

@OnlyIn(Dist.CLIENT)
public class LayerGlow<T extends EntityNPCInterface, M extends EntityModel<T>> extends LayerRenderer<T, M> {

    public LayerGlow(RenderCustomNpc npcRenderer) {
        super(npcRenderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer typeBuffer, int packedLightIn, T npc, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (npc.display.getOverlayTexture().isEmpty())
            return;
        if (npc.textureGlowLocation == null) {
            npc.textureGlowLocation = new ResourceLocation(npc.display.getOverlayTexture());
        }

        IVertexBuilder ivertexbuilder = typeBuffer.getBuffer(RenderType.entityCutoutNoCull(npc.textureGlowLocation));
        this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getOverlayCoords(npc, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);

//        this.renderer.bind(npc.textureGlowLocation);
//        RenderSystem.enableBlend();
//        //RenderSystem.disableAlphaTest();
//        RenderSystem.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
//        RenderSystem.disableLighting();
//        RenderSystem.depthFunc(GL11.GL_EQUAL);
//        char c0 = 61680;
//        int i = c0 % 65536;
//        int j = c0 / 65536;
//        OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, (float)i, (float)j);
//        RenderSystem.enableLighting();
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//        this.renderer.getModel().render(matrixStackIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
//        this.renderer.setLightmap(npc);
//        RenderSystem.disableBlend();
//        RenderSystem.enableAlphaTest();
//        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }
}