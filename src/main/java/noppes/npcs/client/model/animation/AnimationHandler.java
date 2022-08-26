package noppes.npcs.client.model.animation;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import noppes.npcs.ModelData;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;

public class AnimationHandler {


    // Util

    // Registry
    private static final HashMap<Integer, AnimationBase> ANIMATIONS = new HashMap<>();

    public static void animateBipedPre(ModelData data, BipedModel bipedModel, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        EntityNPCInterface npc = (EntityNPCInterface) livingEntity;
        //Global Stuff

        bipedModel.body.x = bipedModel.body.y = bipedModel.body.z = 0;
        bipedModel.body.xRot = bipedModel.body.yRot = bipedModel.body.zRot = 0;

        bipedModel.hat.xRot = bipedModel.head.xRot = 0;
        bipedModel.hat.zRot = bipedModel.head.zRot = 0;

        bipedModel.hat.x = bipedModel.head.x = 0;
        bipedModel.hat.y = bipedModel.head.y = 0;
        bipedModel.hat.z = bipedModel.head.z = 0;

        bipedModel.leftLeg.xRot = 0;
        bipedModel.leftLeg.yRot = 0;
        bipedModel.leftLeg.zRot = 0;
        bipedModel.rightLeg.xRot = 0;
        bipedModel.rightLeg.yRot = 0;
        bipedModel.rightLeg.zRot = 0;
        bipedModel.leftArm.x = 0;
        bipedModel.leftArm.y = 2;
        bipedModel.leftArm.z = 0;
        bipedModel.rightArm.x = 0;
        bipedModel.rightArm.y = 2;
        bipedModel.rightArm.z = 0;

        // Global end

        AnimationBase animation = AnimationHandler.getAnimationFor(npc.currentAnimation);
        if (animation != null) {
            animation.animatePre(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, livingEntity, bipedModel, npc.animationStart);
        }
    }

    public static void animateBipedPost(ModelData data, BipedModel bipedModel, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        EntityNPCInterface npc = (EntityNPCInterface) livingEntity;
        if (livingEntity.isSleeping()) {
            if (bipedModel.head.xRot < 0) {
                bipedModel.head.xRot = 90;
                bipedModel.hat.xRot = 90;
            }
        }


        AnimationBase animation = AnimationHandler.getAnimationFor(npc.currentAnimation);
        if (animation != null) {
            animation.animatePost(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, livingEntity, bipedModel, npc.animationStart);
        }

        if (bipedModel.crouching) {
            bipedModel.body.xRot = 0.5F / data.getPartConfig(EnumParts.BODY).scaleY;
        }

        if (bipedModel instanceof PlayerModel) {
            PlayerModel playerModel = (PlayerModel) bipedModel;
            playerModel.leftPants.copyFrom(playerModel.leftLeg);
            playerModel.rightPants.copyFrom(playerModel.rightLeg);
            playerModel.leftSleeve.copyFrom(playerModel.leftArm);
            playerModel.rightSleeve.copyFrom(playerModel.rightArm);
            playerModel.jacket.copyFrom(playerModel.body);
        }
        bipedModel.hat.copyFrom(bipedModel.head);
    }

    public static void addAnimation(int enumAnimation, AnimationBase animationBase) {
        ANIMATIONS.put(enumAnimation, animationBase);
    }

    public static HashMap<Integer, AnimationBase> getAllAnimations() {
        return ANIMATIONS;
    }

    public static AnimationBase getAnimationFor(int animation) {
        return ANIMATIONS.get(animation);
    }

    static{
        addAnimation(AnimationType.NORMAL, new AniBlank());
        addAnimation(AnimationType.SLEEP, new AniBlank());
        addAnimation(AnimationType.CRAWL, new AniCrawling());
        addAnimation(AnimationType.HUG, new AniHug());
        addAnimation(AnimationType.DANCE, new AniDancing());
        addAnimation(AnimationType.WAVE, new AniWaving());
        addAnimation(AnimationType.BOW, new AniBow());
        addAnimation(AnimationType.YES, new AniYes());
        addAnimation(AnimationType.NO, new AniNo());
        addAnimation(AnimationType.POINT, new AniPoint());
        addAnimation(AnimationType.DEATH, new AniBlank());
        addAnimation(AnimationType.AIM, new AniAim());

        //Crying
        addAnimation(AnimationType.CRY, new AnimationBase() {
            @Override
            public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {

            }

            @Override
            public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {
                model.hat.xRot = model.head.xRot = 0.7f;
            }
        });

        // Sitting
        addAnimation(AnimationType.SIT, new AnimationBase() {
            @Override
            public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {
                model.riding = true;
            }

            @Override
            public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {
                model.riding = false;
            }
        });

        // Sitting
        addAnimation(AnimationType.SNEAK, new AnimationBase() {
            @Override
            public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {
                model.crouching = true;
            }

            @Override
            public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {
                model.crouching = false;
            }
        });

    }

}
