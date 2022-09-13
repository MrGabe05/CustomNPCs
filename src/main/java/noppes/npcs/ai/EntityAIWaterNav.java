package noppes.npcs.ai;

import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIWaterNav extends Goal
{
    private final EntityNPCInterface entity;

    public EntityAIWaterNav(EntityNPCInterface npc) {
        this.entity = npc;
        npc.getNavigation().setCanFloat(true);
    }

    @Override
    public boolean canUse(){
        if (this.entity.isInWater() || this.entity.isInLava()){
        	if (this.entity.ais.canSwim){
        		return true;
        	}
            else {
                return this.entity.horizontalCollision;
            }
        }
        return false;
    }

    @Override
    public void tick() {
        if (this.entity.getRandom().nextFloat() < 0.8F) {
            this.entity.getJumpControl().jump();
        }
    }
}
