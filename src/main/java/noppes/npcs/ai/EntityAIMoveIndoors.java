package noppes.npcs.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Random;

public class EntityAIMoveIndoors extends Goal
{
    private CreatureEntity theCreature;
    private double shelterX;
    private double shelterY;
    private double shelterZ;
    private World level;

    public EntityAIMoveIndoors(CreatureEntity par1CreatureEntity)
    {
        this.theCreature = par1CreatureEntity;
        this.level = par1CreatureEntity.level;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse(){
    	if(this.theCreature.level.isDay() && !this.theCreature.level.isRaining() || theCreature.level.dimensionType.hasSkyLight())
    		return false;
    	BlockPos pos =  new BlockPos(theCreature.getX(), this.theCreature.getBoundingBox().minY, theCreature.getZ());

		if (!this.level.canSeeSky(pos) && this.level.getLightEmission(pos) > 8){
            return false;
        }

        Vector3d var1 = this.findPossibleShelter();
        if (var1 == null){
            return false;
        }
        this.shelterX = var1.x;
        this.shelterY = var1.y;
        this.shelterZ = var1.z;
        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        return !this.theCreature.getNavigation().isDone();
    }

    @Override
    public void start()
    {
        this.theCreature.getNavigation().moveTo(this.shelterX, this.shelterY, this.shelterZ, 1.0D);
    }

    private Vector3d findPossibleShelter(){
        Random random = this.theCreature.getRandom();
        BlockPos blockpos = new BlockPos(this.theCreature.getX(), this.theCreature.getBoundingBox().minY, this.theCreature.getZ());

        for (int i = 0; i < 10; ++i){
            BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

            if (!this.level.canSeeSky(blockpos1) && this.theCreature.getWalkTargetValue(blockpos1) < 0.0F)
            {
                return new Vector3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
            }
        }

        return null;
    }
}
