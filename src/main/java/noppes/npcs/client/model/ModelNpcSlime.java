package noppes.npcs.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.entity.EntityNpcSlime;

@OnlyIn(Dist.CLIENT)
public class ModelNpcSlime<T extends EntityNpcSlime> extends EntityModel<T>
{
    ModelRenderer outerBody;
    ModelRenderer innerBody;

    /** The slime's right eye */
    ModelRenderer slimeRightEye;

    /** The slime's left eye */
    ModelRenderer slimeLeftEye;

    /** The slime's mouth */
    ModelRenderer slimeMouth;

    public ModelNpcSlime(int par1)
    {
    	this.texHeight = 64;
    	this.texWidth = 64;
        outerBody = new ModelRenderer(this, 0, 0);
        this.outerBody = new ModelRenderer(this, 0, 0);
        this.outerBody.addBox(-8.0F, 32.0F, -8.0F, 16, 16, 16);
        //this.outerBody.addBox(-8.0F, 32.0F, -8.0F, 16, 16, 16);

        if (par1 > 0)
        {
            this.innerBody = new ModelRenderer(this, 0, 32);
            this.innerBody.addBox(-3.0F, 17.0F, -3.0F, 6, 6, 6);
            
            this.slimeRightEye = new ModelRenderer(this, 0, 0);
            this.slimeRightEye.addBox(-3.25F, 18.0F, -3.5F, 2, 2, 2);
            this.slimeLeftEye = new ModelRenderer(this, 0, 4);
            this.slimeLeftEye.addBox(1.25F, 18.0F, -3.5F, 2, 2, 2);
            this.slimeMouth = new ModelRenderer(this, 0, 8);
            this.slimeMouth.addBox(0.0F, 21.0F, -3.5F, 1, 1, 1);
        }
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(MatrixStack mStack, IVertexBuilder iVertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if(innerBody != null){
            this.innerBody.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        }
        else{
            mStack.pushPose();
            mStack.scale(0.5f, 0.5f, 0.5f);
            this.outerBody.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
            mStack.popPose();
        }

        if (this.slimeRightEye != null)
        {
            this.slimeRightEye.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
            this.slimeLeftEye.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
            this.slimeMouth.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        }
    }
}
