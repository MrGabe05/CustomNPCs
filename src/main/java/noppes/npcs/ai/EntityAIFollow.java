package noppes.npcs.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public class EntityAIFollow extends Goal
{
    private EntityNPCInterface npc;
    private LivingEntity owner;
	public int updateTick = 0;

    public EntityAIFollow(EntityNPCInterface npc){
        this.npc = npc;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse(){
    	if(!canExcute())
    		return false;
    	return !npc.isInRange(owner, npc.followRange());
    }
    
    public boolean canExcute(){
    	if(!npc.isAlive() || !npc.isFollower() || npc.isAttacking() || (owner = npc.getOwner()) == null || npc.ais.animationType == AnimationType.SIT)
    		return false;
    	return true;
    }

    @Override
    public void start(){
		updateTick = 10;
    }

    @Override
    public boolean canContinueToUse(){
        return !this.npc.getNavigation().isDone() && !npc.isInRange(owner, 2) && canExcute();
    }
    @Override
    public void stop(){
        this.owner = null;
        this.npc.getNavigation().stop();
    }

    @Override
    public void tick(){
    	updateTick++;
    	if(updateTick < 10)
    		return;
    	updateTick = 0;
        this.npc.getLookControl().setLookAt(owner, 10.0F, (float)this.npc.getMaxHeadXRot());
        
        double distance = npc.distanceToSqr(owner);
		double speed = 1 + distance / 150;
		if(speed > 3)
			speed = 3;
		if(owner.isSprinting())
			speed += 0.5f;
        if (this.npc.getNavigation().moveTo(owner, speed) || npc.isInRange(owner, 16))
        	return;
        
        npc.tpTo(owner);
    }
}
