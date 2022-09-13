package noppes.npcs.api.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityProjectile;

import java.lang.reflect.Field;

public class WrapperEntityData implements ICapabilityProvider{
	private static final Field capField;
	static{
		Field f = null;
		try {
			f = CapabilityProvider.class.getDeclaredField("capabilities");
			f.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		capField = f;
	}
	
	@CapabilityInject(WrapperEntityData.class)
	public static Capability<WrapperEntityData> ENTITYDATA_CAPABILITY = null;

	private final LazyOptional<WrapperEntityData> instance = LazyOptional.of(() -> this);
	
	public IEntity base;
	public WrapperEntityData(IEntity base){
		this.base = base;
	}

	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(capability == ENTITYDATA_CAPABILITY)
			return instance.cast();
		return LazyOptional.empty();
	}

	private static final WrapperEntityData backup = new WrapperEntityData(null);
	public static IEntity get(Entity entity){
		if(entity == null || entity.position() == Vector3d.ZERO)
			return null;
		try {
			CapabilityDispatcher dispatcher = (CapabilityDispatcher) capField.get(entity);
			if(dispatcher == null){
				LogWriter.warn("Unable to get EntityData for " + entity);
				return getData(entity).base;
			}
			WrapperEntityData data = dispatcher.getCapability(ENTITYDATA_CAPABILITY, null).orElse(backup);
			if(data == null || data == backup){//shouldnt happen, but does occasionally for unknown reasons
				LogWriter.warn("Unable to get EntityData for " + entity);
				return getData(entity).base;
			}
			return data.base;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final ResourceLocation key = new ResourceLocation("customnpcs", "entitydata");
	public static void register(net.minecraftforge.event.AttachCapabilitiesEvent<Entity> event) {
		event.addCapability(key, getData(event.getObject()));
	}
	private static WrapperEntityData getData(Entity entity) {
		if(entity == null || entity.level == null || entity.level.isClientSide)
			return null;
		
		if(entity instanceof ServerPlayerEntity)
			return new WrapperEntityData(new PlayerWrapper((ServerPlayerEntity) entity));
		else if(PixelmonHelper.isPixelmon(entity))
			return new WrapperEntityData(new PixelmonWrapper((AbstractHorseEntity)entity));
		else if(entity instanceof VillagerEntity)
			return new WrapperEntityData(new VillagerWrapper((VillagerEntity)entity));
		else if(entity instanceof AnimalEntity)
			return new WrapperEntityData(new AnimalWrapper((AnimalEntity) entity));
		else if(entity instanceof MonsterEntity)
			return new WrapperEntityData(new MonsterWrapper((MonsterEntity) entity));
		else if(entity instanceof MobEntity)
			return new WrapperEntityData(new EntityLivingWrapper((MobEntity)entity));
		else if(entity instanceof LivingEntity)
			return new WrapperEntityData(new EntityLivingBaseWrapper((LivingEntity) entity));
		else if(entity instanceof ItemEntity)
			return new WrapperEntityData(new EntityItemWrapper((ItemEntity)entity));
		else if(entity instanceof EntityProjectile)
			return new WrapperEntityData(new ProjectileWrapper((EntityProjectile)entity));
		else if(entity instanceof ThrowableEntity)
			return new WrapperEntityData(new ThrowableWrapper((ThrowableEntity)entity));
		else if(entity instanceof AbstractArrowEntity)
			return new WrapperEntityData(new ArrowWrapper((AbstractArrowEntity)entity));
		return new WrapperEntityData(new EntityWrapper(entity));

	}
}
