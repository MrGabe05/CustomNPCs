package noppes.npcs;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.constants.EnumParts;

import java.util.HashMap;


public class ModelDataShared{
	protected ModelPartConfig arm1 = new ModelPartConfig();
	protected ModelPartConfig arm2 = new ModelPartConfig();
	protected ModelPartConfig body = new ModelPartConfig();
	protected ModelPartConfig leg1 = new ModelPartConfig();
	protected ModelPartConfig leg2 = new ModelPartConfig();
	protected ModelPartConfig head = new ModelPartConfig();

	protected ModelPartData legParts = new ModelPartData("legs");
	public ModelEyeData eyes = new ModelEyeData();

	protected ResourceLocation entityName = null;
	protected LivingEntity entity;
	
	public CompoundNBT extra = new CompoundNBT();
	
	protected HashMap<EnumParts, ModelPartData> parts = new HashMap<EnumParts, ModelPartData>();
	
	public CompoundNBT save(){
		CompoundNBT compound = new CompoundNBT();

		if(entityName != null) {
			compound.putString("EntityName", entityName.toString());
		}

		compound.put("ArmsConfig", arm1.save());
		compound.put("BodyConfig", body.save());
		compound.put("LegsConfig", leg1.save());
		compound.put("HeadConfig", head.save());

		compound.put("LegParts", legParts.save());
		compound.put("Eyes", eyes.save());

		compound.put("ExtraData", extra);
		
		ListNBT list = new ListNBT();
		for(EnumParts e : parts.keySet()){
			CompoundNBT item = parts.get(e).save();
			item.putString("PartName", e.name);
			list.add(item);
		}
		compound.put("Parts", list);
		
		return compound;
	}
	
	public void load(CompoundNBT compound){
		setEntity(compound.getString("EntityName"));
		
		arm1.load(compound.getCompound("ArmsConfig"));
		body.load(compound.getCompound("BodyConfig"));
		leg1.load(compound.getCompound("LegsConfig"));
		head.load(compound.getCompound("HeadConfig"));

		legParts.load(compound.getCompound("LegParts"));
		eyes.load(compound.getCompound("Eyes"));

		extra = compound.getCompound("ExtraData");
				
		HashMap<EnumParts,ModelPartData> parts = new HashMap<EnumParts,ModelPartData>();
		ListNBT list = compound.getList("Parts", 10);
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT item = list.getCompound(i);
			String name = item.getString("PartName");
			ModelPartData part = new ModelPartData(name);
			part.load(item);
			EnumParts e = EnumParts.FromName(name);
			if(e != null)
				parts.put(e, part);
		}
		this.parts = parts;
		updateTransate();
	}

	private void updateTransate(){
		for(EnumParts part : EnumParts.values()){
			ModelPartConfig config = getPartConfig(part);
			if(config == null)
				continue;
			if(part == EnumParts.HEAD){
				config.setTranslate(0, getBodyY(), 0);
			}
			else if(part == EnumParts.ARM_LEFT){
				ModelPartConfig body = getPartConfig(EnumParts.BODY);
				float x = (1 - body.scaleX) * 0.25f + (1 - config.scaleX) * 0.075f;
				float y = getBodyY() + (1 - config.scaleY) * -0.1f;
				config.setTranslate(-x, y, 0);
				if(!config.notShared){
					ModelPartConfig arm = getPartConfig(EnumParts.ARM_RIGHT);
					arm.copyValues(config);
				}
			}
			else if(part == EnumParts.ARM_RIGHT){
				ModelPartConfig body = getPartConfig(EnumParts.BODY);
				float x = (1 - body.scaleX) * 0.25f + (1 - config.scaleX) * 0.075f;
				float y = getBodyY() + (1 - config.scaleY) * -0.1f;
				config.setTranslate(x, y, 0);
			}
			else if(part == EnumParts.LEG_LEFT){
				config.setTranslate(-(1 - config.scaleX) * 0.125f , getLegsY(),  -(1 - config.scaleZ) * 0.00625f);
				if(!config.notShared){
					ModelPartConfig leg = getPartConfig(EnumParts.LEG_RIGHT);
					leg.copyValues(config);
				}
			}
			else if(part == EnumParts.LEG_RIGHT){
				config.setTranslate((1 - config.scaleX) * 0.125f, getLegsY(),  -(1 - config.scaleZ) * 0.00625f);
			}
			else if(part == EnumParts.BODY){
				config.setTranslate(0, getBodyY(), 0);
			}
		}
	}

	public void setEntity(String name){
		if(name == null || name.isEmpty())
			this.entityName = null;
		else
			this.entityName = new ResourceLocation(name);
		entity = null;
		extra = new CompoundNBT();
	}

	public void setEntity(ResourceLocation name){
		this.entityName = name;
		entity = null;
		extra = new CompoundNBT();
	}

	public ResourceLocation getEntityName(){
		return entityName;
	}

	public boolean hasEntity(){
		return entityName != null;
	}

	public float offsetY() {
		if(entity == null)
			return -getBodyY();
		return entity.getBbHeight() - 1.8f;
	}

	public void clearEntity() {
		entity = null;
	}

	
	public ModelPartData getPartData(EnumParts type){
		if(type == EnumParts.LEGS)
			return legParts;
		if(type == EnumParts.EYES)
			return eyes;
		return parts.get(type);
	}
	
	public ModelPartConfig getPartConfig(EnumParts type){
    	if(type == EnumParts.BODY)
    		return body;
    	if(type == EnumParts.ARM_LEFT)
    		return arm1;
    	if(type == EnumParts.ARM_RIGHT)
    		return arm2;
    	if(type == EnumParts.LEG_LEFT)
    		return leg1;
    	if(type == EnumParts.LEG_RIGHT)
    		return leg2;
    	
    	return head;
	}

	public void removePart(EnumParts type) {
		parts.remove(type);
	}

	public ModelPartData getOrCreatePart(EnumParts type) {
		if(type == null)
			return null;
		if(type == EnumParts.EYES){
		    return eyes;
		}
		ModelPartData part = getPartData(type);
		if(part == null)
			parts.put(type, part = new ModelPartData(type.name));
		return part;
	}
	
	public float getBodyY(){
//		if(legParts.type == 3)
//			return (0.9f - body.scaleY) * 0.75f + getLegsY();
//		if(legParts.type == 3)
//			return (0.5f - body.scaleY) * 0.75f + getLegsY();
		return (1 - body.scaleY) * 0.75f + getLegsY();
	}

	public float getLegsY() {
		ModelPartConfig legs = leg1;
		if(leg2.notShared && leg2.scaleY > leg1.scaleY)
			legs = leg2;
//		if(legParts.type == 3)
//			return (0.87f - legs.scaleY) * 1f;
		return (1 - legs.scaleY) * 0.75f;
	}
}
