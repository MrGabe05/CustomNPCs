package noppes.npcs.ai;

import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.ability.AbstractAbility;
import noppes.npcs.ability.IAbilityUpdate;
import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIAbilities extends Goal {
	private EntityNPCInterface npc;
	private IAbilityUpdate ability;
	
	public EntityAIAbilities(EntityNPCInterface npc){
		this.npc = npc;
	}
	
	@Override
	public boolean canUse() {
		if(!npc.isAttacking())
			return false;
		ability = (IAbilityUpdate)npc.abilities.getAbility(EnumAbilityType.UPDATE);
		return ability != null;
	}
	
	@Override
    public boolean canContinueToUse(){
		return npc.isAttacking() && ability.isActive();
    }

    @Override
    public void tick(){
    	ability.update();
    }

    @Override
    public void stop() {
    	((AbstractAbility)ability).endAbility();    
    	ability = null;
    }

}
