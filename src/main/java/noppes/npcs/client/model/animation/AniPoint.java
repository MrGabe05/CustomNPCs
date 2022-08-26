package noppes.npcs.client.model.animation;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;

public class AniPoint implements AnimationBase {

	@Override
	public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {
		model.rightArm.xRot = -1.570796F;
		model.rightArm.yRot = netHeadYaw / (180F / (float)Math.PI);
		model.rightArm.zRot = 0;
	}

	@Override
	public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {

	}
}
