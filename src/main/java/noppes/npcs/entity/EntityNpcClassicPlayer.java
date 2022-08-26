package noppes.npcs.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;

public class EntityNpcClassicPlayer extends EntityCustomNpc {
    public EntityNpcClassicPlayer(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
        display.setSkinTexture("customnpcs:textures/entity/humanmale/steve.png");
    }
}
