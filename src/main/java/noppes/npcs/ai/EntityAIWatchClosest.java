package noppes.npcs.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public class EntityAIWatchClosest extends Goal{
    private final EntityNPCInterface npc;

    /** The closest entity which is being watched by this one. */
    protected Entity closestEntity;
    private final float maxDistance;
    private int lookTime;
    private final float change;
    private final Class<? extends LivingEntity> watchedClass;
    protected final EntityPredicate predicate;

    public EntityAIWatchClosest(EntityNPCInterface par1EntityLiving, Class<? extends LivingEntity> limbSwingAmountClass, float par3){
        this.npc = par1EntityLiving;
        this.watchedClass = limbSwingAmountClass;
        this.maxDistance = par3;
        this.change = 0.002F;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        this.predicate = (new EntityPredicate()).range(par3).allowSameTeam().allowInvulnerable().allowNonAttackable();
    }

    @Override
    public boolean canUse(){
        if (this.npc.getRandom().nextFloat() >= this.change || npc.isInteracting()){
            return false;
        }
        
        if (this.npc.getTarget() != null){
            this.closestEntity = this.npc.getTarget();
        }

        if (this.watchedClass == PlayerEntity.class){
            this.closestEntity = this.npc.level.getNearestPlayer(this.npc, this.maxDistance);
        }
        else{
            this.closestEntity = this.npc.level.getNearestEntity(this.watchedClass, predicate, this.npc, this.npc.getX(), this.npc.getEyeY(), this.npc.getZ(), this.npc.getBoundingBox().inflate(this.maxDistance, 3.0D, this.maxDistance));
            if (this.closestEntity != null){
            	return this.npc.canNpcSee(this.closestEntity);
            }
        }

        return this.closestEntity != null;
        
    }

    @Override
    public boolean canContinueToUse(){
    	if(npc.isInteracting() || npc.isAttacking() || !this.closestEntity.isAlive() || !npc.isAlive())
    		return false;
        return this.npc.isInRange(this.closestEntity, maxDistance) && this.lookTime > 0;
    }

    @Override
    public void start(){
        this.lookTime = 60 + this.npc.getRandom().nextInt(60);
    }

    @Override
    public void stop(){
        this.closestEntity = null;
    }

    @Override
    public void tick(){
        this.npc.getLookControl().setLookAt(this.closestEntity.getX(), this.closestEntity.getY() + (double)this.closestEntity.getEyeHeight(), this.closestEntity.getZ(), 10.0F, (float)this.npc.getMaxHeadXRot());
        --this.lookTime;
    }
}
