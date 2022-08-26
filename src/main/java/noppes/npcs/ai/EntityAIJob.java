package noppes.npcs.ai;

import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public class EntityAIJob extends Goal {

	private EntityNPCInterface npc;
	public EntityAIJob(EntityNPCInterface npc){
		this.npc = npc;
	}
	
	@Override
	public boolean canUse() {
		if(npc.isKilled())
			return false;
		return npc.job.aiShouldExecute();
	}

	@Override
    public void start(){
    	npc.job.aiStartExecuting();
    }
    
	@Override
    public boolean canContinueToUse(){
		if(npc.isKilled())
			return false;
		return npc.job.aiContinueExecute();
    }

	@Override
    public void tick(){
		npc.job.aiUpdateTask();
    }

	@Override
    public void stop() {
		npc.job.stop();
    }

	@Override
    public EnumSet<Goal.Flag> getFlags(){
    	return npc.job.getFlags();
    }
}
