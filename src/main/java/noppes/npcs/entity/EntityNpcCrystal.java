package noppes.npcs.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;
import noppes.npcs.ModelData;


public class EntityNpcCrystal extends EntityNPCInterface {

    public EntityNpcCrystal(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
		scaleX = 0.7f;
		scaleY = 0.7f;
		scaleZ = 0.7f;
		display.setSkinTexture("customnpcs:textures/entity/crystal/EnderCrystal.png");
    }
    
    @Override
    public void tick() {
    	removed = true;
    	setNoAi(true);
    	if(!level.isClientSide){
	    	CompoundNBT compound = new CompoundNBT();
	    	
	    	addAdditionalSaveData(compound);
	    	EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level);
	    	npc.readAdditionalSaveData(compound);
	    	ModelData data = npc.modelData;
			data.setEntity(CustomEntities.entityNpcCrystal.toString());


			level.addFreshEntity(npc);
    	}
        super.tick();
    }

}
