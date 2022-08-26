package noppes.npcs.client.model.animation;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;

public interface AnimationBase {
    // Called Before Super is called
    void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart);

    // Called After Super is called
    void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart);
}
