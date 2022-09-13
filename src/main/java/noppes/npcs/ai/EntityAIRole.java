package noppes.npcs.ai;

import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIRole extends Goal {

	private final EntityNPCInterface npc;
	public EntityAIRole(EntityNPCInterface npc){
		this.npc = npc;
	}
	
	@Override
	public boolean canUse() {
		if(npc.isKilled())
			return false;
		return npc.role.aiShouldExecute();
	}

	@Override
    public void start()
    {
    	npc.role.aiStartExecuting();
    }
    
	@Override
    public boolean canContinueToUse()
    {
		if(npc.isKilled())
			return false;
		return npc.role.aiContinueExecute();
    }
	
    public void tick(){
    	npc.role.aiUpdateTask();
    }
}
