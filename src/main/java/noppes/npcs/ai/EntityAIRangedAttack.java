package noppes.npcs.ai;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public class EntityAIRangedAttack extends Goal{
    private final EntityNPCInterface npc;

    private LivingEntity attackTarget;

    private int rangedAttackTime = 0;
    private int moveTries = 0;
    private int burstCount = 0;
    private int attackTick = 0;
    private boolean hasFired = false;

    public EntityAIRangedAttack(IRangedAttackMob par1IRangedAttackMob){
        if (!(par1IRangedAttackMob instanceof LivingEntity)){
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.npc = (EntityNPCInterface)par1IRangedAttackMob;
        this.rangedAttackTime = this.npc.stats.ranged.getDelayMin() / 2;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse(){
    	attackTarget = this.npc.getTarget();

        if (attackTarget == null || !attackTarget.isAlive() || !npc.isInRange(attackTarget, npc.stats.aggroRange) || this.npc.inventory.getProjectile() == null)
        	return false;
        		
        
        if (this.npc.stats.ranged.getMeleeRange() >= 1 && npc.isInRange(attackTarget, this.npc.stats.ranged.getMeleeRange()))
        	return false;
        
        return true;
    }

    @Override
    public void stop(){
        this.attackTarget = null;
        this.npc.setTarget(null);
        this.npc.getNavigation().stop();
        this.moveTries = 0;
        this.hasFired = false;
        this.rangedAttackTime = this.npc.stats.ranged.getDelayMin() / 2;
    }

    @Override
    public void tick(){
    	this.npc.getLookControl().setLookAt(this.attackTarget, 30.0F, 30.0F);
        double var1 = this.npc.distanceToSqr(this.attackTarget.getX(), this.attackTarget.getBoundingBox().minY, this.attackTarget.getZ());
		float range = this.npc.stats.ranged.getRange() * this.npc.stats.ranged.getRange();

		if (this.npc.ais.directLOS){
			if (this.npc.getSensing().canSee(this.attackTarget)){
				++this.moveTries;
		    }
			else{
				this.moveTries = 0;
			}
			
			int v = 15;
			if (var1 <= (double)range && this.moveTries >= v){
				this.npc.getNavigation().stop();
			}
			else{
				this.npc.getNavigation().moveTo(this.attackTarget, 1.0D);
			}
        }
        
        if (this.rangedAttackTime-- <= 0){
            if (var1 <= (double)range && (this.npc.getSensing().canSee(this.attackTarget) || this.npc.stats.ranged.getFireType() == 2)){
            	 if (this.burstCount++ <= this.npc.stats.ranged.getBurst()){
                     this.rangedAttackTime = this.npc.stats.ranged.getBurstDelay();
                 }
                 else{
                	 this.burstCount = 0;
                	 this.hasFired = true;
                	 this.rangedAttackTime = this.npc.stats.ranged.getDelayRNG();
                 }
            	 
            	 if (this.burstCount > 1){
            		 boolean indirect = false;
            		 
            		 switch(this.npc.stats.ranged.getFireType()){
            		     case 1 : indirect = var1 > (double)range / 2; break;
            		     case 2 : indirect = !this.npc.getSensing().canSee(this.attackTarget);
            		 }
            		 
            		 this.npc.performRangedAttack(this.attackTarget, indirect ? 1 : 0);
            		 if (this.npc.currentAnimation != AnimationType.AIM){
            			npc.swing(Hand.MAIN_HAND);
            		 }
                 }
            } 
        }
    }
    
    public boolean hasFired(){
    	return this.hasFired;
    }
}