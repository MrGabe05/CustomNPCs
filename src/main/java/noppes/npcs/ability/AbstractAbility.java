package noppes.npcs.ability;

import net.minecraft.entity.LivingEntity;
import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class AbstractAbility implements IAbility {
	private long cooldown = 0;
	private final int cooldownTime = 10;
	private final int startCooldownTime = 10;
	protected EntityNPCInterface npc;
	public float maxHP = 1;
	public float minHP =  0;
	
	public AbstractAbility(EntityNPCInterface npc){
		this.npc = npc;
	}

	private boolean onCooldown(){
		return System.currentTimeMillis() < cooldown;
	}
	
	public int getRNG(){
		return 0;
	}

	public boolean canRun(LivingEntity target){
		if(onCooldown())
			return false;
		float f = npc.getHealth() / npc.getMaxHealth();
		if(f < minHP || f > maxHP)
			return false;
		if(getRNG() > 1 && npc.getRandom().nextInt(getRNG()) != 0)
			return false;
		return npc.canNpcSee(target);
	}

	public void endAbility(){
		cooldown = System.currentTimeMillis() + cooldownTime * 1000;
	}
	
	public abstract boolean isType(EnumAbilityType type);

	public void startCombat() {
		cooldown = System.currentTimeMillis() + startCooldownTime * 1000;
	}

}
