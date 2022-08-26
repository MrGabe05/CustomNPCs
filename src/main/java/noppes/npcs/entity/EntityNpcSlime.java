package noppes.npcs.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;
import noppes.npcs.ModelData;

public class EntityNpcSlime extends EntityNPCInterface{
	public EntityNpcSlime(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
		scaleX = 2f;
		scaleY = 2f;
		scaleZ = 2f;
		display.setSkinTexture("customnpcs:textures/entity/slime/Slime.png");
		this.baseSize = new EntitySize(0.8f, 0.8f, false);
	}    
	
	@Override
	public EntitySize getDimensions(Pose pos) {
		return new EntitySize(0.8f, 0.8f, false);
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
			data.setEntity(CustomEntities.entityNpcSlime.toString());

			level.addFreshEntity(npc);
    	}
        super.tick();
    }
}
