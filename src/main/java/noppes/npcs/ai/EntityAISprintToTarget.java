package noppes.npcs.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public class EntityAISprintToTarget extends Goal{
    private EntityNPCInterface npc;


    public EntityAISprintToTarget(EntityNPCInterface par1EntityLiving){
        this.npc = par1EntityLiving;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse(){
        LivingEntity runTarget = this.npc.getTarget();

        if (runTarget == null || this.npc.getNavigation().isDone()){
            return false;
        }
        
    	switch(this.npc.ais.onAttack){       	
    		case 0 : return !this.npc.isInRange(runTarget, 8)? (!this.npc.isOnGround() ? false : true) : false;
    		case 2 : return this.npc.isInRange(runTarget, 7)? (!this.npc.isOnGround() ? false : true) : false;
    		default : return false;
    	}
        
    }

    @Override
    public boolean canContinueToUse(){
        Vector3d mo = this.npc.getDeltaMovement();
        return this.npc.isAlive() && this.npc.isOnGround() && this.npc.hurtTime <= 0 && (mo.x != 0.0D && mo.z != 0.0D);
    }

    @Override
    public void start(){
    	this.npc.setSprinting(true);
    }
    
    @Override
    public void stop(){
        this.npc.setSprinting(false);
    }
}
