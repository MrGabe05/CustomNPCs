package noppes.npcs.ai;

import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;
import java.util.List;

public class EntityAIMovingPath extends Goal{
    private EntityNPCInterface npc;
    private int[] pos;
    private int retries = 0;

    public EntityAIMovingPath(EntityNPCInterface iNpc) {
        this.npc = iNpc;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse(){
        if (npc.isAttacking() || npc.isInteracting() || npc.getRandom().nextInt(40) != 0 && npc.ais.movingPause || !npc.getNavigation().isDone())
            return false;
        
        List<int[]> list = npc.ais.getMovingPath();
        if(list.size() < 2)
        	return false;
        
        npc.ais.incrementMovingPath();
    	pos = npc.ais.getCurrentMovingPath();
    	retries = 0;
        
        return true;
    }

    @Override
    public boolean canContinueToUse(){
    	if(npc.isAttacking() || npc.isInteracting()){
    		npc.ais.decreaseMovingPath();
    		return false;
    	}
    	if(this.npc.getNavigation().isDone()) {
        	this.npc.getNavigation().stop();
        	if(npc.distanceToSqr(pos[0], pos[1], pos[2]) < 3) {
        		return false;
        	}
    		if(retries++ < 3) {
    			start();
    	        return true;
    		}
    		return false;
    	}
        return true;
    }

    @Override
    public void start(){
        this.npc.getNavigation().moveTo(pos[0] + 0.5, pos[1], pos[2] + 0.5, 1.0D);
    }
}
