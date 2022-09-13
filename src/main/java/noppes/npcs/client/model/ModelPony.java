package noppes.npcs.client.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelPlaneNpcsRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resources.IResource;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityNpcPony;

public class ModelPony<T extends EntityNpcPony> extends EntityModel<T>
{

    private boolean rainboom;
    private float WingRotateAngleX;
    private float WingRotateAngleY;
    private float WingRotateAngleZ;
    private float TailRotateAngleY;
    public ModelRenderer Head;
    public ModelRenderer[] Headpiece;
    public ModelRenderer Helmet;
    public ModelRenderer Body;
    public ModelPlaneNpcsRenderer[] Bodypiece;
    public ModelRenderer RightArm;
    public ModelRenderer LeftArm;
    public ModelRenderer RightLeg;
    public ModelRenderer LeftLeg;
    public ModelRenderer unicornarm;
    public ModelPlaneNpcsRenderer[] Tail;
    public ModelRenderer[] LeftWing;
    public ModelRenderer[] RightWing;
    public ModelRenderer[] LeftWingExt;
    public ModelRenderer[] RightWingExt;
    public boolean isPegasus;
    public boolean isUnicorn;
    public boolean isFlying;
    public boolean isGlow;
    public boolean isSleeping;
    public boolean isSneak;
    public boolean aimedBow;
    public int heldItemRight;

    public ModelPony()
    {
        init(0,0.0f);
    }

    public void init(float strech,float f)
    {
        float f2 = 0.0F;
        float f3 = 0.0F;
        float f4 = 0.0F;
        Head = new ModelRenderer(this, 0, 0);
        Head.addBox(-4F, -4F, -6F, 8, 8, 8, strech);
        Head.setPos(f2, f3 + f, f4);
        Headpiece = new ModelRenderer[3];
        Headpiece[0] = new ModelRenderer(this, 12, 16);
        Headpiece[0].addBox(-4F, -6F, -1F, 2, 2, 2, strech);
        Headpiece[0].setPos(f2, f3 + f, f4);
        Headpiece[1] = new ModelRenderer(this, 12, 16);
        Headpiece[1].addBox(2.0F, -6F, -1F, 2, 2, 2, strech);
        Headpiece[1].setPos(f2, f3 + f, f4);
        Headpiece[2] = new ModelRenderer(this, 56, 0);
        Headpiece[2].addBox(-0.5F, -10F, -4F, 1, 4, 1, strech);
        Headpiece[2].setPos(f2, f3 + f, f4);
        Helmet = new ModelRenderer(this, 32, 0);
        Helmet.addBox(-4F, -4F, -6F, 8, 8, 8, strech + 0.5F);
        Helmet.setPos(f2, f3, f4);
        float f5 = 0.0F;
        float f6 = 0.0F;
        float f7 = 0.0F;
        Body = new ModelRenderer(this, 16, 16);
        Body.addBox(-4F, 4F, -2F, 8, 8, 4, strech);
        Body.setPos(f5, f6 + f, f7);
        Bodypiece = new ModelPlaneNpcsRenderer[13];
        Bodypiece[0] = new ModelPlaneNpcsRenderer(this, 24, 0);
        Bodypiece[0].addSidePlane(-4F, 4F, 2.0F, 8, 8, strech);
        Bodypiece[0].setPos(f5, f6 + f, f7);
        Bodypiece[1] = new ModelPlaneNpcsRenderer(this, 24, 0);
        Bodypiece[1].addSidePlane(4F, 4F, 2.0F, 8, 8, strech);
        Bodypiece[1].setPos(f5, f6 + f, f7);
        Bodypiece[2] = new ModelPlaneNpcsRenderer(this, 24, 0);
        Bodypiece[2].addTopPlane(-4F, 4F, 2.0F, 8, 8, strech);
        Bodypiece[2].setPos(f2, f3 + f, f4);
        Bodypiece[3] = new ModelPlaneNpcsRenderer(this, 24, 0);
        Bodypiece[3].addTopPlane(-4F, 12F, 2.0F, 8, 8, strech);
        Bodypiece[3].setPos(f2, f3 + f, f4);
        Bodypiece[4] = new ModelPlaneNpcsRenderer(this, 0, 20);
        Bodypiece[4].addSidePlane(-4F, 4F, 10F, 8, 4, strech);
        Bodypiece[4].setPos(f5, f6 + f, f7);
        Bodypiece[5] = new ModelPlaneNpcsRenderer(this, 0, 20);
        Bodypiece[5].addSidePlane(4F, 4F, 10F, 8, 4, strech);
        Bodypiece[5].setPos(f5, f6 + f, f7);
        Bodypiece[6] = new ModelPlaneNpcsRenderer(this, 24, 0);
        Bodypiece[6].addTopPlane(-4F, 4F, 10F, 8, 4, strech);
        Bodypiece[6].setPos(f2, f3 + f, f4);
        Bodypiece[7] = new ModelPlaneNpcsRenderer(this, 24, 0);
        Bodypiece[7].addTopPlane(-4F, 12F, 10F, 8, 4, strech);
        Bodypiece[7].setPos(f2, f3 + f, f4);
        Bodypiece[8] = new ModelPlaneNpcsRenderer(this, 24, 0);
        Bodypiece[8].addBackPlane(-4F, 4F, 14F, 8, 8, strech);
        Bodypiece[8].setPos(f2, f3 + f, f4);
        Bodypiece[9] = new ModelPlaneNpcsRenderer(this, 32, 0);
        Bodypiece[9].addTopPlane(-1F, 10F, 8F, 2, 6, strech);
        Bodypiece[9].setPos(f2, f3 + f, f4);
        Bodypiece[10] = new ModelPlaneNpcsRenderer(this, 32, 0);
        Bodypiece[10].addTopPlane(-1F, 12F, 8F, 2, 6, strech);
        Bodypiece[10].setPos(f2, f3 + f, f4);
        Bodypiece[11] = new ModelPlaneNpcsRenderer(this, 32, 0);
        Bodypiece[11].mirror = true;
        Bodypiece[11].addSidePlane(-1F, 10F, 8F, 2, 6, strech);
        Bodypiece[11].setPos(f2, f3 + f, f4);
        Bodypiece[12] = new ModelPlaneNpcsRenderer(this, 32, 0);
        Bodypiece[12].addSidePlane(1.0F, 10F, 8F, 2, 6, strech);
        Bodypiece[12].setPos(f2, f3 + f, f4);
        RightArm = new ModelRenderer(this, 40, 16);
        RightArm.addBox(-2F, 4F, -2F, 4, 12, 4, strech);
        RightArm.setPos(-3F, 8F + f, 0.0F);
        LeftArm = new ModelRenderer(this, 40, 16);
        LeftArm.mirror = true;
        LeftArm.addBox(-2F, 4F, -2F, 4, 12, 4, strech);
        LeftArm.setPos(3F, 8F + f, 0.0F);
        RightLeg = new ModelRenderer(this, 40, 16);
        RightLeg.addBox(-2F, 4F, -2F, 4, 12, 4, strech);
        RightLeg.setPos(-3F, 0.0F + f, 0.0F);
        LeftLeg = new ModelRenderer(this, 40, 16);
        LeftLeg.mirror = true;
        LeftLeg.addBox(-2F, 4F, -2F, 4, 12, 4, strech);
        LeftLeg.setPos(3F, 0.0F + f, 0.0F);
        unicornarm = new ModelRenderer(this, 40, 16);
        unicornarm.addBox(-3F, -2F, -2F, 4, 12, 4, strech);
        unicornarm.setPos(-5F, 2.0F + f, 0.0F);
        float f8 = 0.0F;
        float f9 = 8F;
        float f10 = -14F;
        float f11 = 0.0F - f8;
        float f12 = 10F - f9;
        float f13 = 0.0F;
        Tail = new ModelPlaneNpcsRenderer[10];
        Tail[0] = new ModelPlaneNpcsRenderer(this, 32, 0);
        Tail[0].addTopPlane(-2F + f8, -7F + f9, 16F + f10, 4, 4, strech);
        Tail[0].setPos(f11, f12 + f, f13);
        Tail[1] = new ModelPlaneNpcsRenderer(this, 32, 0);
        Tail[1].addTopPlane(-2F + f8, 9F + f9, 16F + f10, 4, 4, strech);
        Tail[1].setPos(f11, f12 + f, f13);
        Tail[2] = new ModelPlaneNpcsRenderer(this, 32, 0);
        Tail[2].addBackPlane(-2F + f8, -7F + f9, 16F + f10, 4, 8, strech);
        Tail[2].setPos(f11, f12 + f, f13);
        Tail[3] = new ModelPlaneNpcsRenderer(this, 32, 0);
        Tail[3].addBackPlane(-2F + f8, -7F + f9, 20F + f10, 4, 8, strech);
        Tail[3].setPos(f11, f12 + f, f13);
        Tail[4] = new ModelPlaneNpcsRenderer(this, 32, 0);
        Tail[4].addBackPlane(-2F + f8, 1.0F + f9, 16F + f10, 4, 8, strech);
        Tail[4].setPos(f11, f12 + f, f13);
        Tail[5] = new ModelPlaneNpcsRenderer(this, 32, 0);
        Tail[5].addBackPlane(-2F + f8, 1.0F + f9, 20F + f10, 4, 8, strech);
        Tail[5].setPos(f11, f12 + f, f13);
        Tail[6] = new ModelPlaneNpcsRenderer(this, 36, 0);
        Tail[6].mirror = true;
        Tail[6].addSidePlane(2.0F + f8, -7F + f9, 16F + f10, 8, 4, strech);
        Tail[6].setPos(f11, f12 + f, f13);
        Tail[7] = new ModelPlaneNpcsRenderer(this, 36, 0);
        Tail[7].addSidePlane(-2F + f8, -7F + f9, 16F + f10, 8, 4, strech);
        Tail[7].setPos(f11, f12 + f, f13);
        Tail[8] = new ModelPlaneNpcsRenderer(this, 36, 0);
        Tail[8].mirror = true;
        Tail[8].addSidePlane(2.0F + f8, 1.0F + f9, 16F + f10, 8, 4, strech);
        Tail[8].setPos(f11, f12 + f, f13);
        Tail[9] = new ModelPlaneNpcsRenderer(this, 36, 0);
        Tail[9].addSidePlane(-2F + f8, 1.0F + f9, 16F + f10, 8, 4, strech);
        Tail[9].setPos(f11, f12 + f, f13);
        TailRotateAngleY = Tail[0].yRot;
        TailRotateAngleY = Tail[0].yRot;
        float f14 = 0.0F;
        float f15 = 0.0F;
        float f16 = 0.0F;
        LeftWing = new ModelRenderer[3];
        LeftWing[0] = new ModelRenderer(this, 56, 16);
        LeftWing[0].mirror = true;
        LeftWing[0].addBox(4F, 5F, 2.0F, 2, 6, 2, strech);
        LeftWing[0].setPos(f14, f15 + f, f16);
        LeftWing[1] = new ModelRenderer(this, 56, 16);
        LeftWing[1].mirror = true;
        LeftWing[1].addBox(4F, 5F, 4F, 2, 8, 2, strech);
        LeftWing[1].setPos(f14, f15 + f, f16);
        LeftWing[2] = new ModelRenderer(this, 56, 16);
        LeftWing[2].mirror = true;
        LeftWing[2].addBox(4F, 5F, 6F, 2, 6, 2, strech);
        LeftWing[2].setPos(f14, f15 + f, f16);
        RightWing = new ModelRenderer[3];
        RightWing[0] = new ModelRenderer(this, 56, 16);
        RightWing[0].addBox(-6F, 5F, 2.0F, 2, 6, 2, strech);
        RightWing[0].setPos(f14, f15 + f, f16);
        RightWing[1] = new ModelRenderer(this, 56, 16);
        RightWing[1].addBox(-6F, 5F, 4F, 2, 8, 2, strech);
        RightWing[1].setPos(f14, f15 + f, f16);
        RightWing[2] = new ModelRenderer(this, 56, 16);
        RightWing[2].addBox(-6F, 5F, 6F, 2, 6, 2, strech);
        RightWing[2].setPos(f14, f15 + f, f16);
        float f17 = f2 + 4.5F;
        float f18 = f3 + 5F;
        float f19 = f4 + 6F;
        LeftWingExt = new ModelRenderer[7];
        LeftWingExt[0] = new ModelRenderer(this, 56, 19);
        LeftWingExt[0].mirror = true;
        LeftWingExt[0].addBox(0.0F, 0.0F, 0.0F, 1, 8, 2, strech + 0.1F);
        LeftWingExt[0].setPos(f17, f18 + f, f19);
        LeftWingExt[1] = new ModelRenderer(this, 56, 19);
        LeftWingExt[1].mirror = true;
        LeftWingExt[1].addBox(0.0F, 8F, 0.0F, 1, 6, 2, strech + 0.1F);
        LeftWingExt[1].setPos(f17, f18 + f, f19);
        LeftWingExt[2] = new ModelRenderer(this, 56, 19);
        LeftWingExt[2].mirror = true;
        LeftWingExt[2].addBox(0.0F, -1.2F, -0.2F, 1, 8, 2, strech - 0.2F);
        LeftWingExt[2].setPos(f17, f18 + f, f19);
        LeftWingExt[3] = new ModelRenderer(this, 56, 19);
        LeftWingExt[3].mirror = true;
        LeftWingExt[3].addBox(0.0F, 1.8F, 1.3F, 1, 8, 2, strech - 0.1F);
        LeftWingExt[3].setPos(f17, f18 + f, f19);
        LeftWingExt[4] = new ModelRenderer(this, 56, 19);
        LeftWingExt[4].mirror = true;
        LeftWingExt[4].addBox(0.0F, 5F, 2.0F, 1, 8, 2, strech);
        LeftWingExt[4].setPos(f17, f18 + f, f19);
        LeftWingExt[5] = new ModelRenderer(this, 56, 19);
        LeftWingExt[5].mirror = true;
        LeftWingExt[5].addBox(0.0F, 0.0F, -0.2F, 1, 6, 2, strech + 0.3F);
        LeftWingExt[5].setPos(f17, f18 + f, f19);
        LeftWingExt[6] = new ModelRenderer(this, 56, 19);
        LeftWingExt[6].mirror = true;
        LeftWingExt[6].addBox(0.0F, 0.0F, 0.2F, 1, 3, 2, strech + 0.2F);
        LeftWingExt[6].setPos(f17, f18 + f, f19);
        float f20 = f2 - 4.5F;
        float f21 = f3 + 5F;
        float f22 = f4 + 6F;
        RightWingExt = new ModelRenderer[7];
        RightWingExt[0] = new ModelRenderer(this, 56, 19);
        RightWingExt[0].mirror = true;
        RightWingExt[0].addBox(0.0F, 0.0F, 0.0F, 1, 8, 2, strech + 0.1F);
        RightWingExt[0].setPos(f20, f21 + f, f22);
        RightWingExt[1] = new ModelRenderer(this, 56, 19);
        RightWingExt[1].mirror = true;
        RightWingExt[1].addBox(0.0F, 8F, 0.0F, 1, 6, 2, strech + 0.1F);
        RightWingExt[1].setPos(f20, f21 + f, f22);
        RightWingExt[2] = new ModelRenderer(this, 56, 19);
        RightWingExt[2].mirror = true;
        RightWingExt[2].addBox(0.0F, -1.2F, -0.2F, 1, 8, 2, strech - 0.2F);
        RightWingExt[2].setPos(f20, f21 + f, f22);
        RightWingExt[3] = new ModelRenderer(this, 56, 19);
        RightWingExt[3].mirror = true;
        RightWingExt[3].addBox(0.0F, 1.8F, 1.3F, 1, 8, 2, strech - 0.1F);
        RightWingExt[3].setPos(f20, f21 + f, f22);
        RightWingExt[4] = new ModelRenderer(this, 56, 19);
        RightWingExt[4].mirror = true;
        RightWingExt[4].addBox(0.0F, 5F, 2.0F, 1, 8, 2, strech);
        RightWingExt[4].setPos(f20, f21 + f, f22);
        RightWingExt[5] = new ModelRenderer(this, 56, 19);
        RightWingExt[5].mirror = true;
        RightWingExt[5].addBox(0.0F, 0.0F, -0.2F, 1, 6, 2, strech + 0.3F);
        RightWingExt[5].setPos(f20, f21 + f, f22);
        RightWingExt[6] = new ModelRenderer(this, 56, 19);
        RightWingExt[6].mirror = true;
        RightWingExt[6].addBox(0.0F, 0.0F, 0.2F, 1, 3, 2, strech + 0.2F);
        RightWingExt[6].setPos(f20, f21 + f, f22);
        WingRotateAngleX = LeftWingExt[0].xRot;
        WingRotateAngleY = LeftWingExt[0].yRot;
        WingRotateAngleZ = LeftWingExt[0].zRot;
    }

    @Override
    public void setupAnim(T npc, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if(npc.textureLocation != npc.checked && npc.textureLocation != null){
            try (IResource resource = Minecraft.getInstance().getResourceManager().getResource(npc.textureLocation)) {
                BufferedImage bufferedimage = ImageIO.read(resource.getInputStream());

                npc.isPegasus = false;
                npc.isUnicorn = false;
                Color color = new Color(bufferedimage.getRGB(0, 0), true);
                Color color1 = new Color(249, 177, 49, 255);
                Color color2 = new Color(136, 202, 240, 255);
                Color color3 = new Color(209, 159, 228, 255);
                Color color4 = new Color(254, 249, 252, 255);
                if(color.equals(color1))
                {
                }
                if(color.equals(color2))
                {
                    npc.isPegasus = true;
                }
                if(color.equals(color3))
                {
                    npc.isUnicorn = true;
                }
                if(color.equals(color4))
                {
                    npc.isPegasus = true;
                    npc.isUnicorn = true;
                }
                npc.checked = npc.textureLocation;

            } catch (IOException e) {

            }
        }
        isSleeping = npc.isSleeping();
        isUnicorn = npc.isUnicorn;
        isPegasus = npc.isPegasus;
        isSneak = npc.isCrouching();
        heldItemRight = npc.getMainHandItem() == null ? 0 : 1;
        riding = npc.isPassenger();

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
            f6 = headPitch / 57.29578F;
            f7 = netHeadYaw / 57.29578F;
        }
        Head.yRot = f6;
        Head.xRot = f7;
        Headpiece[0].yRot = f6;
        Headpiece[0].xRot = f7;
        Headpiece[1].yRot = f6;
        Headpiece[1].xRot = f7;
        Headpiece[2].yRot = f6;
        Headpiece[2].xRot = f7;
        Helmet.yRot = f6;
        Helmet.xRot = f7;
        Headpiece[2].xRot = f7 + 0.5F;
        float f8;
        float f9;
        float f10;
        float f11;
        if(!isFlying || !isPegasus)
        {
            f8 = MathHelper.cos(limbSwing * 0.6662F + 3.141593F) * 0.6F * limbSwingAmount;
            f9 = MathHelper.cos(limbSwing * 0.6662F) * 0.6F * limbSwingAmount;
            f10 = MathHelper.cos(limbSwing * 0.6662F) * 0.3F * limbSwingAmount;
            f11 = MathHelper.cos(limbSwing * 0.6662F + 3.141593F) * 0.3F * limbSwingAmount;
            RightArm.yRot = 0.0F;
            unicornarm.yRot = 0.0F;
            LeftArm.yRot = 0.0F;
            RightLeg.yRot = 0.0F;
            LeftLeg.yRot = 0.0F;
        } else
        {
            if(limbSwingAmount < 0.9999F)
            {
                rainboom = false;
                f8 = MathHelper.sin(0.0F - limbSwingAmount * 0.5F);
                f9 = MathHelper.sin(0.0F - limbSwingAmount * 0.5F);
                f10 = MathHelper.sin(limbSwingAmount * 0.5F);
                f11 = MathHelper.sin(limbSwingAmount * 0.5F);
            } else
            {
                rainboom = true;
                f8 = 4.712F;
                f9 = 4.712F;
                f10 = 1.571F;
                f11 = 1.571F;
            }
            RightArm.yRot = 0.2F;
            LeftArm.yRot = -0.2F;
            RightLeg.yRot = -0.2F;
            LeftLeg.yRot = 0.2F;
        }
        if(isSleeping)
        {
            f8 = 4.712F;
            f9 = 4.712F;
            f10 = 1.571F;
            f11 = 1.571F;
        }
        RightArm.xRot = f8;
        unicornarm.xRot = 0.0F;
        LeftArm.xRot = f9;
        RightLeg.xRot = f10;
        LeftLeg.xRot = f11;
        RightArm.zRot = 0.0F;
        unicornarm.zRot = 0.0F;
        LeftArm.zRot = 0.0F;
        for(int i = 0; i < Tail.length; i++)
        {
            if(rainboom)
            {
                Tail[i].zRot = 0.0F;
            } else
            {
                Tail[i].zRot = MathHelper.cos(limbSwing * 0.8F) * 0.2F * limbSwingAmount;
            }
        }

        if(heldItemRight != 0 && !rainboom && !isUnicorn)
        {
            RightArm.xRot = RightArm.xRot * 0.5F - 0.3141593F;
        }
        float f12 = 0.0F;
//        if(f5 > -9990F && !isUnicorn)
//        {
//            f12 = MathHelper.sin(MathHelper.sqrt(f5) * 3.141593F * 2.0F) * 0.2F;
//        }
        Body.yRot = (float)((double)f12 * 0.20000000000000001D);
        for(int j = 0; j < Bodypiece.length; j++)
        {
            Bodypiece[j].yRot = (float)((double)f12 * 0.20000000000000001D);
        }

        for(int k = 0; k < LeftWing.length; k++)
        {
            LeftWing[k].yRot = (float)((double)f12 * 0.20000000000000001D);
        }

        for(int l = 0; l < RightWing.length; l++)
        {
            RightWing[l].yRot = (float)((double)f12 * 0.20000000000000001D);
        }

        for(int i1 = 0; i1 < Tail.length; i1++)
        {
            Tail[i1].yRot = f12;
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
            RightArm.z = f13 + 2.0F;
            LeftArm.z = (0.0F - f13) + 2.0F;
        } else
        {
            RightArm.z = f13 + 1.0F;
            LeftArm.z = (0.0F - f13) + 1.0F;
        }
        RightArm.x = (0.0F - f14 - 1.0F) + f15;
        LeftArm.x = (f14 + 1.0F) - f15;
        RightLeg.x = (0.0F - f14 - 1.0F) + f15;
        LeftLeg.x = (f14 + 1.0F) - f15;
        RightArm.yRot += Body.yRot;
        LeftArm.yRot += Body.yRot;
        LeftArm.xRot += Body.yRot;
        RightArm.y = 8F;
        LeftArm.y = 8F;
        RightLeg.y = 4F;
        LeftLeg.y = 4F;
//        if(f5 > -9990F)
//        {
//            float f16 = f5;
//            f16 = 1.0F - f5;
//            f16 *= f16 * f16;
//            f16 = 1.0F - f16;
//            float f22 = MathHelper.sin(f16 * 3.141593F);
//            float f28 = MathHelper.sin(f5 * 3.141593F);
//            float f33 = f28 * -(Head.xRot - 0.7F) * 0.75F;
//            if(isUnicorn)
//            {
//                unicornarm.xRot -= (double)f22 * 1.2D + (double)f33;
//                unicornarm.yRot += Body.yRot * 2.0F;
//                unicornarm.zRot = f28 * -0.4F;
//            } else
//            {
//                unicornarm.xRot -= (double)f22 * 1.2D + (double)f33;
//                unicornarm.yRot += Body.yRot * 2.0F;
//                unicornarm.zRot = f28 * -0.4F;
////                RightArm.xRot -= (double)f22 * 1.2D + (double)f33;
////                RightArm.yRot += Body.yRot * 2.0F;
////                RightArm.zRot = f28 * -0.4F;
//            }
//        }
        if(isSneak && !isFlying)
        {
            float f17 = 0.4F;
            float f23 = 7F;
            float f29 = -4F;
            Body.xRot = f17;
            Body.y = f23;
            Body.z = f29;
            for(int i3 = 0; i3 < Bodypiece.length; i3++)
            {
                Bodypiece[i3].xRot = f17;
                Bodypiece[i3].y = f23;
                Bodypiece[i3].z = f29;
            }

            float f34 = 3.5F;
            float f37 = 6F;
            for(int i4 = 0; i4 < LeftWingExt.length; i4++)
            {
                LeftWingExt[i4].xRot = (float)((double)f17 + 2.3561947345733643D);
                LeftWingExt[i4].y = f23 + f34;
                LeftWingExt[i4].z = f29 + f37;
                LeftWingExt[i4].xRot = 2.5F;
                LeftWingExt[i4].zRot = -6F;
            }

            float f40 = 4.5F;
            float f43 = 6F;
            for(int i5 = 0; i5 < LeftWingExt.length; i5++)
            {
                RightWingExt[i5].xRot = (float)((double)f17 + 2.3561947345733643D);
                RightWingExt[i5].y = f23 + f40;
                RightWingExt[i5].z = f29 + f43;
                RightWingExt[i5].xRot = 2.5F;
                RightWingExt[i5].zRot = 6F;
            }

            RightLeg.xRot -= 0.0F;
            LeftLeg.xRot -= 0.0F;
            RightArm.xRot -= 0.4F;
            unicornarm.xRot += 0.4F;
            LeftArm.xRot -= 0.4F;
            RightLeg.z = 10F;
            LeftLeg.z = 10F;
            RightLeg.y = 7F;
            LeftLeg.y = 7F;
            float f46;
            float f48;
            float f50;
            if(isSleeping)
            {
                f46 = 2.0F;
                f48 = -1F;
                f50 = 1.0F;
            } else
            {
                f46 = 6F;
                f48 = -2F;
                f50 = 0.0F;
            }
            Head.y = f46;
            Head.z = f48;
            Head.x = f50;
            Helmet.y = f46;
            Helmet.z = f48;
            Helmet.x = f50;
            Headpiece[0].y = f46;
            Headpiece[0].z = f48;
            Headpiece[0].x = f50;
            Headpiece[1].y = f46;
            Headpiece[1].z = f48;
            Headpiece[1].x = f50;
            Headpiece[2].y = f46;
            Headpiece[2].z = f48;
            Headpiece[2].x = f50;
            float f52 = 0.0F;
            float f54 = 8F;
            float f56 = -14F;
            float f58 = 0.0F - f52;
            float f60 = 9F - f54;
            float f62 = -4F - f56;
            float f63 = 0.0F;
            for(int i6 = 0; i6 < Tail.length; i6++)
            {
                Tail[i6].x = f58;
                Tail[i6].y = f60;
                Tail[i6].z = f62;
                Tail[i6].xRot = f63;
            }

        } else
        {
            float f18 = 0.0F;
            float f24 = 0.0F;
            float f30 = 0.0F;
            Body.xRot = f18;
            Body.y = f24;
            Body.z = f30;
            for(int j3 = 0; j3 < Bodypiece.length; j3++)
            {
                Bodypiece[j3].xRot = f18;
                Bodypiece[j3].y = f24;
                Bodypiece[j3].z = f30;
            }

            if(isPegasus)
            {
                if(!isFlying)
                {
                    for(int k3 = 0; k3 < LeftWing.length; k3++)
                    {
                        LeftWing[k3].xRot = (float)((double)f18 + 1.5707964897155762D);
                        LeftWing[k3].y = f24 + 13F;
                        LeftWing[k3].z = f30 - 3F;
                    }

                    for(int l3 = 0; l3 < RightWing.length; l3++)
                    {
                        RightWing[l3].xRot = (float)((double)f18 + 1.5707964897155762D);
                        RightWing[l3].y = f24 + 13F;
                        RightWing[l3].z = f30 - 3F;
                    }

                } else
                {
                    float f35 = 5.5F;
                    float f38 = 3F;
                    for(int j4 = 0; j4 < LeftWingExt.length; j4++)
                    {
                        LeftWingExt[j4].xRot = (float)((double)f18 + 1.5707964897155762D);
                        LeftWingExt[j4].y = f24 + f35;
                        LeftWingExt[j4].z = f30 + f38;
                    }

                    float f41 = 6.5F;
                    float f44 = 3F;
                    for(int j5 = 0; j5 < RightWingExt.length; j5++)
                    {
                        RightWingExt[j5].xRot = (float)((double)f18 + 1.5707964897155762D);
                        RightWingExt[j5].y = f24 + f41;
                        RightWingExt[j5].z = f30 + f44;
                    }

                }
            }
            RightLeg.z = 10F;
            LeftLeg.z = 10F;
            RightLeg.y = 8F;
            LeftLeg.y = 8F;
            float f36 = MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            float f39 = MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            unicornarm.zRot += f36;
            unicornarm.xRot += f39;
            if(isPegasus && isFlying)
            {
                WingRotateAngleY = MathHelper.sin(ageInTicks * 0.067F * 8F) * 1.0F;
                WingRotateAngleZ = MathHelper.sin(ageInTicks * 0.067F * 8F) * 1.0F;
                for(int k4 = 0; k4 < LeftWingExt.length; k4++)
                {
                    LeftWingExt[k4].xRot = 2.5F;
                    LeftWingExt[k4].zRot = -WingRotateAngleZ - 4.712F - 0.4F;
                }

                for(int l4 = 0; l4 < RightWingExt.length; l4++)
                {
                    RightWingExt[l4].xRot = 2.5F;
                    RightWingExt[l4].zRot = WingRotateAngleZ + 4.712F + 0.4F;
                }

            }
            float f42;
            float f45;
            float f47;
            if(isSleeping)
            {
                f42 = 2.0F;
                f45 = 1.0F;
                f47 = 1.0F;
            } else
            {
                f42 = 0.0F;
                f45 = 0.0F;
                f47 = 0.0F;
            }
            Head.y = f42;
            Head.z = f45;
            Head.x = f47;
            Helmet.y = f42;
            Helmet.z = f45;
            Helmet.x = f47;
            Headpiece[0].y = f42;
            Headpiece[0].z = f45;
            Headpiece[0].x = f47;
            Headpiece[1].y = f42;
            Headpiece[1].z = f45;
            Headpiece[1].x = f47;
            Headpiece[2].y = f42;
            Headpiece[2].z = f45;
            Headpiece[2].x = f47;
            float f49 = 0.0F;
            float f51 = 8F;
            float f53 = -14F;
            float f55 = 0.0F - f49;
            float f57 = 9F - f51;
            float f59 = 0.0F - f53;
            float f61 = 0.5F * limbSwingAmount;
            for(int k5 = 0; k5 < Tail.length; k5++)
            {
                Tail[k5].x = f55;
                Tail[k5].y = f57;
                Tail[k5].z = f59;
                if(rainboom)
                {
                    Tail[k5].xRot = 1.571F + 0.1F * MathHelper.sin(limbSwing);
                } else
                {
                    Tail[k5].xRot = f61;
                }
            }

            for(int l5 = 0; l5 < Tail.length; l5++)
            {
                if(!rainboom)
                {
                    Tail[l5].xRot += f39;
                }
            }

        }
        LeftWingExt[2].xRot = LeftWingExt[2].xRot - 0.85F;
        LeftWingExt[3].xRot = LeftWingExt[3].xRot - 0.75F;
        LeftWingExt[4].xRot = LeftWingExt[4].xRot - 0.5F;
        LeftWingExt[6].xRot = LeftWingExt[6].xRot - 0.85F;
        RightWingExt[2].xRot = RightWingExt[2].xRot - 0.85F;
        RightWingExt[3].xRot = RightWingExt[3].xRot - 0.75F;
        RightWingExt[4].xRot = RightWingExt[4].xRot - 0.5F;
        RightWingExt[6].xRot = RightWingExt[6].xRot - 0.85F;
        Bodypiece[9].xRot = Bodypiece[9].xRot + 0.5F;
        Bodypiece[10].xRot = Bodypiece[10].xRot + 0.5F;
        Bodypiece[11].xRot = Bodypiece[11].xRot + 0.5F;
        Bodypiece[12].xRot = Bodypiece[12].xRot + 0.5F;
        if(rainboom)
        {
            for(int j1 = 0; j1 < Tail.length; j1++)
            {
                Tail[j1].y = Tail[j1].y + 6F;
                Tail[j1].z = Tail[j1].z + 1.0F;
            }

        }
//        if(isRiding)
//        {
//            float f19 = -10F;
//            float f25 = -10F;
//            Head.y = Head.y + f19;
//            Head.z = Head.z + f25;
//            Headpiece[0].y = Headpiece[0].y + f19;
//            Headpiece[0].z = Headpiece[0].z + f25;
//            Headpiece[1].y = Headpiece[1].y + f19;
//            Headpiece[1].z = Headpiece[1].z + f25;
//            Headpiece[2].y = Headpiece[2].y + f19;
//            Headpiece[2].z = Headpiece[2].z + f25;
//            Helmet.y = Helmet.y + f19;
//            Helmet.z = Helmet.z + f25;
//            Body.y = Body.y + f19;
//            Body.z = Body.z + f25;
//            for(int k1 = 0; k1 < Bodypiece.length; k1++)
//            {
//                Bodypiece[k1].y = Bodypiece[k1].y + f19;
//                Bodypiece[k1].z = Bodypiece[k1].z + f25;
//            }
//
//            LeftArm.y = LeftArm.y + f19;
//            LeftArm.z = LeftArm.z + f25;
//            RightArm.y = RightArm.y + f19;
//            RightArm.z = RightArm.z + f25;
//            LeftLeg.y = LeftLeg.y + f19;
//            LeftLeg.z = LeftLeg.z + f25;
//            RightLeg.y = RightLeg.y + f19;
//            RightLeg.z = RightLeg.z + f25;
//            for(int l1 = 0; l1 < Tail.length; l1++)
//            {
//                Tail[l1].y = Tail[l1].y + f19;
//                Tail[l1].z = Tail[l1].z + f25;
//            }
//
//            for(int i2 = 0; i2 < LeftWing.length; i2++)
//            {
//                LeftWing[i2].y = LeftWing[i2].y + f19;
//                LeftWing[i2].z = LeftWing[i2].z + f25;
//            }
//
//            for(int j2 = 0; j2 < RightWing.length; j2++)
//            {
//                RightWing[j2].y = RightWing[j2].y + f19;
//                RightWing[j2].z = RightWing[j2].z + f25;
//            }
//
//            for(int k2 = 0; k2 < LeftWingExt.length; k2++)
//            {
//                LeftWingExt[k2].y = LeftWingExt[k2].y + f19;
//                LeftWingExt[k2].z = LeftWingExt[k2].z + f25;
//            }
//
//            for(int l2 = 0; l2 < RightWingExt.length; l2++)
//            {
//                RightWingExt[l2].y = RightWingExt[l2].y + f19;
//                RightWingExt[l2].z = RightWingExt[l2].z + f25;
//            }
//
//        }
        if(isSleeping)
        {
            RightArm.z = RightArm.z + 6F;
            LeftArm.z = LeftArm.z + 6F;
            RightLeg.z = RightLeg.z - 8F;
            LeftLeg.z = LeftLeg.z - 8F;
            RightArm.y = RightArm.y + 2.0F;
            LeftArm.y = LeftArm.y + 2.0F;
            RightLeg.y = RightLeg.y + 2.0F;
            LeftLeg.y = LeftLeg.y + 2.0F;
        }
        if(aimedBow)
        {
            float f20 = 0.0F;
            float f26 = 0.0F;
            unicornarm.zRot = 0.0F;
            unicornarm.yRot = -(0.1F - f20 * 0.6F) + Head.yRot;
            unicornarm.xRot = 4.712F + Head.xRot;
            unicornarm.xRot -= f20 * 1.2F - f26 * 0.4F;
            float f31 = ageInTicks;
            unicornarm.zRot += MathHelper.cos(f31 * 0.09F) * 0.05F + 0.05F;
            unicornarm.xRot += MathHelper.sin(f31 * 0.067F) * 0.05F;
            if(!isUnicorn)
            {
                RightArm.z = RightArm.z + 1.0F;
            }
        }
    }

    @Override
    public void renderToBuffer(MatrixStack mStack, IVertexBuilder iVertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
        mStack.pushPose();
        if(isSleeping){
            RenderSystem.rotatef(90, 1, 0, 0);
            RenderSystem.translatef(0f, -0.5f, -0.9f);
        }
        Head.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        Headpiece[0].render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        Headpiece[1].render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        if(isUnicorn)
        {
            Headpiece[2].render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        }
        Helmet.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        Body.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        for(int i = 0; i < Bodypiece.length; i++)
        {
            Bodypiece[i].render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        }

        LeftArm.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        RightArm.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        LeftLeg.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        RightLeg.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        for(int j = 0; j < Tail.length; j++)
        {
            Tail[j].render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        }

        if(isPegasus)
        {
            if(isFlying || isSneak)
            {
                for(int k = 0; k < LeftWingExt.length; k++)
                {
                    LeftWingExt[k].render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
                }

                for(int l = 0; l < RightWingExt.length; l++)
                {
                    RightWingExt[l].render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
                }

            } else
            {
                for(int i1 = 0; i1 < LeftWing.length; i1++)
                {
                    LeftWing[i1].render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
                }

                for(int j1 = 0; j1 < RightWing.length; j1++)
                {
                    RightWing[j1].render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
                }

            }
        }
        mStack.popPose();
    }
//
//    protected void renderGlow(EntityRendererManager rendermanager, PlayerEntity entityplayer)
//    {
//        ItemStack itemstack = entityplayer.inventory.getSelected();
//        if(itemstack == null)
//        {
//            return;
//        } else
//        {
//            RenderSystem.pushMatrix();
//            double d = entityplayer.posX;
//            double d1 = entityplayer.posY;
//            double d2 = entityplayer.posZ;
//            RenderSystem.enableRescaleNormal();
//            RenderSystem.translatef((float)d + 0.0F, (float)d1 + 2.3F, (float)d2);
//            RenderSystem.scalef(5F, 5F, 5F);
//            RenderSystem.rotatef(-rendermanager.playerViewY, 0.0F, 1.0F, 0.0F);
//            RenderSystem.rotatef(rendermanager.playerViewX, 1.0F, 0.0F, 0.0F);
////            RenderEngine renderengine = rendermanager.getTextureManager();
////            renderengine.bind("/fx/glow.png");
//            WorldRenderer tessellator = Tessellator.getInstance().getWorldRenderer();
//            float f = 0.0F;
//            float f1 = 0.25F;
//            float f2 = 0.0F;
//            float f3 = 0.25F;
//            float f4 = 1.0F;
//            float f5 = 0.5F;
//            float f6 = 0.25F;
//    		tessellator.func_181668_a(7, DefaultVertexFormats.field_181710_j);
//            tessellator.setNormal(0.0F, 1.0F, 0.0F);
//            tessellator.addVertexWithUV(-1D, -1D, 0.0D, 0.0D, 1.0D);
//            tessellator.addVertexWithUV(-1D, 1.0D, 0.0D, 1.0D, 1.0D);
//            tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, 1.0D, 0.0D);
//            tessellator.addVertexWithUV(1.0D, -1D, 0.0D, 0.0D, 0.0D);
//            Tessellator.getInstance().end();
//            RenderSystem.disableRescaleNormal();
//            RenderSystem.popMatrix();
//            return;
//        }
//    }
}
