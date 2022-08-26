package noppes.npcs.client.model;

import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class ModelClassicPlayer<T extends LivingEntity> extends PlayerModel<T> {
    public ModelClassicPlayer(float scale) {
        super(scale, false);
    }

    @Override
    public void setupAnim(T entity, float par1, float limbSwingAmount, float par3, float par4, float par5) {
        super.setupAnim(entity, par1, limbSwingAmount, par3, par4, par5);

        float j = 2.0f;

        if (entity.isSprinting()) {
            j = 1.0f;
        }

        rightArm.xRot += MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * j * limbSwingAmount;
        leftArm.xRot += MathHelper.cos(par1 * 0.6662F) * j * limbSwingAmount;
        leftArm.zRot += (MathHelper.cos(par1 * 0.2812F) - 1.0F) * limbSwingAmount;
        rightArm.zRot += (MathHelper.cos(par1 * 0.2312F) + 1.0F) * limbSwingAmount;

        leftSleeve.xRot = leftArm.xRot;
        leftSleeve.yRot = leftArm.yRot;
        leftSleeve.zRot = leftArm.zRot;
        rightSleeve.xRot = rightArm.xRot;
        rightSleeve.yRot = rightArm.yRot;
        rightSleeve.zRot = rightArm.zRot;
    }
}
