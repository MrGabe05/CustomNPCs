package noppes.npcs.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.entity.EntityNPCInterface;

public class ModelPonyArmor extends EntityModel{

    private boolean rainboom;
    public ModelRenderer head;
    public ModelRenderer Body;
    public ModelRenderer BodyBack;
    public ModelRenderer rightarm;
    public ModelRenderer LeftArm;
    public ModelRenderer RightLeg;
    public ModelRenderer LeftLeg;
    public ModelRenderer rightarm2;
    public ModelRenderer LeftArm2;
    public ModelRenderer RightLeg2;
    public ModelRenderer LeftLeg2;
    public boolean isPegasus = false;
    public boolean isUnicorn = false;
    public boolean isSleeping = false;
    public boolean isFlying = false;
    public boolean isGlow = false;
    public boolean isSneak = false;
    public boolean aimedBow;
    public int heldItemRight;

    public ModelPonyArmor(float f)
    {
    	init(f,0.0f);
    }

    public void init(float strech,float f)
    {
        float f2 = 0.0F;
        float f3 = 0.0F;
        float f4 = 0.0F;
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-4F, -4F, -6F, 8, 8, 8, strech);
        head.setPos(f2, f3, f4);
        float f5 = 0.0F;
        float f6 = 0.0F;
        float f7 = 0.0F;
        Body = new ModelRenderer(this, 16, 16);
        Body.addBox(-4F, 4F, -2F, 8, 8, 4, strech);
        Body.setPos(f5, f6 + f, f7);
        BodyBack = new ModelRenderer(this, 0, 0);
        BodyBack.addBox(-4F, 4F, 6F, 8, 8, 8, strech);
        BodyBack.setPos(f5, f6 + f, f7);
        rightarm = new ModelRenderer(this, 0, 16);
        rightarm.addBox(-2F, 4F, -2F, 4, 12, 4, strech);
        rightarm.setPos(-3F, 8F + f, 0.0F);
        LeftArm = new ModelRenderer(this, 0, 16);
        LeftArm.mirror = true;
        LeftArm.addBox(-2F, 4F, -2F, 4, 12, 4, strech);
        LeftArm.setPos(3F, 8F + f, 0.0F);
        RightLeg = new ModelRenderer(this, 0, 16);
        RightLeg.addBox(-2F, 4F, -2F, 4, 12, 4, strech);
        RightLeg.setPos(-3F, 0.0F + f, 0.0F);
        LeftLeg = new ModelRenderer(this, 0, 16);
        LeftLeg.mirror = true;
        LeftLeg.addBox(-2F, 4F, -2F, 4, 12, 4, strech);
        LeftLeg.setPos(3F, 0.0F + f, 0.0F);
        rightarm2 = new ModelRenderer(this, 0, 16);
        rightarm2.addBox(-2F, 4F, -2F, 4, 12, 4, strech * 0.5F);
        rightarm2.setPos(-3F, 8F + f, 0.0F);
        LeftArm2 = new ModelRenderer(this, 0, 16);
        LeftArm2.mirror = true;
        LeftArm2.addBox(-2F, 4F, -2F, 4, 12, 4, strech * 0.5F);
        LeftArm2.setPos(3F, 8F + f, 0.0F);
        RightLeg2 = new ModelRenderer(this, 0, 16);
        RightLeg2.addBox(-2F, 4F, -2F, 4, 12, 4, strech * 0.5F);
        RightLeg2.setPos(-3F, 0.0F + f, 0.0F);
        LeftLeg2 = new ModelRenderer(this, 0, 16);
        LeftLeg2.mirror = true;
        LeftLeg2.addBox(-2F, 4F, -2F, 4, 12, 4, strech * 0.5F);
        LeftLeg2.setPos(3F, 0.0F + f, 0.0F);
    }

    @Override
    public void setupAnim(Entity entity, float aniPosition, float aniSpeed, float age, float yHead, float xHead) {
    	EntityNPCInterface npc = (EntityNPCInterface) entity;
    	if(!riding)
    		riding = npc.currentAnimation == AnimationType.SIT;
    	
    	if(isSneak && (npc.currentAnimation == AnimationType.CRAWL || npc.currentAnimation == AnimationType.SLEEP))
    		isSneak = false;
        rainboom = false;
        float f6;
        float f7;
        if(isSleeping)
        {
            f6 = 1.4F;
            f7 = 0.1F;
        } else
        {
            f6 = yHead / 57.29578F;
            f7 = xHead / 57.29578F;
        }
        head.yRot = f6;
        head.xRot = f7;
        float f8;
        float f9;
        float f10;
        float f11;
        if(!isFlying || !isPegasus)
        {
            f8 = MathHelper.cos(aniPosition * 0.6662F + 3.141593F) * 0.6F * aniSpeed;
            f9 = MathHelper.cos(aniPosition * 0.6662F) * 0.6F * aniSpeed;
            f10 = MathHelper.cos(aniPosition * 0.6662F) * 0.3F * aniSpeed;
            f11 = MathHelper.cos(aniPosition * 0.6662F + 3.141593F) * 0.3F * aniSpeed;
            rightarm.yRot = 0.0F;
            LeftArm.yRot = 0.0F;
            RightLeg.yRot = 0.0F;
            LeftLeg.yRot = 0.0F;
            rightarm2.yRot = 0.0F;
            LeftArm2.yRot = 0.0F;
            RightLeg2.yRot = 0.0F;
            LeftLeg2.yRot = 0.0F;
        } else
        {
            if(aniSpeed < 0.9999F)
            {
                rainboom = false;
                f8 = MathHelper.sin(0.0F - aniSpeed * 0.5F);
                f9 = MathHelper.sin(0.0F - aniSpeed * 0.5F);
                f10 = MathHelper.sin(aniSpeed * 0.5F);
                f11 = MathHelper.sin(aniSpeed * 0.5F);
            } else
            {
                rainboom = true;
                f8 = 4.712F;
                f9 = 4.712F;
                f10 = 1.571F;
                f11 = 1.571F;
            }
            rightarm.yRot = 0.2F;
            LeftArm.yRot = -0.2F;
            RightLeg.yRot = -0.2F;
            LeftLeg.yRot = 0.2F;
            rightarm2.yRot = 0.2F;
            LeftArm2.yRot = -0.2F;
            RightLeg2.yRot = -0.2F;
            LeftLeg2.yRot = 0.2F;
        }
        if(isSleeping)
        {
            f8 = 4.712F;
            f9 = 4.712F;
            f10 = 1.571F;
            f11 = 1.571F;
        }
        rightarm.xRot = f8;
        LeftArm.xRot = f9;
        RightLeg.xRot = f10;
        LeftLeg.xRot = f11;
        rightarm.zRot = 0.0F;
        LeftArm.zRot = 0.0F;
        rightarm2.xRot = f8;
        LeftArm2.xRot = f9;
        RightLeg2.xRot = f10;
        LeftLeg2.xRot = f11;
        rightarm2.zRot = 0.0F;
        LeftArm2.zRot = 0.0F;
        if(heldItemRight != 0 && !rainboom && !isUnicorn)
        {
            rightarm.xRot = rightarm.xRot * 0.5F - 0.3141593F;
            rightarm2.xRot = rightarm2.xRot * 0.5F - 0.3141593F;
        }
        float f13 = MathHelper.sin(Body.yRot) * 5F;
        float f14 = MathHelper.cos(Body.yRot) * 5F;
        float f15 = 4F;
        if(isSneak && !isFlying)
        {
            f15 = 0.0F;
        }
        if(isSleeping)
        {
            f15 = 2.6F;
        }
        if(rainboom)
        {
            rightarm.z = f13 + 2.0F;
            rightarm2.z = f13 + 2.0F;
            LeftArm.z = (0.0F - f13) + 2.0F;
            LeftArm2.z = (0.0F - f13) + 2.0F;
        } else
        {
            rightarm.z = f13 + 1.0F;
            rightarm2.z = f13 + 1.0F;
            LeftArm.z = (0.0F - f13) + 1.0F;
            LeftArm2.z = (0.0F - f13) + 1.0F;
        }
        rightarm.x = (0.0F - f14 - 1.0F) + f15;
        rightarm2.x = (0.0F - f14 - 1.0F) + f15;
        LeftArm.x = (f14 + 1.0F) - f15;
        LeftArm2.x = (f14 + 1.0F) - f15;
        RightLeg.x = (0.0F - f14 - 1.0F) + f15;
        RightLeg2.x = (0.0F - f14 - 1.0F) + f15;
        LeftLeg.x = (f14 + 1.0F) - f15;
        LeftLeg2.x = (f14 + 1.0F) - f15;
        rightarm.yRot += Body.yRot;
        rightarm2.yRot += Body.yRot;
        LeftArm.yRot += Body.yRot;
        LeftArm2.yRot += Body.yRot;
        LeftArm.xRot += Body.yRot;
        LeftArm2.xRot += Body.yRot;
        rightarm.y = 8F;
        LeftArm.y = 8F;
        RightLeg.y = 4F;
        LeftLeg.y = 4F;
        rightarm2.y = 8F;
        LeftArm2.y = 8F;
        RightLeg2.y = 4F;
        LeftLeg2.y = 4F;

        if(isSneak && !isFlying)
        {
            float f17 = 0.4F;
            float f22 = 7F;
            float f27 = -4F;
            Body.xRot = f17;
            Body.y = f22;
            Body.z = f27;
            BodyBack.xRot = f17;
            BodyBack.y = f22;
            BodyBack.z = f27;
            RightLeg.xRot -= 0.0F;
            LeftLeg.xRot -= 0.0F;
            rightarm.xRot -= 0.4F;
            LeftArm.xRot -= 0.4F;
            RightLeg.z = 10F;
            LeftLeg.z = 10F;
            RightLeg.y = 7F;
            LeftLeg.y = 7F;
            RightLeg2.xRot -= 0.0F;
            LeftLeg2.xRot -= 0.0F;
            rightarm2.xRot -= 0.4F;
            LeftArm2.xRot -= 0.4F;
            RightLeg2.z = 10F;
            LeftLeg2.z = 10F;
            RightLeg2.y = 7F;
            LeftLeg2.y = 7F;
            float f31;
            float f33;
            float f35;
            if(isSleeping)
            {
                f31 = 2.0F;
                f33 = -1F;
                f35 = 1.0F;
            } else
            {
                f31 = 6F;
                f33 = -2F;
                f35 = 0.0F;
            }
            head.y = f31;
            head.z = f33;
            head.x = f35;
        } else
        {
            float f18 = 0.0F;
            float f23 = 0.0F;
            float f28 = 0.0F;
            Body.xRot = f18;
            Body.y = f23;
            Body.z = f28;
            BodyBack.xRot = f18;
            BodyBack.y = f23;
            BodyBack.z = f28;
            RightLeg.z = 10F;
            LeftLeg.z = 10F;
            RightLeg.y = 8F;
            LeftLeg.y = 8F;
            RightLeg2.z = 10F;
            LeftLeg2.z = 10F;
            RightLeg2.y = 8F;
            LeftLeg2.y = 8F;
            float f36 = 0.0F;
            float f37 = 0.0F;
            head.y = f36;
            head.z = f37;
        }
//        if(riding)
//        {
//            float f19 = -10F;
//            float f24 = -10F;
//            head.y = head.y + f19;
//            head.z = head.z + f24;
//            Body.y = Body.y + f19;
//            Body.z = Body.z + f24;
//            BodyBack.y = BodyBack.y + f19;
//            BodyBack.z = BodyBack.z + f24;
//            LeftArm.y = LeftArm.y + f19;
//            LeftArm.z = LeftArm.z + f24;
//            rightarm.y = rightarm.y + f19;
//            rightarm.z = rightarm.z + f24;
//            LeftLeg.y = LeftLeg.y + f19;
//            LeftLeg.z = LeftLeg.z + f24;
//            RightLeg.y = RightLeg.y + f19;
//            RightLeg.z = RightLeg.z + f24;
//            LeftArm2.y = LeftArm2.y + f19;
//            LeftArm2.z = LeftArm2.z + f24;
//            rightarm2.y = rightarm2.y + f19;
//            rightarm2.z = rightarm2.z + f24;
//            LeftLeg2.y = LeftLeg2.y + f19;
//            LeftLeg2.z = LeftLeg2.z + f24;
//            RightLeg2.y = RightLeg2.y + f19;
//            RightLeg2.z = RightLeg2.z + f24;
//        }
        if(isSleeping)
        {
            rightarm.z = rightarm.z + 6F;
            LeftArm.z = LeftArm.z + 6F;
            RightLeg.z = RightLeg.z - 8F;
            LeftLeg.z = LeftLeg.z - 8F;
            rightarm.y = rightarm.y + 2.0F;
            LeftArm.y = LeftArm.y + 2.0F;
            RightLeg.y = RightLeg.y + 2.0F;
            LeftLeg.y = LeftLeg.y + 2.0F;
            rightarm2.z = rightarm2.z + 6F;
            LeftArm2.z = LeftArm2.z + 6F;
            RightLeg2.z = RightLeg2.z - 8F;
            LeftLeg2.z = LeftLeg2.z - 8F;
            rightarm2.y = rightarm2.y + 2.0F;
            LeftArm2.y = LeftArm2.y + 2.0F;
            RightLeg2.y = RightLeg2.y + 2.0F;
            LeftLeg2.y = LeftLeg2.y + 2.0F;
        }
        if(aimedBow && !isUnicorn)
        {
            float f20 = 0.0F;
            float f25 = 0.0F;
            rightarm.zRot = 0.0F;
            rightarm.yRot = -(0.1F - f20 * 0.6F) + head.yRot;
            rightarm.xRot = 4.712F + head.xRot;
            rightarm.xRot -= f20 * 1.2F - f25 * 0.4F;
            float f29 = age;
            rightarm.zRot += MathHelper.cos(f29 * 0.09F) * 0.05F + 0.05F;
            rightarm.xRot += MathHelper.sin(f29 * 0.067F) * 0.05F;
            rightarm2.zRot = 0.0F;
            rightarm2.yRot = -(0.1F - f20 * 0.6F) + head.yRot;
            rightarm2.xRot = 4.712F + head.xRot;
            rightarm2.xRot -= f20 * 1.2F - f25 * 0.4F;
            rightarm2.zRot += MathHelper.cos(f29 * 0.09F) * 0.05F + 0.05F;
            rightarm2.xRot += MathHelper.sin(f29 * 0.067F) * 0.05F;
            rightarm.z = rightarm.z + 1.0F;
            rightarm2.z = rightarm2.z + 1.0F;
        }
    }

    @Override
    public void renderToBuffer(MatrixStack mStack, IVertexBuilder iVertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
        head.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        Body.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        BodyBack.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        LeftArm.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        rightarm.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        LeftLeg.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        RightLeg.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        LeftArm2.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        rightarm2.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        LeftLeg2.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        RightLeg2.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
    }
}
