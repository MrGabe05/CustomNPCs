package noppes.npcs.ai.selector;

import com.google.common.base.Predicate;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effects;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobGuard;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.companion.CompanionGuard;

public class NPCAttackSelector implements Predicate<LivingEntity>{
	private final EntityNPCInterface npc;
	
	public NPCAttackSelector(EntityNPCInterface npc){
		this.npc = npc;
	}
	
    public boolean isEntityApplicable(LivingEntity entity){
    	if(!entity.isAlive() || entity == npc || !npc.isInRange(entity, npc.stats.aggroRange) || entity.getHealth() < 1)
    		return false;
        if (this.npc.ais.directLOS && !this.npc.getSensing().canSee(entity)) {
			return false;
		}

    	//prevent the npc from going on an endless killing spree
    	if(!npc.isFollower() && npc.ais.shouldReturnHome()){ 
	    	int allowedDistance = npc.stats.aggroRange * 2;
	    	if(npc.ais.getMovingType() == 1)
	    		allowedDistance += npc.ais.walkingRange;
	    	double distance = entity.distanceToSqr(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos());
	    	if(npc.ais.getMovingType() == 2){
	    		int[] arr = npc.ais.getCurrentMovingPath();
		    	distance = entity.distanceToSqr(arr[0], arr[1], arr[2]);
	    	}
	    	
	    	if(distance > allowedDistance * allowedDistance)
	    		return false;
    	}

    	if(npc.job.getType() == JobType.GUARD && ((JobGuard)npc.job).isEntityApplicable(entity))
    		return true;
    	
    	if(npc.role.getType() == RoleType.COMPANION){
    		RoleCompanion role = (RoleCompanion)npc.role;
    		if(role.companionJobInterface.getType() == EnumCompanionJobs.GUARD && ((CompanionGuard)role.companionJobInterface).isEntityApplicable(entity))
    			return true;
    	}
    	if(entity instanceof ServerPlayerEntity){
    		ServerPlayerEntity player = (ServerPlayerEntity) entity;
			return npc.faction.isAggressiveToPlayer(player) && !player.abilities.invulnerable;
		}

    	if(entity instanceof EntityNPCInterface){
    		if(((EntityNPCInterface)entity).isKilled())
    			return false;
    		if(npc.advanced.attackOtherFactions)
    			return npc.faction.isAggressiveToNpc((EntityNPCInterface)entity);
    	}
    	
        return false;
    }
	@Override
	public boolean apply(LivingEntity ob) {
		return isEntityApplicable(ob);
	}
}
