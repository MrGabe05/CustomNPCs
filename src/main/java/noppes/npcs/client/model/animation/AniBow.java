package noppes.npcs.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;

public class AniBow implements AnimationBase {

	@Override
	public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {

	}

	@Override
	public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {
		float ticks = (entity.tickCount - animationStart) / 10f;
		if(ticks > 1)
			ticks = 1;
		float ticks2 = (entity.tickCount + 1 - animationStart) / 10f;
		if(ticks2 > 1)
			ticks2 = 1;
		ticks += (ticks2 - ticks) * Minecraft.getInstance().getDeltaFrameTime();
		model.body.xRot = ticks;
		model.head.xRot = ticks;
		model.leftArm.xRot = ticks;
		model.rightArm.xRot = ticks;

		model.body.z = -ticks * 10;
		model.body.y = ticks * 6;

		model.head.z = -ticks * 10;
		model.head.y = ticks * 6;

		model.leftArm.z = -ticks * 10;
		model.leftArm.y += ticks * 6;

		model.rightArm.z = -ticks * 10;
		model.rightArm.y += ticks * 6;
	}
}
