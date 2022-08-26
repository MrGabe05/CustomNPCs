package noppes.npcs.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.entity.LivingEntity;
import noppes.npcs.client.model.ModelNpcSlime;
import noppes.npcs.entity.EntityNpcSlime;

public class LayerSlimeNpc<T extends EntityNpcSlime> extends LayerRenderer<T, ModelNpcSlime<T>>{
    private final LivingRenderer renderer;
    private final EntityModel slimeModel = new ModelNpcSlime(0);

    public LayerSlimeNpc(LivingRenderer renderer){
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!living.isInvisible()) {
            this.getParentModel().copyPropertiesTo(this.slimeModel);
            this.slimeModel.prepareMobModel(living, limbSwing, limbSwingAmount, partialTicks);
            this.slimeModel.setupAnim(living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(living)));
            this.slimeModel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getOverlayCoords(living, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
