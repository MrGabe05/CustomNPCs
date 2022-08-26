package noppes.npcs;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LogWriter;

import java.lang.reflect.Method;

public class ModelData extends ModelDataShared{

	public boolean simpleRender = false;
	
	public LivingEntity getEntity(EntityNPCInterface npc){
		if(!hasEntity())
			return null;
		if(entity == null){
			try {
				entity = (LivingEntity) ForgeRegistries.ENTITIES.getValue(getEntityName()).create(npc.level);
				CompoundNBT comp = new CompoundNBT();
				entity.addAdditionalSaveData(comp);
				if(PixelmonHelper.isPixelmon(entity) && !extra.contains("Name")){
					extra.putString("Name", "abra");
				}
				comp = comp.merge(extra);
				try {
					entity.readAdditionalSaveData(comp);
					if(PixelmonHelper.isPixelmon(entity)){
						PixelmonHelper.initEntity(entity, extra.getString("Name"));
					}
				}
				catch (Exception e) {
					LogWriter.except(e);
				} 

				entity.setInvulnerable(true);
				entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(npc.getMaxHealth());
				for(EquipmentSlotType slot : EquipmentSlotType.values()){
					entity.setItemSlot(slot, npc.getItemBySlot(slot));
				}
			} catch (Exception e) {
				LogWriter.except(e);
			}
		}
		return entity;
	}
	
	public ModelData copy(){
		ModelData data = new ModelData();
		data.load(this.save());
		return data;
	}

	@Override
	public CompoundNBT save() {
		CompoundNBT compound = super.save();
		compound.putBoolean("SimpleRender", simpleRender);
		return compound;
	}

	@Override
	public void load(CompoundNBT compound){
		super.load(compound);
		simpleRender = compound.getBoolean("SimpleRender");
	}
	
	public void setExtra(LivingEntity entity, String key, String value){
		key = key.toLowerCase();

		if(key.equals("breed") && entity.getEncodeId().equals("tgvstyle.Dog")){
			try {
				Method method = entity.getClass().getMethod("getBreedID");
				Enum breed = (Enum) method.invoke(entity);				
				method = entity.getClass().getMethod("setBreedID", breed.getClass());
				method.invoke(entity, breed.getClass().getEnumConstants()[Integer.parseInt(value)]);
				CompoundNBT comp = new CompoundNBT();
				entity.readAdditionalSaveData(comp);
				extra.putString("EntityData21", comp.getString("EntityData21"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    	if(key.equalsIgnoreCase("name") && PixelmonHelper.isPixelmon(entity)){
    		extra.putString("Name", value);
    	}
		clearEntity();
	}
}
