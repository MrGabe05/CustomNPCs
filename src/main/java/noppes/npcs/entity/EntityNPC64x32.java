package noppes.npcs.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;

public class EntityNPC64x32 extends EntityCustomNpc{

	public EntityNPC64x32(EntityType<? extends CreatureEntity> type, World world) {
		super(type, world);
		display.setSkinTexture("customnpcs:textures/entity/humanmale/Steve64x32.png");
	}

}
