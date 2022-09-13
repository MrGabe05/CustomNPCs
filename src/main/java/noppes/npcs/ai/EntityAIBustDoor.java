package noppes.npcs.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.InteractDoorGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class EntityAIBustDoor extends InteractDoorGoal
{
    private int breakingTime;
    private int field_75358_j = -1;

    public EntityAIBustDoor(MobEntity par1EntityLiving){
        super(par1EntityLiving);
    }

    @Override
    public boolean canUse(){
        return super.canUse() && !isOpen();
    }

    @Override
    public void start(){
        super.start();
        this.breakingTime = 0;
    }

    @Override
    public boolean canContinueToUse(){
        return this.breakingTime <= 240 && !isOpen() && this.mob.blockPosition().distSqr(doorPos) < 4.0D;
    }

    @Override
    public void stop(){
        super.stop();
        this.mob.level.destroyBlockProgress(this.mob.getId(), this.doorPos, -1);
    }

    @Override
    public void tick(){
        super.tick();

        if (this.mob.getRandom().nextInt(20) == 0){
            this.mob.level.levelEvent(null, 1010, this.doorPos, 0);
            this.mob.swing(Hand.MAIN_HAND);
        }

        ++this.breakingTime;
        int var1 = (int)((float)this.breakingTime / 240.0F * 10.0F);

        if (var1 != this.field_75358_j){
            this.mob.level.destroyBlockProgress(this.mob.getId(), this.doorPos, var1);
            this.field_75358_j = var1;
        }

        if (this.breakingTime == 240){
            this.mob.level.removeBlock(this.doorPos, false);
            this.mob.level.levelEvent(null, 1012, this.doorPos, 0);
            this.mob.level.levelEvent(null, 2001, this.doorPos, Block.getId(this.mob.level.getBlockState(this.doorPos)));
        }
    }
}
