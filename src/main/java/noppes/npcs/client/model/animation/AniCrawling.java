package noppes.npcs.client.model.animation;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class AniCrawling implements AnimationBase {

    @Override
    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {

    }

    @Override
    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {

        model.head.zRot = -netHeadYaw / (180F / (float) Math.PI);
        model.head.yRot = 0;
        model.head.xRot = -55 / (180F / (float) Math.PI);

        model.hat.xRot = model.head.xRot;
        model.hat.yRot = model.head.yRot;
        model.hat.zRot = model.head.zRot;

        if (limbSwingAmount > 0.25)
            limbSwingAmount = 0.25f;
        float movement = MathHelper.cos(limbSwing * 0.8f + (float) Math.PI) * limbSwingAmount;

        model.leftArm.xRot = 180 / (180F / (float) Math.PI) - movement * 0.25f;
        model.leftArm.yRot = movement * -0.46f;
        model.leftArm.zRot = movement * -0.2f;
        model.leftArm.y = 2 - movement * 9.0F;

        model.rightArm.xRot = 180 / (180F / (float) Math.PI) + movement * 0.25f;
        model.rightArm.yRot = movement * -0.4f;
        model.rightArm.zRot = movement * -0.2f;
        model.rightArm.y = 2 + movement * 9.0F;

        model.body.yRot = movement * 0.1f;
        model.body.xRot = 0;
        model.body.zRot = movement * 0.1f;

        model.leftLeg.xRot = movement * 0.1f;
        model.leftLeg.yRot = movement * 0.1f;
        model.leftLeg.zRot = -7 / (180F / (float) Math.PI) - movement * 0.25f;
        model.leftLeg.y = 10.4f + movement * 9.0F;
        model.leftLeg.z = movement * 0.6f;

        model.rightLeg.xRot = movement * -0.1f;
        model.rightLeg.yRot = movement * 0.1f;
        model.rightLeg.zRot = 7 / (180F / (float) Math.PI) - movement * 0.25f;
        model.rightLeg.y = 10.4f - movement * 9.0F;
        model.rightLeg.z = movement * -0.6f;
    }

}
