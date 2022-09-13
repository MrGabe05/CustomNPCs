package noppes.npcs.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class EntityAIPanic extends Goal
{
    private final CreatureEntity entityCreature;
    private final float speed;
    private double randPosX;
    private double randPosY;
    private double randPosZ;

    public EntityAIPanic(CreatureEntity par1CreatureEntity, float limbSwingAmount)
    {
        this.entityCreature = par1CreatureEntity;
        this.speed = limbSwingAmount;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse(){
        if (this.entityCreature.getTarget() == null && !this.entityCreature.isOnFire()){
            return false;
        }
        else
        {
            Vector3d var1 = RandomPositionGenerator.getLandPos(this.entityCreature, 5, 4);

            if (var1 == null){
                return false;
            }
            else{
				this.randPosX = var1.x;
                this.randPosY = var1.y;
                this.randPosZ = var1.z;
                return true;
            }
        }
    }

    @Override
    public void start(){
        this.entityCreature.getNavigation().moveTo(this.randPosX, this.randPosY, this.randPosZ, this.speed);
    }

    @Override
    public boolean canContinueToUse(){
    	if(this.entityCreature.getTarget() == null)
    		return false;
        return !this.entityCreature.getNavigation().isDone();
    }
}