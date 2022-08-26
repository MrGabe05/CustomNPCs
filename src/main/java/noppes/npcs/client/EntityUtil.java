package noppes.npcs.client;

import net.minecraft.entity.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EntityUtil {

	public static void Copy(LivingEntity copied, LivingEntity entity){
		entity.level = copied.level;

		entity.deathTime = copied.deathTime;
		entity.walkDist = copied.walkDist;
		entity.walkDistO = copied.walkDist;
		entity.moveDist = copied.moveDist;

		entity.zza = copied.zza;
		entity.xxa = copied.xxa;
		entity.setOnGround(copied.isOnGround());
		entity.fallDistance = copied.fallDistance;
		entity.jumping = copied.jumping;

		List<EntityDataManager.DataEntry<? extends Object>> copiedData = copied.getEntityData().getAll();
		List<EntityDataManager.DataEntry<? extends Object>> data = entity.getEntityData().getAll();
		for(EntityDataManager.DataEntry<? extends Object> entry : copiedData){
			if(data.stream().anyMatch(e -> e.getAccessor() == entry.getAccessor())){
				entity.getEntityData().set((DataParameter<Object>)entry.getAccessor(), entry.getValue());
			}
		}

		entity.xo = copied.xo;
		entity.yo = copied.yo;
		entity.zo = copied.zo;

		entity.setPos(copied.getX(), copied.getY(), copied.getZ());
		//entity.setEntityBoundingBox(copied.getEntityBoundingBox());

		entity.xOld = copied.xOld;
		entity.yOld = copied.yOld;
		entity.zOld = copied.zOld;

		entity.setDeltaMovement(copied.getDeltaMovement());

		entity.xRot = copied.xRot;
		entity.yRot = copied.yRot;
		entity.xRotO = copied.xRotO;
		entity.yRotO = copied.yRotO;
		entity.yHeadRot = copied.yHeadRot;
		entity.yHeadRotO = copied.yHeadRotO;
		entity.yBodyRot = copied.yBodyRot;
		entity.yBodyRotO = copied.yBodyRotO;

		entity.useItemRemaining = copied.useItemRemaining;

		entity.animationPosition = copied.animationPosition;
		entity.animStep = copied.animStep;
		entity.animStepO = copied.animStepO;
		entity.swimAmount = copied.swimAmount;
		entity.swimAmountO = copied.swimAmountO;
		entity.swinging = copied.swinging;
		entity.swingTime = copied.swingTime;

		entity.animationSpeed = copied.animationSpeed;
		entity.animationSpeedOld = copied.animationSpeedOld;
		entity.attackAnim = copied.attackAnim;
		entity.oAttackAnim = copied.oAttackAnim;

		entity.tickCount = copied.tickCount;

		entity.setHealth(Math.min(copied.getHealth(), entity.getMaxHealth()));

		entity.getPersistentData().merge(copied.getPersistentData());

		//if(entity.getVehicle() != copied.getVehicle())
		//	entity.vehicle = copied.vehicle;

		if(entity instanceof PlayerEntity && copied instanceof PlayerEntity){
			PlayerEntity ePlayer = (PlayerEntity) entity;
			PlayerEntity cPlayer = (PlayerEntity) copied;

			ePlayer.bob = cPlayer.bob;
			ePlayer.oBob = cPlayer.oBob;

			ePlayer.xCloakO = cPlayer.xCloakO;
			ePlayer.yCloakO = cPlayer.yCloakO;
			ePlayer.zCloakO = cPlayer.zCloakO;
			ePlayer.xCloak = cPlayer.xCloak;
			ePlayer.yCloak = cPlayer.yCloak;
			ePlayer.zCloak = cPlayer.zCloak;
		}
		for(EquipmentSlotType slot : EquipmentSlotType.values()){
			entity.setItemSlot(slot, copied.getItemBySlot(slot));
		}

		if(entity instanceof EnderDragonEntity){
			entity.xRot += 180;
		}

		entity.removed = copied.removed;
		entity.deathTime = copied.deathTime;
		
		entity.tickCount = copied.tickCount;

		if(entity instanceof EnderDragonEntity){
			entity.yRot += 180;
		}
		if(entity instanceof ChickenEntity){
			((ChickenEntity)entity).flap = copied.isOnGround()?0:1;
		}
		
		for(EquipmentSlotType slot : EquipmentSlotType.values()){
			entity.setItemSlot(slot, copied.getItemBySlot(slot));
		}
		
		if(copied instanceof EntityNPCInterface && entity instanceof EntityNPCInterface){
			EntityNPCInterface npc = (EntityNPCInterface) copied;
			EntityNPCInterface target = (EntityNPCInterface) entity;

			target.textureLocation = npc.textureLocation;
			target.textureGlowLocation = npc.textureGlowLocation;
			target.textureCloakLocation = npc.textureCloakLocation;
			target.display = npc.display;
			target.inventory = npc.inventory;
			if(npc.job.getType() == JobType.PUPPET){
				target.job = npc.job;
			}
			if(target.currentAnimation != npc.currentAnimation){
				target.currentAnimation = npc.currentAnimation;
				npc.refreshDimensions();
			}
			target.setDataWatcher(npc.getEntityData());
		}

		if(entity instanceof EntityCustomNpc && copied instanceof EntityCustomNpc){
			EntityCustomNpc npc = (EntityCustomNpc) copied;
			EntityCustomNpc target = (EntityCustomNpc) entity;
			
			target.modelData = npc.modelData.copy();
			target.modelData.setEntity((String)null);
		}
	}

	private <T> void setData(LivingEntity entity, List<EntityDataManager.DataEntry<T>> copiedData, List<EntityDataManager.DataEntry<T>> data ){
		for(EntityDataManager.DataEntry<? extends Object> entry : copiedData){
			if(data.stream().anyMatch(e -> e.getAccessor() == entry.getAccessor())){
				entity.getEntityData().set((DataParameter<T>)entry.getAccessor(), (T)entry.getValue());
			}
		}
	}

	public static void setRecentlyHit(LivingEntity entity){
		entity.lastHurtByPlayerTime = 100;
	}

	private static HashMap<EntityType<? extends Entity>, Class> entityClasses = new HashMap<EntityType<? extends Entity>, Class>();
	public static HashMap<EntityType<? extends Entity>, Class> getAllEntitiesClasses(World level){
		if(!entityClasses.isEmpty())
			return entityClasses;
		HashMap<EntityType<? extends Entity>, Class> data = new HashMap<EntityType<? extends Entity>, Class>();

		for(EntityType<? extends Entity> ent : ForgeRegistries.ENTITIES.getValues()){
			try {
				Entity e = ent.create(level);
				if(e != null){
					if(LivingEntity.class.isAssignableFrom(e.getClass())){
						data.put(ent, e.getClass());
					}
					e.remove();
					e.removed = true;
				}
			}
			catch(Exception e){

			}
		}
		return entityClasses = data;
	}
	public static HashMap<EntityType<? extends Entity>, Class> getAllEntitiesClassesNoNpcs(World level){
		HashMap<EntityType<? extends Entity>, Class> data = new HashMap<EntityType<? extends Entity>, Class>(getAllEntitiesClasses(level));
		Iterator<Map.Entry<EntityType<? extends Entity>, Class>> ita = data.entrySet().iterator();
		while(ita.hasNext()){
			Map.Entry<EntityType<? extends Entity>, Class> entry = ita.next();
			if(EntityNPCInterface.class.isAssignableFrom(entry.getValue()) || !LivingEntity.class.isAssignableFrom(entry.getValue())){
				ita.remove();
			}
		}
		return data;
	}

	public static HashMap<String, ResourceLocation> getAllEntities(World level, boolean withNpcs){
		HashMap<String, ResourceLocation> data = new HashMap<String, ResourceLocation>();

		for(EntityType<? extends Entity> ent : ForgeRegistries.ENTITIES.getValues()){
			try {
				Entity e = ent.create(level);
				if(e != null){
					if(LivingEntity.class.isAssignableFrom(e.getClass()) && (withNpcs || !EntityNPCInterface.class.isAssignableFrom(e.getClass()))){
						data.put(ent.getDescriptionId(), ent.getRegistryName());
					}
					e.remove();
					e.removed = true;
				}
			}
			catch(Throwable e){
				LogWriter.except(e);
			}
		}

		return data;
	}
}
