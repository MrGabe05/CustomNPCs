package noppes.npcs.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;
import noppes.npcs.ModelData;
import noppes.npcs.api.constants.AnimationType;

public class EntityNPCGolem extends EntityNPCInterface
{

    public EntityNPCGolem(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
        display.setSkinTexture("customnpcs:textures/entity/golem/Iron Golem.png");
		this.baseSize = new EntitySize(1.4f, 2.5f, false);
    }

    @Override
	public EntitySize getDimensions(Pose pos) {
		currentAnimation = entityData.get(Animation);
		if(currentAnimation == AnimationType.SLEEP){
			return new EntitySize(0.5f, 0.5f, false);
		}
		else if (currentAnimation == AnimationType.SIT){
			return new EntitySize(1.4f, 2f, false);
		}
		else{
			return new EntitySize(1.4f, 2.5f, false);
		}
	}
    
    @Override
    public void tick(){
    	removed = true;
    	setNoAi(true);

    	if(!level.isClientSide){
	    	CompoundNBT compound = new CompoundNBT();
	    	
	    	addAdditionalSaveData(compound);
	    	EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level);
	    	npc.readAdditionalSaveData(compound);
	    	ModelData data = npc.modelData;
			data.setEntity(CustomEntities.entityNPCGolem.toString());

			level.addFreshEntity(npc);
    	}
        super.tick();
    }
}