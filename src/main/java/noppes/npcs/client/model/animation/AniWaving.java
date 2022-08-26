package noppes.npcs.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class AniWaving implements AnimationBase {

	@Override
	public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {

	}

	@Override
	public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedModel model, int animationStart) {
		float f = MathHelper.sin(entity.tickCount * 0.27f);
		float f2 = MathHelper.sin((entity.tickCount + 1) * 0.27f);
		f += (f2 - f) * Minecraft.getInstance().getDeltaFrameTime();

		model.rightArm.xRot = -0.1f;
		model.rightArm.yRot = 0;
		model.rightArm.zRot = (float) (Math.PI - 1f  - f * 0.5f );
	}
}
