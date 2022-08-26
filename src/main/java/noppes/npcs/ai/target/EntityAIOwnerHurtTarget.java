package noppes.npcs.ai.target;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public class EntityAIOwnerHurtTarget extends TargetGoal
{
    EntityNPCInterface npc;
    LivingEntity theTarget;
    private int field_142050_e;

    public EntityAIOwnerHurtTarget(EntityNPCInterface npc)
    {
        super(npc, false);
        this.npc = npc;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse()
    {
        if (!this.npc.isFollower() || !npc.role.defendOwner())
        {
            return false;
        }
        else
        {
            LivingEntity entitylivingbase = this.npc.getOwner();

            if (entitylivingbase == null)
            {
                return false;
            }
            else
            {
                this.theTarget = entitylivingbase.getLastHurtMob();
                int i = entitylivingbase.getLastHurtMobTimestamp();
                return i != this.field_142050_e && this.canAttack(this.theTarget, EntityPredicate.DEFAULT);
            }
        }
    }

    @Override
    public void start()
    {
        this.npc.setTarget(this.theTarget);
        LivingEntity entitylivingbase = this.npc.getOwner();

        if (entitylivingbase != null)
        {
            this.field_142050_e = entitylivingbase.getLastHurtMobTimestamp();
        }

        super.start();
    }
}