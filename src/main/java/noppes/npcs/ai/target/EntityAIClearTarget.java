package noppes.npcs.ai.target;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public class EntityAIClearTarget extends Goal
{
	private final EntityNPCInterface npc;
	private LivingEntity target;
    public EntityAIClearTarget(EntityNPCInterface npc){
    	this.npc = npc;
    }

    @Override
    public boolean canUse(){
    	target = npc.getTarget();
        if (target == null)
            return false;
                
        if(npc.getOwner() != null && !npc.isInRange(npc.getOwner(), npc.stats.aggroRange * 2)){
        	return true;
        }
        
        return npc.combatHandler.checkTarget();
    }

    @Override
    public void start(){
        this.npc.setTarget(null);
        if(target == npc.getLastHurtByMob())
        	this.npc.setLastHurtByMob(null);
        super.start();
    }

    @Override
    public void stop(){
    	npc.getNavigation().stop();
    }
}
