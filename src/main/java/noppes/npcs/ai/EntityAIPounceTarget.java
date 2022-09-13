package noppes.npcs.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public class EntityAIPounceTarget extends Goal{
	private final EntityNPCInterface npc;
	private LivingEntity leapTarget;
	private final float leapSpeed = 1.3F;

    public EntityAIPounceTarget(EntityNPCInterface leapingEntity){
        this.npc = leapingEntity;
        this.setFlags(EnumSet.of(Flag.JUMP));
    }

    @Override
    public boolean canUse(){
        if(!this.npc.isOnGround())
        	return false;
        
        this.leapTarget = this.npc.getTarget();
        
        if (this.leapTarget == null || !this.npc.getSensing().canSee(leapTarget))
        	return false;
        
        return !npc.isInRange(leapTarget, 4) && npc.isInRange(leapTarget, 8) && this.npc.getRandom().nextInt(5) == 0;
    }

    @Override
    public boolean canContinueToUse(){
        return !this.npc.isOnGround();
    }

    @Override
    public void start(){
    	double varX = this.leapTarget.getX() - this.npc.getX();
		double varY = this.leapTarget.getBoundingBox().minY - this.npc.getBoundingBox().minY;
		double varZ = this.leapTarget.getZ() - this.npc.getZ();
		float varF = MathHelper.sqrt(varX * varX + varZ * varZ);
		float angle = this.getAngleForXYZ(varX, varY, varZ, varF);
        float yaw = (float)(Math.atan2(varX, varZ) * 180.0D / Math.PI);
        Vector3d mo = new Vector3d(MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(angle / 180.0F * (float)Math.PI),
                MathHelper.sin((angle + 1.0F) / 180.0F * (float)Math.PI),
                MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(angle / 180.0F * (float)Math.PI));

        mo.scale(this.leapSpeed);
        this.npc.setDeltaMovement(mo);
    }
    
    public float getAngleForXYZ(double varX, double varY, double varZ, double horiDist) {
    	float g = 0.1F;
    	float var1 = this.leapSpeed * this.leapSpeed;
    	double var2 = (g * horiDist);
    	double var3 = ((g * horiDist * horiDist) + (2 * varY * var1));
    	double var4 = (var1 * var1) - (g * var3);
    	if (var4 < 0) return 90.0F;
    	float var6 = var1 - MathHelper.sqrt(var4);
    	float var7 = (float) (Math.atan2(var6 , var2) * 180.0D / Math.PI);
    	return var7;
    }
}