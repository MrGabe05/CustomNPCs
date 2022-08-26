package noppes.npcs.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public class EntityAIReturn extends Goal{
	public static final int MaxTotalTicks = 600;
    private final EntityNPCInterface npc;
    private int stuckTicks = 0;
    private int totalTicks = 0;
    private double endPosX;
    private double endPosY;
    private double endPosZ;
    private boolean wasAttacked = false;
    private double[] preAttackPos;
    private int stuckCount = 0;

    public EntityAIReturn(EntityNPCInterface npc){
        this.npc = npc;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse(){
    	if(npc.hasOwner() || npc.isPassenger() || !npc.ais.shouldReturnHome() || npc.isKilled() || !npc.getNavigation().isDone() || npc.isInteracting()){
    		return false;
    	}
    	
    	if (npc.ais.findShelter == 0 && (!npc.level.isDay() || npc.level.isRaining()) && !npc.level.dimensionType.hasSkyLight()){
    		BlockPos pos = new BlockPos(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos());
    		if (npc.level.canSeeSky(pos) || npc.level.getLightEmission(pos) <= 8){
                return false;
            }
        }
    	else if (npc.ais.findShelter == 1 && npc.level.isDay()){
    		BlockPos pos = new BlockPos(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos());
    		if (npc.level.canSeeSky(pos)){
                return false;
            }
    	}
    	
    	if(npc.isAttacking()){
    		if(!wasAttacked){
	    		wasAttacked = true;
	    		preAttackPos = new double[]{npc.getX(), npc.getY(), npc.getZ()};
    		}
    		return false;
    	}
    	if(!npc.isAttacking() && wasAttacked){
    		return true;
    	}
    	if(npc.ais.getMovingType() == 2 && npc.ais.distanceToSqrToPathPoint() < CustomNpcs.NpcNavRange * CustomNpcs.NpcNavRange)
    		return false;
    	
    	if(npc.ais.getMovingType() == 1){
    		double x = npc.getX() - npc.getStartXPos();
    		double z = npc.getZ() - npc.getStartZPos();
    		
    		return !npc.isInRange(npc.getStartXPos(), -1, npc.getStartZPos(), npc.ais.walkingRange);
    	}
        if(npc.ais.getMovingType() == 0)
        	return !this.npc.isVeryNearAssignedPlace();
        
        return false;
    }

    @Override
    public boolean canContinueToUse(){
    	if(npc.isFollower() || npc.isKilled() || npc.isAttacking() || npc.isVeryNearAssignedPlace() || npc.isInteracting() || npc.isPassenger())
    		return false;
    	if(npc.getNavigation().isDone() && wasAttacked && !isTooFar())
    		return false;
        return totalTicks <= MaxTotalTicks;
    }

    @Override
    public void tick(){
    	totalTicks++;
    	if(totalTicks > MaxTotalTicks){
			npc.setPos(endPosX, endPosY, endPosZ);
    		npc.getNavigation().stop();
			return;
    	}	
    	
    	if(stuckTicks > 0){
    		stuckTicks--;
    	}
    	else if(npc.getNavigation().isDone()){
    		stuckCount++;
    		stuckTicks = 10;
    		if(totalTicks > 30 && wasAttacked && isTooFar() || stuckCount > 5 ){
        		npc.setPos(endPosX, endPosY, endPosZ);
        		npc.getNavigation().stop();
    		}
    		else
            	navigate(stuckCount % 2 == 1);
    	}  
    	else{
    		stuckCount = 0;
    	}
    }
    private boolean isTooFar(){
    	int allowedDistance = npc.stats.aggroRange * 2;
    	if(npc.ais.getMovingType() == 1)
    		allowedDistance += npc.ais.walkingRange;

		double x = npc.getX() - endPosX;
		double z = npc.getZ() - endPosZ;
    	return x * x + z * z > allowedDistance * allowedDistance;
    }

    @Override
    public void start() {
    	stuckTicks = 0;
    	totalTicks = 0;
    	stuckCount = 0;
    	navigate(false);
    }
    
    private void navigate(boolean towards) {
    	if(!wasAttacked){
        	endPosX = npc.getStartXPos();
        	endPosY = npc.getStartYPos();
        	endPosZ = npc.getStartZPos();
    	}
    	else{
    		endPosX = preAttackPos[0];
    		endPosY = preAttackPos[1];
    		endPosZ = preAttackPos[2];
    	}
    	double posX = endPosX;
    	double posY = endPosY;
    	double posZ = endPosZ;
    	double range = MathHelper.sqrt(npc.distanceToSqr(posX, posY, posZ));
    	if(range > CustomNpcs.NpcNavRange || towards){
    		int distance = (int) range;
    		if(distance > CustomNpcs.NpcNavRange)
    			distance = CustomNpcs.NpcNavRange / 2;
    		else 
    			distance /= 2;
    		if(distance > 2){
	    		Vector3d start = new Vector3d(posX, posY, posZ);
	    		Vector3d pos = RandomPositionGenerator.getPosTowards(npc, distance / 2,distance /2 > 7? 7:distance/2, start);
	    		if(pos != null){
	    			posX = pos.x;
	    			posY = pos.y;
	    			posZ = pos.z;
	    		}
    		}
    	}
        npc.getNavigation().stop();
        npc.getNavigation().moveTo(posX, posY, posZ, 1);
	}

    @Override
	public void stop(){
    	wasAttacked = false;
        this.npc.getNavigation().stop();
    }
}
