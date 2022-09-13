package noppes.npcs.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import noppes.npcs.entity.EntityNpcDragon;

public class ModelNpcDragon<T extends Entity> extends EntityModel<T>
{

    private final ModelRenderer head;
    private final ModelRenderer neck;
    private final ModelRenderer jaw;
    private final ModelRenderer body;
    private final ModelRenderer leftWing;
    private final ModelRenderer leftWingTip;
    private final ModelRenderer leftFrontLeg;
    private final ModelRenderer leftFrontLegTip;
    private final ModelRenderer leftFrontFoot;
    private final ModelRenderer leftRearLeg;
    private final ModelRenderer leftRearLegTip;
    private final ModelRenderer leftRearFoot;
    private final ModelRenderer rightWing;
    private final ModelRenderer rightWingTip;
    private final ModelRenderer rightFrontLeg;
    private final ModelRenderer rightFrontLegTip;
    private final ModelRenderer rightFrontFoot;
    private final ModelRenderer rightRearLeg;
    private final ModelRenderer rightRearLegTip;
    private final ModelRenderer rightRearFoot;
    private float field_40317_s;

    private EntityNpcDragon entitydragon;

    public ModelNpcDragon() {
        texWidth = 256;
        texHeight = 256;
        float f = -16.0F;
        this.head = new ModelRenderer(this);
        this.head.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 0.0F, 176, 44);
        this.head.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 0.0F, 112, 30);
        this.head.mirror = true;
        this.head.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
        this.head.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
        this.head.mirror = false;
        this.head.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
        this.head.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
        this.jaw = new ModelRenderer(this);
        this.jaw.setPos(0.0F, 4.0F, -8.0F);
        this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, 0.0F, 176, 65);
        this.head.addChild(this.jaw);
        this.neck = new ModelRenderer(this);
        this.neck.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 0.0F, 192, 104);
        this.neck.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 0.0F, 48, 0);
        this.body = new ModelRenderer(this);
        this.body.setPos(0.0F, 4.0F, 8.0F);
        this.body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, 0.0F, 0, 0);
        this.body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, 0.0F, 220, 53);
        this.body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, 0.0F, 220, 53);
        this.body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, 0.0F, 220, 53);
        this.leftWing = new ModelRenderer(this);
        this.leftWing.mirror = true;
        this.leftWing.setPos(12.0F, 5.0F, 2.0F);
        this.leftWing.addBox("bone", 0.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
        this.leftWing.addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
        this.leftWingTip = new ModelRenderer(this);
        this.leftWingTip.mirror = true;
        this.leftWingTip.setPos(56.0F, 0.0F, 0.0F);
        this.leftWingTip.addBox("bone", 0.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
        this.leftWingTip.addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
        this.leftWing.addChild(this.leftWingTip);
        this.leftFrontLeg = new ModelRenderer(this);
        this.leftFrontLeg.setPos(12.0F, 20.0F, 2.0F);
        this.leftFrontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
        this.leftFrontLegTip = new ModelRenderer(this);
        this.leftFrontLegTip.setPos(0.0F, 20.0F, -1.0F);
        this.leftFrontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
        this.leftFrontLeg.addChild(this.leftFrontLegTip);
        this.leftFrontFoot = new ModelRenderer(this);
        this.leftFrontFoot.setPos(0.0F, 23.0F, 0.0F);
        this.leftFrontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
        this.leftFrontLegTip.addChild(this.leftFrontFoot);
        this.leftRearLeg = new ModelRenderer(this);
        this.leftRearLeg.setPos(16.0F, 16.0F, 42.0F);
        this.leftRearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
        this.leftRearLegTip = new ModelRenderer(this);
        this.leftRearLegTip.setPos(0.0F, 32.0F, -4.0F);
        this.leftRearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
        this.leftRearLeg.addChild(this.leftRearLegTip);
        this.leftRearFoot = new ModelRenderer(this);
        this.leftRearFoot.setPos(0.0F, 31.0F, 4.0F);
        this.leftRearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
        this.leftRearLegTip.addChild(this.leftRearFoot);
        this.rightWing = new ModelRenderer(this);
        this.rightWing.setPos(-12.0F, 5.0F, 2.0F);
        this.rightWing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
        this.rightWing.addBox("skin", -56F, 0.0F, 2.0F, 56, 0, 56, 0.0f, -56, 88);
        this.rightWingTip = new ModelRenderer(this);
        this.rightWingTip.setPos(-56.0F, 0.0F, 0.0F);
        this.rightWingTip.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
        this.rightWingTip.addBox("skin", -56F, 0.0F, 2.0F, 56, 0, 56, 0.0f, -56, 144);
        this.rightWing.addChild(this.rightWingTip);
        this.rightFrontLeg = new ModelRenderer(this);
        this.rightFrontLeg.setPos(-12.0F, 20.0F, 2.0F);
        this.rightFrontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
        this.rightFrontLegTip = new ModelRenderer(this);
        this.rightFrontLegTip.setPos(0.0F, 20.0F, -1.0F);
        this.rightFrontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
        this.rightFrontLeg.addChild(this.rightFrontLegTip);
        this.rightFrontFoot = new ModelRenderer(this);
        this.rightFrontFoot.setPos(0.0F, 23.0F, 0.0F);
        this.rightFrontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
        this.rightFrontLegTip.addChild(this.rightFrontFoot);
        this.rightRearLeg = new ModelRenderer(this);
        this.rightRearLeg.setPos(-16.0F, 16.0F, 42.0F);
        this.rightRearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
        this.rightRearLegTip = new ModelRenderer(this);
        this.rightRearLegTip.setPos(0.0F, 32.0F, -4.0F);
        this.rightRearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
        this.rightRearLeg.addChild(this.rightRearLegTip);
        this.rightRearFoot = new ModelRenderer(this);
        this.rightRearFoot.setPos(0.0F, 31.0F, 4.0F);
        this.rightRearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
        this.rightRearLegTip.addChild(this.rightRearFoot);
    }

    @Override
    public void setupAnim(Entity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {

    }

    private float animationPos, animationSpeed;

    @Override
    public void prepareMobModel(Entity entityliving, float animationPos, float animationSpeed, float f2) {
        field_40317_s = f2;
        entitydragon = (EntityNpcDragon) entityliving;
        this.animationPos = animationPos;
        this.animationSpeed = animationSpeed;
    }
    
    @Override
    public void renderToBuffer(MatrixStack mStack, IVertexBuilder iVertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
        mStack.pushPose();
        float f6 = entitydragon.prevAnimTime + (entitydragon.animTime - entitydragon.prevAnimTime) * field_40317_s;
        jaw.xRot = (float)(Math.sin(f6 * (float)Math.PI * 2.0F) + 1.0D) * 0.2F;
        float f7 = (float)(Math.sin(f6 * (float)Math.PI * 2.0F - 1.0F) + 1.0D);
        f7 = (f7 * f7 * 1.0F + f7 * 2.0F) * 0.05F;
        mStack.translate(0.0F, f7 - 2.0F, -3F);
        mStack.mulPose(Vector3f.XP.rotationDegrees(f7 * 2.0F));
        float f8 = -30F;
        float f9 = 22F;
        float f10 = 0.0F;
        float f11 = 1.5F;
        double[] ad = entitydragon.getMovementOffsets(6, field_40317_s);
        float f12 = func_40307_a(entitydragon.getMovementOffsets(5, field_40317_s)[0] - entitydragon.getMovementOffsets(10, field_40317_s)[0]);
        float f13 = func_40307_a(entitydragon.getMovementOffsets(5, field_40317_s)[0] + (double)(f12 / 2.0F));
        f8 += 2.0F;
        float f14 = 0.0F;
        float f15 = f6 * 3.141593F * 2.0F;
        f8 = 20F;
        f9 = -12F;
        for(int i = 0; i < 5; i++)
        {
            double[] ad3 = entitydragon.getMovementOffsets(5 - i, field_40317_s);
            f14 = (float)Math.cos((float)i * 0.45F + f15) * 0.15F;
            neck.yRot = ((func_40307_a(ad3[0] - ad[0]) * (float)Math.PI) / 180F) * f11;
            neck.xRot = f14 + (((float)(ad3[1] - ad[1]) * (float)Math.PI) / 180F) * f11 * 5F;
            neck.zRot = ((-func_40307_a(ad3[0] - (double)f13) * (float)Math.PI) / 180F) * f11;
            neck.y = f8;
            neck.z = f9;
            neck.x = f10;
            f8 = (float)((double)f8 + Math.sin(neck.xRot) * 10D);
            f9 = (float)((double)f9 - Math.cos(neck.yRot) * Math.cos(neck.xRot) * 10D);
            f10 = (float)((double)f10 - Math.sin(neck.yRot) * Math.cos(neck.xRot) * 10D);
            neck.render(mStack, iVertex, lightmapUV, packedOverlayIn);
        }

        head.y = f8;
        head.z = f9;
        head.x = f10;
        double[] ad1 = entitydragon.getMovementOffsets(0, field_40317_s);
        head.yRot = ((func_40307_a(ad1[0] - ad[0]) * (float)Math.PI) / 180F) * 1.0F;
        head.zRot = ((-func_40307_a(ad1[0] - (double)f13) * (float)Math.PI) / 180F) * 1.0F;
        head.render(mStack, iVertex, lightmapUV, packedOverlayIn);
        mStack.pushPose();
        mStack.translate(0.0F, 1.0F, 0.0F);
        if(entitydragon.isOnGround())
            mStack.mulPose(Vector3f.ZP.rotationDegrees(-f12 * f11 * 0.3F));
        else
            mStack.mulPose(Vector3f.ZP.rotationDegrees(-f12 * f11 * 1.0F));

        mStack.translate(0.0F, -1.18F, 0.0F);
        body.zRot = 0.0F;
        body.render(mStack, iVertex, lightmapUV, packedOverlayIn);
        if(entitydragon.isOnGround()){
            //RenderSystem.enableCull();
            leftWing.xRot = 0.25f;
            leftWing.yRot = -0.95F;
            leftWing.zRot = 0.5f;

            this.rightWing.xRot = this.leftWing.xRot;
            this.rightWing.yRot = -this.leftWing.yRot;
            this.rightWing.zRot = -this.leftWing.zRot;

//            if(animationSpeed > 0 && entitydragon.rider == null)
//                animationSpeed = 0;

            leftWingTip.zRot = 0.4f;
            rightWingTip.zRot = -0.4f;
            leftFrontLeg.xRot = rightFrontLeg.xRot = MathHelper.cos((float) (animationPos * 0.6662F + (Math.PI))) * 0.6F * animationSpeed + 0.45f + f7 * 0.5f ;
            leftRearLeg.xRot = rightRearLeg.xRot = MathHelper.cos(animationPos * 0.6662F + (0)) * 0.6F * animationSpeed + 0.75f + f7 * 0.5f;
            leftFrontLegTip.xRot = rightFrontLegTip.xRot = -1.3f - f7 * 1.2f ;
            leftFrontFoot.xRot = rightFrontFoot.xRot = 0.85f + f7 * 0.5f;

            leftRearLegTip.xRot = rightRearLegTip.xRot = -1.6f - f7 * 0.8f ;
            leftRearLegTip.y = rightRearLegTip.y = 20;
            leftRearLegTip.z = rightRearLegTip.z = 2;
            leftRearFoot.xRot = rightRearFoot.xRot = 0.85f + f7 * 0.2f;

            leftFrontLeg.render(mStack, iVertex, lightmapUV, packedOverlayIn);
            rightFrontLeg.render(mStack, iVertex, lightmapUV, packedOverlayIn);
            leftRearLeg.render(mStack, iVertex, lightmapUV, packedOverlayIn);
            rightRearLeg.render(mStack, iVertex, lightmapUV, packedOverlayIn);
            leftWing.render(mStack, iVertex, lightmapUV, packedOverlayIn);
            rightWing.render(mStack, iVertex, lightmapUV, packedOverlayIn);
            //RenderSystem.scalef(-1F, 1.0F, 1.0F);

        }
        else{
            //RenderSystem.enableCull();
            float f16 = f6 * (float)Math.PI * 2.0F;
            leftWing.xRot = 0.125F - (float)Math.cos(f16) * 0.2F;
            leftWing.yRot = -0.25F;
            leftWing.zRot = -(float)(Math.sin(f16) + 0.125D) * 0.8F;
            this.rightWing.xRot = this.leftWing.xRot;
            this.rightWing.yRot = -this.leftWing.yRot;
            this.rightWing.zRot = -this.leftWing.zRot;

            leftWingTip.zRot = (float)(Math.sin(f16 + 2.0F) + 0.5D) * 0.75F;
            rightWingTip.zRot = -leftWingTip.zRot;

            leftRearLegTip.y = rightRearLegTip.y = 32;
            leftRearLegTip.z = rightRearLegTip.z = -2;
            leftRearLeg.xRot = rightRearLeg.xRot = 1.0F + f7 * 0.1F;

            leftRearLegTip.xRot = rightRearLegTip.xRot = 0.5F + f7 * 0.1F;
            leftRearFoot.xRot = rightRearFoot.xRot = 0.75F + f7 * 0.1F;
            leftFrontLeg.xRot = rightFrontLeg.xRot = 1.3F + f7 * 0.1F;
            leftFrontLegTip.xRot = rightFrontLegTip.xRot = -0.5F - f7 * 0.1F;
            leftFrontFoot.xRot = rightFrontFoot.xRot = 0.75F + f7 * 0.1F;

            leftWing.render(mStack, iVertex, lightmapUV, packedOverlayIn);
            rightWing.render(mStack, iVertex, lightmapUV, packedOverlayIn);
            leftFrontLeg.render(mStack, iVertex, lightmapUV, packedOverlayIn);
            rightFrontLeg.render(mStack, iVertex, lightmapUV, packedOverlayIn);
            leftRearLeg.render(mStack, iVertex, lightmapUV, packedOverlayIn);
            rightRearLeg.render(mStack, iVertex, lightmapUV, packedOverlayIn);
            //RenderSystem.scalef(-1F, 1.0F, 1.0F);
        }
        mStack.popPose();
        //RenderSystem.disableCull();
        f14 = -(float)Math.sin(f6 * 3.141593F * 2.0F) * 0.0F;
        f15 = f6 * (float)Math.PI * 2.0F;
        f8 = 10F;
        f9 = 60F;
        f10 = 0.0F;
        ad = entitydragon.getMovementOffsets(11, field_40317_s);
        for(int k = 0; k < 12; k++)
        {
            double[] ad2 = entitydragon.getMovementOffsets(12 + k, field_40317_s);
            f14 = (float)((double)f14 + Math.sin((float)k * 0.45F + f15) * 0.05000000074505806D);
            neck.yRot = ((func_40307_a(ad2[0] - ad[0]) * f11 + 180F) * (float)Math.PI) / 180F;
            neck.xRot = f14 + (((float)(ad2[1] - ad[1]) * (float)Math.PI) / 180F) * f11 * 5F;
            neck.zRot = ((func_40307_a(ad2[0] - (double)f13) * (float)Math.PI) / 180F) * f11;
            neck.y = f8;
            neck.z = f9;
            neck.x = f10;
            f8 = (float)((double)f8 + Math.sin(neck.xRot) * 10D);
            f9 = (float)((double)f9 - Math.cos(neck.yRot) * Math.cos(neck.xRot) * 10D);
            f10 = (float)((double)f10 - Math.sin(neck.yRot) * Math.cos(neck.xRot) * 10D);
            neck.render(mStack, iVertex, lightmapUV, packedOverlayIn);
        }

        mStack.popPose();
        //}
    }

    private float func_40307_a(double d)
    {
        for(; d >= 180D; d -= 360D) { }
        for(; d < -180D; d += 360D) { }
        return (float)d;
    }
}
