package noppes.npcs.ai;

import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public class EntityAITransform extends Goal {

	private EntityNPCInterface npc;
	public EntityAITransform(EntityNPCInterface npc){
		this.npc = npc;
		setFlags(EnumSet.of(Goal.Flag.MOVE));
	}
	
	@Override
	public boolean canUse() {
		if(npc.isKilled() || npc.isAttacking() || npc.transform.editingModus)
			return false;

		return npc.level.isDay()?npc.transform.isActive:!npc.transform.isActive;
	}

	@Override
    public void start(){
    	npc.transform.transform(!npc.transform.isActive);
    }
}
