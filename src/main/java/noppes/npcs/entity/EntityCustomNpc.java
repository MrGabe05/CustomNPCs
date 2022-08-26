package noppes.npcs.entity;

import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartData;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.constants.EnumParts;

public class EntityCustomNpc extends EntityNPCFlying{
	public ModelData modelData = new ModelData();

	public EntityCustomNpc(EntityType<? extends CreatureEntity> type, World world) {
		super(type, world);
		if(!CustomNpcs.EnableDefaultEyes) {
			modelData.eyes.type = -1;
		}
	}

    @Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		if(compound.contains("NpcModelData"))
			modelData.load(compound.getCompound("NpcModelData"));
		super.readAdditionalSaveData(compound);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.put("NpcModelData", modelData.save());
	}

	@Override
    public boolean saveAsPassenger(CompoundNBT compound){
    	boolean bo = super.saveAsPassenger(compound);
    	if(bo){
    		String s = getEncodeId();
    		if(s.equals("minecraft:customnpcs.customnpc")){
    			compound.putString("id", "customnpcs:customnpc");
    		}
    	}
    	return bo;
    }

	@Override
    public void tick(){
    	super.tick();
    	if(isClientSide()){
	        ModelPartData particles = modelData.getPartData(EnumParts.PARTICLES);
	    	if(particles != null && !isKilled()){
	    		CustomNpcs.proxy.spawnParticle(this, "ModelData", modelData, particles);
	    	}
	    	LivingEntity entity = modelData.getEntity(this);
	    	if(entity != null){
	    		try{
	    			entity.tick();
	    		}
	    		catch(Exception e){
	    		}
				EntityUtil.Copy(this, entity);
	    	}
    	}
    	modelData.eyes.update(this);
    }

	@Override
    public boolean startRiding(Entity par1Entity, boolean force){
    	boolean b = super.startRiding(par1Entity, force);
    	refreshDimensions();
    	return b;
    }

	@Override
	public void refreshDimensions() {
		Entity entity = modelData.getEntity(this);
		if(entity != null){
			entity.refreshDimensions();
		}
		super.refreshDimensions();
	}
	
	@Override
	public EntitySize getDimensions(Pose pos) {
		Entity entity = modelData.getEntity(this);
		if(entity == null){
			float height = 1.9f - modelData.getBodyY() + (modelData.getPartConfig(EnumParts.HEAD).scaleY - 1) / 2;
			if(baseSize.height != height){
				baseSize = new EntitySize(baseSize.width, height, false);
			}
			return super.getDimensions(pos);
		}
		else{
			EntitySize size = entity.getDimensions(pos);
			if(entity instanceof EntityNPCInterface){
				return size;
			}

			float width = (size.width / 5f) * display.getSize();
			float height = (size.height / 5f) * display.getSize();

			if(width < 0.1f)
				width = 0.1f;
			if(height < 0.1f)
				height = 0.1f;
			if(display.getHitboxState() == 1 || isKilled() && stats.hideKilledBody) {
				width = 0.00001f;
			}

			if(width / 2 > level.getMaxEntityRadius()) {
				level.increaseMaxEntityRadius(width / 2);
			}
			//this.setPos(posX, posY, posZ);
			return new EntitySize(width, height, false);
		}
	}
}
	