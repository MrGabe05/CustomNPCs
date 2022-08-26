package noppes.npcs.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;

public class AniYes implements AnimationBase {

	@Override
	public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {

	}

	@Override
	public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {
		float ticks = (entity.tickCount - animationStart) / 8f;
		float ticks2 = (entity.tickCount + 1 - animationStart) / 8f;
		ticks += (ticks2 - ticks) * Minecraft.getInstance().getDeltaFrameTime();

		ticks = ticks % 2;
		float ani = ticks - 0.5f;
		if(ticks > 1)
			ani = 1.5f - ticks;
		model.head.xRot = ani;
	}
}
