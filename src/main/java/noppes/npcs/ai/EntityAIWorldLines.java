package noppes.npcs.ai;

import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIWorldLines extends Goal {

	private EntityNPCInterface npc;
	private int cooldown = 100;
	
	public EntityAIWorldLines(EntityNPCInterface npc){
		this.npc = npc;
	}
	
	@Override
	public boolean canUse() {
		if(cooldown > 0){
			cooldown--;
		}
		return !npc.isAttacking() && !npc.isKilled() && npc.advanced.hasWorldLines() && npc.getRandom().nextInt(1800) == 1;
	}

	@Override
    public void start(){
    	cooldown = 100;
    	npc.saySurrounding(npc.advanced.getWorldLine());
    }
    
}
