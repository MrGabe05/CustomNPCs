package noppes.npcs.ai.target;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import noppes.npcs.entity.EntityNPCInterface;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class NpcNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    public NpcNearestAttackableTargetGoal(EntityNPCInterface npc, Class<T> c, int range, boolean b, boolean b2, @Nullable Predicate<LivingEntity> selector) {
        super(npc, c, range, b, b2, selector);
        if(npc.ais.attackInvisible){
            this.targetConditions.ignoreInvisibilityTesting();
        }
    }
}
