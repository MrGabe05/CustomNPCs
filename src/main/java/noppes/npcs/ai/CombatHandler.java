package noppes.npcs.ai;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.ability.AbstractAbility;
import noppes.npcs.ability.IAbility;
import noppes.npcs.entity.EntityNPCInterface;

public class CombatHandler {
	
	private Map<LivingEntity, Float> aggressors = new HashMap<LivingEntity, Float>();
	
	private EntityNPCInterface npc;
	
	private long startTime = 0;
	
	private int combatResetTimer = 0;

	public CombatHandler(EntityNPCInterface npc) {
		this.npc = npc;
	}
	
	public void update(){
		if(npc.isKilled()){
			if(npc.isAttacking()){
				reset();
			}
			return;
		}
		if(npc.getTarget() != null && !npc.isAttacking()){
			start();
		}

		if(!shouldCombatContinue()){
			if(combatResetTimer++ > 40){
				reset();
			}
			return;
		}
		combatResetTimer = 0;
	}

    private boolean shouldCombatContinue() {
    	if(npc.getTarget() == null)
    		return false;
    	return isValidTarget(npc.getTarget());
	}

	public void damage(DamageSource source, float damageAmount){
		combatResetTimer = 0;
		Entity e = NoppesUtilServer.GetDamageSourcee(source);
		if(e instanceof LivingEntity){
			LivingEntity el = (LivingEntity) e;
			Float f = aggressors.get(el);
			if(f == null)
				f = 0f;
			aggressors.put(el, f + damageAmount);		
		}		
    }
	
	public void start(){
		combatResetTimer = 0;
		startTime = npc.level.getLevelData().getGameTime();
		npc.getEntityData().set(EntityNPCInterface.Attacking, true);
		
		for(AbstractAbility ab : npc.abilities.abilities){
			ab.startCombat();
		}
	}
	
	public void reset(){
		combatResetTimer = 0;
		aggressors.clear();
		npc.getEntityData().set(EntityNPCInterface.Attacking, false);
	}

	public boolean checkTarget() {
		if(aggressors.isEmpty() || npc.tickCount % 10 != 0)
			return false;
		LivingEntity target = npc.getTarget();
		Float current = 0f;
		if(isValidTarget(target)){
			current = aggressors.get(target);
			if(current == null)
				current = 0f;
		}
		else
			target = null;
		for (Map.Entry<LivingEntity, Float> entry : aggressors.entrySet()){
			if(entry.getValue() > current && isValidTarget(entry.getKey())){
				current = entry.getValue();
				target = entry.getKey();
			}
		}
		return target == null;
	}
	
	public boolean isValidTarget(LivingEntity target){
		if(target == null || !target.isAlive())
			return false;
        
        if(target instanceof PlayerEntity && ((PlayerEntity)target).abilities.invulnerable)
        	return false;
        
		return npc.isInRange(target, npc.stats.aggroRange);
	}

}
