package noppes.npcs.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public class EntityAIAttackTarget extends Goal{
    private final World level;
    private final EntityNPCInterface npc;
    private LivingEntity entityTarget;

    private int attackTick;

    private Path entityPathEntity;
    private int field_75445_i;
    
    public EntityAIAttackTarget(EntityNPCInterface par1EntityLiving){
        this.attackTick = 0;
        this.npc = par1EntityLiving;
        this.level = par1EntityLiving.level;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse(){
    	LivingEntity entitylivingbase = this.npc.getTarget();
    	
        if (entitylivingbase == null || !entitylivingbase.isAlive()){
            return false;
        }

        int melee = this.npc.stats.ranged.getMeleeRange();
        if (this.npc.inventory.getProjectile() != null && (melee <= 0 || !this.npc.isInRange(entitylivingbase, melee))){
      	   return false;
        }
        
    	this.entityTarget = entitylivingbase;
        this.entityPathEntity = this.npc.getNavigation().createPath(entitylivingbase, 0);
        return this.entityPathEntity != null;
        
    }

    @Override
    public boolean canContinueToUse(){
    	this.entityTarget = this.npc.getTarget();
    	if(entityTarget == null)
    		entityTarget = this.npc.getLastHurtByMob();
    	
		if(entityTarget == null || !entityTarget.isAlive())
			return false;
		if(!npc.isInRange(entityTarget, npc.stats.aggroRange))
			return false;
        int melee = this.npc.stats.ranged.getMeleeRange();
		if (melee > 0 && !npc.isInRange(entityTarget, melee))
			return false;
		
		return this.npc.isWithinRestriction(entityTarget.blockPosition());
    }

    @Override
    public void start(){
        this.npc.getNavigation().moveTo(this.entityPathEntity, 1.3D);
        this.field_75445_i = 0;
    }

    @Override
    public void stop()
    {
    	this.entityPathEntity = null;
    	this.entityTarget = null;
        this.npc.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.npc.getLookControl().setLookAt(this.entityTarget, 30.0F, 30.0F);

        if (--this.field_75445_i <= 0)
        {
            this.field_75445_i = 4 + this.npc.getRandom().nextInt(7);
            this.npc.getNavigation().moveTo(this.entityTarget, 1.3f);
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        double y = this.entityTarget.getY();
        if(this.entityTarget.getBoundingBox() != null)
        	y = this.entityTarget.getBoundingBox().minY;
        double distance = this.npc.distanceToSqr(this.entityTarget.getX(), y, this.entityTarget.getZ());
        double range = npc.stats.melee.getRange() * npc.stats.melee.getRange() + entityTarget.getBbWidth();
        double minRange = this.npc.getBbWidth() * 2.0F * this.npc.getBbWidth() * 2.0F + entityTarget.getBbWidth();
        if(minRange > range)
        	range = minRange;

        if (distance <= range && (npc.canNpcSee(this.entityTarget) || distance < minRange))
        {
            if (this.attackTick <= 0)
            {
                this.attackTick = this.npc.stats.melee.getDelay();
        		npc.swing(Hand.MAIN_HAND);
                this.npc.doHurtTarget(this.entityTarget);
            }
        }
    }
}
