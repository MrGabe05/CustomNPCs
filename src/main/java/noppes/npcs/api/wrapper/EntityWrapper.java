package noppes.npcs.api.wrapper;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.api.*;
import noppes.npcs.api.constants.EntitiesType;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityItem;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.ServerCloneController;

import java.util.*;

public class EntityWrapper<T extends Entity> implements IEntity{
	protected T entity;
	private final Map<String, Object> tempData = new HashMap<String, Object>();
	private IWorld levelWrapper;
	

	private final IData tempdata = new IData() {

		@Override
		public void put(String key, Object value) {
			tempData.put(key, value);
		}

		@Override
		public Object get(String key) {
			return tempData.get(key);
		}

		@Override
		public void remove(String key) {
			tempData.remove(key);
		}

		@Override
		public boolean has(String key) {
			return tempData.containsKey(key);
		}

		@Override
		public void clear() {
			tempData.clear();
		}

		@Override
		public String[] getKeys() {
			return tempData.keySet().toArray(new String[tempData.size()]);
		}
		
	};
	

	private final IData storeddata = new IData() {

		@Override
		public void put(String key, Object value) {
			CompoundNBT compound = getStoredCompound();
			if(value instanceof Number){
				compound.putDouble(key, ((Number) value).doubleValue());
			}
			else if(value instanceof String)
				compound.putString(key, (String)value);
			saveStoredCompound(compound);
		}

		@Override
		public Object get(String key) {
			CompoundNBT compound = getStoredCompound();
			if(!compound.contains(key))
				return null;
			INBT base = compound.get(key);
			if(base instanceof NumberNBT)
				return ((NumberNBT)base).getAsDouble();
			return base.getAsString();
		}

		@Override
		public void remove(String key) {
			CompoundNBT compound = getStoredCompound();
			compound.remove(key);
			saveStoredCompound(compound);
		}

		@Override
		public boolean has(String key) {
			return getStoredCompound().contains(key);
		}

		@Override
		public void clear() {
			entity.getPersistentData().remove("CNPCStoredData");
		}
		
		private CompoundNBT getStoredCompound(){
			CompoundNBT compound = entity.getPersistentData().getCompound("CNPCStoredData");
			if(compound == null)
				entity.getPersistentData().put("CNPCStoredData", compound = new CompoundNBT());
			return compound;
		}
		
		private void saveStoredCompound(CompoundNBT compound){
			entity.getPersistentData().put("CNPCStoredData", compound);
		}

		@Override
		public String[] getKeys() {
			CompoundNBT compound = getStoredCompound();
			return compound.getAllKeys().toArray(new String[compound.getAllKeys().size()]);
		}
	};
	
	public EntityWrapper(T entity){
		this.entity = entity;
		this.levelWrapper = NpcAPI.Instance().getIWorld((ServerWorld)entity.level);
	}
	@Override
	public double getX() {
		return entity.getX();
	}

	@Override
	public void setX(double x) {
		entity.setPos(x, entity.getY(), entity.getZ());
	}

	@Override
	public double getY() {
		return entity.getY();
	}

	@Override
	public void setY(double y) {
		entity.setPos(entity.getX(), y, entity.getZ());
	}

	@Override
	public double getZ() {
		return entity.getZ();
	}

	@Override
	public void setZ(double z) {
		entity.setPos(entity.getX(), entity.getY(), z);
	}

	@Override
	public int getBlockX() {
		return MathHelper.floor(entity.getX());
	}

	@Override
	public int getBlockY() {
		return MathHelper.floor(entity.getY());
	}

	@Override
	public int getBlockZ() {
		return MathHelper.floor(entity.getZ());
	}

	@Override
	public String getEntityName() {
        String s = entity.getType().getDescriptionId();
        return LanguageMap.getInstance().getOrDefault(s);
	}
	
	@Override
	public String getName() {
		return entity.getName().getString();
	}

	@Override
	public void setName(String name) {
		entity.setCustomName(new StringTextComponent(name));
	}

	@Override
	public boolean hasCustomName() {
		return entity.hasCustomName();
	}
	
	@Override
	public void setPosition(double x, double y, double z) {
		entity.setPos(x, y, z);
	}
	
	@Override
	public IWorld getWorld() {
		if(entity.level != levelWrapper.getMCWorld())
			this.levelWrapper = NpcAPI.Instance().getIWorld((ServerWorld) entity.level);
		return levelWrapper;
	}

	@Override
	public boolean isAlive(){
		return entity.isAlive();
	}

	@Override
	public IData getTempdata(){
		return tempdata;
	}

	@Override
	public IData getStoreddata(){
		return storeddata;
	}

	@Override
	public long getAge(){
		return entity.tickCount;
	}

	@Override
	public void damage(float amount){
		entity.hurt(DamageSource.GENERIC, amount);
	}

	@Override
	public void despawn(){
		entity.removed = true;
	}

	@Override
	public void spawn() {
		if(levelWrapper.getMCWorld().getEntity(entity.getUUID()) != null)
			throw new CustomNPCsException("Entity is already spawned");
		entity.removed = false;
		levelWrapper.getMCWorld().addFreshEntity(entity);
	}

	@Override
	public void kill(){
		entity.remove();
	}

	@Override
	public boolean inWater(){
		return entity.level.getBlockStates(entity.getBoundingBox()).anyMatch((state) -> state.getMaterial() == Material.WATER);
	}

	@Override
	public boolean inLava(){
		return entity.level.getBlockStates(entity.getBoundingBox()).anyMatch((state) -> state.getMaterial() == Material.LAVA);
	}

	@Override
	public boolean inFire(){
		return entity.level.getBlockStates(entity.getBoundingBox()).anyMatch((state) -> state.getMaterial() == Material.FIRE);
	}
	
	@Override
	public boolean isBurning(){
		return entity.isOnFire();
	}

	@Override
	public void setBurning(int ticks){
		entity.setRemainingFireTicks(ticks);
	}

	@Override
	public void extinguish(){
		entity.clearFire();
	}

	@Override
	public String getTypeName(){
		return entity.getEncodeId();
	}

	@Override
	public IEntityItem dropItem(IItemStack item){
		return (IEntityItem) NpcAPI.Instance().getIEntity(entity.spawnAtLocation(item.getMCItemStack(), 0));
	}

	@Override
	public IEntity[] getRiders(){
		List<Entity> list = entity.getPassengers();
		IEntity[] riders = new IEntity[list.size()];
		for(int i = 0; i < list.size(); i++){
			riders[i] = NpcAPI.Instance().getIEntity(list.get(i));
		}
		return riders;
	}

	@Override
	public IRayTrace rayTraceBlock(double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox) {
        Vector3d vec3d = entity.getEyePosition(1);
        Vector3d vec3d1 = entity.getViewVector(1);
        Vector3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
		RayTraceResult result = entity.level.clip(new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.OUTLINE, stopOnLiquid ? RayTraceContext.FluidMode.ANY: RayTraceContext.FluidMode.NONE, entity));
		if(result.getType() == RayTraceResult.Type.MISS)
        	return null;
		BlockRayTraceResult br = (BlockRayTraceResult) result;
        return new RayTraceWrapper(NpcAPI.Instance().getIBlock(entity.level, br.getBlockPos()), br.getDirection().get3DDataValue());
	}

	@Override
	public IEntity[] rayTraceEntities(double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox) {
        Vector3d vec3d = entity.getEyePosition(1);
        Vector3d vec3d1 = entity.getViewVector(1);
        Vector3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
        //RayTraceResult result = entity.level.clip(vec3d, vec3d2, stopOnLiquid ? RayTraceFluidMode.ALWAYS: RayTraceContext.FluidMode.NONE, ignoreBlockWithoutBoundingBox, false);
		RayTraceResult result = entity.level.clip(new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.COLLIDER, stopOnLiquid ? RayTraceContext.FluidMode.ANY: RayTraceContext.FluidMode.NONE, entity));
		if(result.getType() != RayTraceResult.Type.MISS) {
			vec3d2 = result.getLocation();
		}
        return this.findEntityOnPath(distance, vec3d, vec3d2);
	}
	
	private IEntity[] findEntityOnPath(double distance, Vector3d vec3d, Vector3d vec3d1) {

        List<Entity> list = entity.level.getEntities(entity, entity.getBoundingBox().inflate(distance));

        List<IEntity> result = new ArrayList<IEntity>();
        for (Entity entity1 : list){
            if (entity1.canBeCollidedWith() && entity1 != this.entity){
                AxisAlignedBB axisalignedbb = entity1.getBoundingBox().inflate(entity1.getPickRadius());

				Optional<Vector3d> optional = axisalignedbb.clip(vec3d, vec3d1);
				if (optional.isPresent()) {
                    result.add(NpcAPI.Instance().getIEntity(entity1));
                }
                
            }
        }
        result.sort((o1, o2) -> {
            double d1 = EntityWrapper.this.entity.distanceToSqr(o1.getMCEntity());
            double d2 = EntityWrapper.this.entity.distanceToSqr(o2.getMCEntity());
            if (d1 == d2)
                return 0;
            return d1 > d2 ? 1 : -1;
        });
        return result.toArray(new IEntity[result.size()]);
	}

	@Override
	public IEntity[] getAllRiders(){
		List<Entity> list = new ArrayList<Entity>(entity.getIndirectPassengers());
		IEntity[] riders = new IEntity[list.size()];
		for(int i = 0; i < list.size(); i++){
			riders[i] = NpcAPI.Instance().getIEntity(list.get(i));
		}
		return riders;
	}

	@Override
	public void addRider(IEntity entity){
		if(entity != null){
			entity.getMCEntity().startRiding(this.entity, true);
		}
	}

	@Override
	public void clearRiders(){
		entity.ejectPassengers();
	}
	
	@Override
	public IEntity getMount(){
		return NpcAPI.Instance().getIEntity(entity.getVehicle());
	}
	
	@Override
	public void setMount(IEntity entity){
		if(entity == null)
			this.entity.stopRiding();
		else {
			this.entity.startRiding(entity.getMCEntity(), true);
		}
	}

	@Override
	public void setRotation(float rotation){
		entity.yRot = rotation;
	}

	@Override
	public float getRotation(){
		return entity.yRot;
	}

	@Override
	public void setPitch(float rotation){
		entity.xRot = rotation;
	}

	@Override
	public float getPitch(){
		return entity.xRot;
	}

	@Override
	public void knockback(int power, float direction){
		float v = direction * (float)Math.PI / 180.0F;
        entity.push(-MathHelper.sin(v) * (float)power, 0.1D + power * 0.04f, MathHelper.cos(v) * (float)power);
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.6, 1, 0.6));
        entity.hurtMarked = true;
	}

	@Override
	public boolean isSneaking(){
		return entity.isCrouching();
	}

	@Override
	public boolean isSprinting(){
		return entity.isSprinting();
	}

	@Override
	public T getMCEntity(){
		return entity;
	}

	@Override
	public int getType() {
		return EntitiesType.UNKNOWN;
	}

	@Override
	public boolean typeOf(int type){
		return type == getType();
	}
	
	@Override
	public String getUUID() {
		return entity.getUUID().toString();
	}
	
	@Override
	public String generateNewUUID() {
		UUID id = UUID.randomUUID();
		entity.setUUID(id);
		return id.toString();
	}
	
	@Override
	public INbt getNbt() {
		return NpcAPI.Instance().getINbt(entity.getPersistentData());
	}
	
	@Override
	public void storeAsClone(int tab, String name) {
		CompoundNBT compound = new CompoundNBT();
		if(!entity.saveAsPassenger(compound))
			throw new CustomNPCsException("Cannot store dead entities");
		ServerCloneController.Instance.addClone(compound, name, tab);
	}

	@Override
	public INbt getEntityNbt(){
		CompoundNBT compound = new CompoundNBT();
		entity.saveWithoutId(compound);
		ResourceLocation resourcelocation = net.minecraft.entity.EntityType.getKey(entity.getType());
		if(getType() == EntitiesType.PLAYER) {
			resourcelocation = new ResourceLocation("player");
		}
		if(resourcelocation != null) {
            compound.putString("id", resourcelocation.toString());
		}
		return NpcAPI.Instance().getINbt(compound);
	}

	@Override
	public void setEntityNbt(INbt nbt){
		entity.load(nbt.getMCNBT());
	}

	@Override
	public void playAnimation(int type) {
		levelWrapper.getMCWorld().getChunkSource().broadcastAndSend(entity, new SAnimateHandPacket(entity, type));
	}

	@Override
	public float getHeight() {
		return entity.getBbHeight();
	}

	@Override
	public float getEyeHeight() {
		return entity.getEyeHeight();
	}

	@Override
	public float getWidth() {
		return entity.getBbWidth();
	}
	@Override
	public IPos getPos() {
		return new BlockPosWrapper(entity.blockPosition());
	}
	@Override
	public void setPos(IPos pos) {
		entity.setPos(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
	}
	@Override
	public String[] getTags() {
		return entity.getTags().toArray(new String[entity.getTags().size()]);
	}
	@Override
	public void addTag(String tag) {
		entity.addTag(tag);
	}
	@Override
	public boolean hasTag(String tag) {
		return entity.getTags().contains(tag);
	}
	@Override
	public void removeTag(String tag) {
		entity.removeTag(tag);
	}
	@Override
	public double getMotionX() {
		return entity.getDeltaMovement().x;
	}
	@Override
	public double getMotionY() {
		return entity.getDeltaMovement().y;
	}
	@Override
	public double getMotionZ() {
		return entity.getDeltaMovement().z;
	}
	@Override
	public void setMotionX(double motion) {
		Vector3d mo = entity.getDeltaMovement();
		if(mo.x == motion)
			return;
		entity.setDeltaMovement(motion, mo.y, mo.z);
		entity.hurtMarked = true;
	}
	@Override
	public void setMotionY(double motion) {
		Vector3d mo = entity.getDeltaMovement();
		if(mo.y == motion)
			return;
		entity.setDeltaMovement(mo.x, motion, mo.z);
		entity.hurtMarked = true;
	}
	@Override
	public void setMotionZ(double motion) {
		Vector3d mo = entity.getDeltaMovement();
		if(mo.z == motion)
			return;
		entity.setDeltaMovement(mo.x, mo.y, motion);
		entity.hurtMarked = true;
	}
}
