package noppes.npcs.ai.target;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public class EntityAIOwnerHurtByTarget extends TargetGoal{
    EntityNPCInterface npc;
    LivingEntity theOwnerAttacker;
    private int timer;

    public EntityAIOwnerHurtByTarget(EntityNPCInterface npc){
        super(npc, false);
        this.npc = npc;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse(){
        if (!this.npc.isFollower() || !npc.role.defendOwner()){
            return false;
        }
        else{
            LivingEntity entitylivingbase = this.npc.getOwner();

            if (entitylivingbase == null){
                return false;
            }
            else{
                this.theOwnerAttacker = entitylivingbase.getLastHurtByMob();
                int i = entitylivingbase.getLastHurtByMobTimestamp();
                return i != this.timer && this.canAttack(this.theOwnerAttacker, EntityPredicate.DEFAULT);
            }
        }
    }

    @Override
    public void start(){
        this.npc.setTarget(this.theOwnerAttacker);
        LivingEntity entitylivingbase = this.npc.getOwner();

        if (entitylivingbase != null){
            this.timer = entitylivingbase.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}