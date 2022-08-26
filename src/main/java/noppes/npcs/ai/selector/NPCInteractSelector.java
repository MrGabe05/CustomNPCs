package noppes.npcs.ai.selector;

import com.google.common.base.Predicate;

import net.minecraft.entity.LivingEntity;
import noppes.npcs.entity.EntityNPCInterface;

public class NPCInteractSelector implements Predicate {
	private EntityNPCInterface npc;
	public NPCInteractSelector(EntityNPCInterface npc){
		this.npc = npc;
	}
	
	public boolean isEntityApplicable(EntityNPCInterface entity) {
		if(entity == npc || !npc.isAlive())
			return false;
		if(entity.isAttacking() || npc.getFaction().isAggressiveToNpc(entity) || !npc.ais.stopAndInteract)
			return false;
		return true;
	}
	
	@Override
	public boolean apply(Object ob) {
		if(!(ob instanceof EntityNPCInterface))
			return false;
		return isEntityApplicable((EntityNPCInterface) ob);
	}

}
