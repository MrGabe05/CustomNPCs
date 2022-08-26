package noppes.npcs.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;
import java.util.Iterator;

public class EntityAILook extends Goal{
    private final EntityNPCInterface npc;
    private int idle = 0;
    private double lookX;
    private double lookZ;
    
    private boolean forced = false;
    private Entity forcedEntity = null;

    public EntityAILook(EntityNPCInterface npc){
        this.npc = npc;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse(){
		if(npc.isAttacking() || !npc.getNavigation().isDone() || npc.isSleeping() || !npc.isAlive()) {
			return false;
		}
		if(npc.isInteracting() || forced || npc.ais.getStandingType() > 0 || idle > 0){
			return true;
		}
        return this.npc.getRandom().nextFloat() < 0.004F;
    }
    
    @Override
    public void start(){
    	if(npc.ais.getStandingType() == 0 || npc.ais.getStandingType() == 3){
			double var1 = (Math.PI * 2D) * this.npc.getRandom().nextDouble();
			if(npc.ais.getStandingType() == 3)
				var1 = Math.PI / 180 * npc.ais.orientation + Math.PI * 0.2 + Math.PI * 0.6 * this.npc.getRandom().nextDouble();
			this.lookX = Math.cos(var1);
			this.lookZ = Math.sin(var1);
			this.idle = 20 + this.npc.getRandom().nextInt(20);
		}

    }
    
    public void rotate(Entity entity){
    	forced = true;
		forcedEntity = entity;
    }
    
    public void rotate(int degrees){
    	forced = true;
		npc.yHeadRot = npc.yRot = npc.yBodyRot = degrees;
    }
    
    @Override
    public void stop(){
    	forced = false;
    	forcedEntity = null;
    }
    
    @Override
    public void tick(){
    	Entity lookat = null;
    	if(forced && forcedEntity != null){
    		lookat = forcedEntity;
    	}
    	else if(npc.isInteracting()){
    		Iterator<LivingEntity> ita = npc.interactingEntities.iterator();
    		double closestDistance = 12;
    		while(ita.hasNext()){
    			LivingEntity entity = ita.next();
    			double distance = entity.distanceToSqr(npc);
    			if(distance < closestDistance){
    				closestDistance = entity.distanceToSqr(npc);
    				lookat = entity;
    			}
    			else if(distance > 12)
    				ita.remove();
    		}
    	}
    	else if(npc.ais.getStandingType() == 2){
    		lookat = npc.level.getNearestPlayer(npc, 16);
    	}
    	
    	if(lookat != null){
			npc.getLookControl().setLookAt(lookat, 10F, npc.getMaxHeadXRot());
    		return;
    	}

		if(idle > 0){
			idle--;
			npc.getLookControl().setLookAt(npc.getX() + lookX, npc.getY() + npc.getEyeHeight(), npc.getZ() + lookZ, 10F, npc.getMaxHeadXRot());
		}
    	
    	if(npc.ais.getStandingType() == 1 && !forced){
    		npc.yHeadRot = npc.yRot = npc.yBodyRot = npc.ais.orientation;
    	}
    }
}
