package noppes.npcs.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;

public class EntityNpcAlex extends EntityCustomNpc {

    public EntityNpcAlex(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
        display.setSkinTexture("customnpcs:textures/entity/alex_skins/alex_2.png");
    }
}
