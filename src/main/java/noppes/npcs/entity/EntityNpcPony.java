package noppes.npcs.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;
import noppes.npcs.ModelData;

public class EntityNpcPony extends EntityNPCInterface
{
    public boolean isPegasus = false;
    public boolean isUnicorn = false;
    public boolean isFlying = false;
    
    public ResourceLocation checked = null;
    
    public EntityNpcPony(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
        display.setSkinTexture("customnpcs:textures/entity/ponies/MineLP Derpy Hooves.png");
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
            data.setEntity(CustomEntities.entityNpcPony.toString());

            level.addFreshEntity(npc);
    	}
        super.tick();
    }
    
}
