package noppes.npcs.api.wrapper;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.EventHooks;
import noppes.npcs.api.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.constants.EntitiesType;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketPlaySound;

import java.util.*;
import java.util.function.Predicate;

public class WorldWrapper implements IWorld{
	public static Map<String,Object> tempData = new HashMap<String,Object>();

	public ServerWorld level;
	
	public IDimension dimension;
	
	private IData tempdata = new IData(){

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

	private IData storeddata = new IData(){

		@Override
		public void put(String key, Object value) {
			CompoundNBT compound = ScriptController.Instance.compound;
			if(value instanceof Number)
				compound.putDouble(key, ((Number) value).doubleValue());
			else if(value instanceof String)
				compound.putString(key, (String)value);
			ScriptController.Instance.shouldSave = true;
		}

		@Override
		public Object get(String key) {
			CompoundNBT compound = ScriptController.Instance.compound;
			if(!compound.contains(key))
				return null;
			INBT base = compound.get(key);
			if(base instanceof NumberNBT)
				return ((NumberNBT)base).getAsDouble();
			return ((StringNBT)base).getAsString();
		}

		@Override
		public void remove(String key) {
			ScriptController.Instance.compound.remove(key);
			ScriptController.Instance.shouldSave = true;
		}

		@Override
		public boolean has(String key) {
			return ScriptController.Instance.compound.contains(key);
		}

		@Override
		public void clear() {
			ScriptController.Instance.compound = new CompoundNBT();
			ScriptController.Instance.shouldSave = true;
		}

		@Override
		public String[] getKeys() {
			return ScriptController.Instance.compound.getAllKeys().toArray(new String[ScriptController.Instance.compound.getAllKeys().size()]);
		}
		
	};
	
	private WorldWrapper(World level){
		this.level = (ServerWorld) level;
		this.dimension = new DimensionWrapper(level.dimension.location(), level.dimensionType);
	}
	
	@Override
	public ServerWorld getMCWorld() {
		return level;
	}

	@Override
	public IEntity[] getNearbyEntities(int x, int y, int z, int range, int type) {
		return getNearbyEntities(new BlockPosWrapper(new BlockPos(x, y, z)), range, type);
	}

	@Override
	public IEntity[] getNearbyEntities(IPos pos, int range, int type) {
		AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 1, 1, 1).move(pos.getMCBlockPos()).inflate(range, range, range);
		List<Entity> entities = level.getEntitiesOfClass(getClassForType(type), bb);
		List<IEntity> list = new ArrayList<IEntity>();
		for(Entity living : entities){
			list.add(NpcAPI.Instance().getIEntity(living));
		}
		return list.toArray(new IEntity[list.size()]);
	}

	@Override
	public IEntity[] getAllEntities(int type) {
		List<Entity> entities = getEntities(getClassForType(type), EntityPredicates.NO_SPECTATORS);
		List<IEntity> list = new ArrayList<IEntity>();
		for(Entity living : entities){
			list.add(NpcAPI.Instance().getIEntity(living));
		}
		return list.toArray(new IEntity[list.size()]);
	}

	public List<Entity> getEntities(Class<?> entityTypeIn, Predicate<? super Entity> predicateIn) {
		List<Entity> list = Lists.newArrayList();
		ServerChunkProvider serverchunkprovider = level.getChunkSource();

		for(Entity entity : level.entitiesById.values()) {
			if (entityTypeIn.isAssignableFrom(entity.getClass()) && serverchunkprovider.hasChunk(MathHelper.floor(entity.getX()) >> 4, MathHelper.floor(entity.getZ()) >> 4) && predicateIn.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	@Override
	public IEntity getClosestEntity(int x, int y, int z, int range, int type) {
		return getClosestEntity(new BlockPosWrapper(new BlockPos(x, y, z)), range, type);
	}

	@Override
	public IEntity getClosestEntity(IPos pos, int range, int type) {
		AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 1, 1, 1).move(pos.getMCBlockPos()).inflate(range, range, range);
		List<Entity> entities = level.getEntitiesOfClass(getClassForType(type), bb);
		double distance = range * range * range;
		Entity entity = null;
		for(Entity e : entities){
	        double r = pos.getMCBlockPos().distSqr(e.blockPosition());
			if(entity == null){
				distance = r;
				entity = e;
			}
			else if(r < distance){
				distance = r;
				entity = e;
			}
		}
		return NpcAPI.Instance().getIEntity(entity);
	}
	
	@Override
	public IEntity getEntity(String uuid){
		try{
			UUID id = UUID.fromString(uuid);
			Entity e = level.getEntity(id);
			if(e == null)
				e = level.getPlayerByUUID(id);
			if(e == null)
				return null;
			return NpcAPI.Instance().getIEntity(e);
		}
		catch(Exception e){
			throw new CustomNPCsException("Given uuid was invalid " + uuid);
		}
	}

	@Override
	public IEntity createEntityFromNBT(INbt nbt){
		Entity entity = EntityType.create(nbt.getMCNBT(), level).orElse(null);
		if(entity == null)
			throw new CustomNPCsException("Failed to create an entity from given NBT");
		return NpcAPI.Instance().getIEntity(entity);
	}

	@Override
	public IEntity createEntity(String id){
		ResourceLocation resource = new ResourceLocation(id);
		net.minecraft.entity.EntityType type = ForgeRegistries.ENTITIES.getValue(resource);
		Entity entity = type.create(level);
		if(entity == null)
			throw new CustomNPCsException("Failed to create an entity from given id: " + id);
		entity.setPos(0, 1, 0);
		return NpcAPI.Instance().getIEntity(entity);
	}
		
	@Override
	public IPlayer getPlayer(String name){

		for(PlayerEntity entityplayer : level.players) {
			if (name.equals(entityplayer.getName().getString())) {
				return (IPlayer) NpcAPI.Instance().getIEntity(entityplayer);
			}
		}
		return null;
	}

	
	private Class getClassForType(int type){
		if(type == EntitiesType.ANY)
			return Entity.class;
		if(type == EntitiesType.LIVING)
			return LivingEntity.class;
		if(type == EntitiesType.PLAYER)
			return PlayerEntity.class;
		if(type == EntitiesType.ANIMAL)
			return AnimalEntity.class;
		if(type == EntitiesType.MONSTER)
			return MonsterEntity.class;
		if(type == EntitiesType.NPC)
			return EntityNPCInterface.class;
		if(type == EntitiesType.ITEM)
			return ItemEntity.class;
		if(type == EntitiesType.PROJECTILE)
			return EntityProjectile.class;
		if(type == EntitiesType.THROWABLE)
			return ThrowableEntity.class;
		if(type == EntitiesType.ARROW)
			return AbstractArrowEntity.class;
		if(type == EntitiesType.PIXELMON)
			return PixelmonHelper.getPixelmonClass();
		if(type == EntitiesType.VILLAGER)
			return VillagerEntity.class;
		return Entity.class;
	}

	@Override
	public long getTime() {
		return level.getDayTime();
	}
	
	@Override
	public void setTime(long time){
		level.setDayTime(time);
	}

	@Override
	public long getTotalTime() {
		return level.getGameTime();
	}

	@Override
	public IBlock getBlock(int x, int y, int z) {
		return NpcAPI.Instance().getIBlock(level, new BlockPos(x, y, z));
	}

	@Override
	public IBlock getBlock(IPos pos) {
		return NpcAPI.Instance().getIBlock(level, pos.getMCBlockPos());
	}
	
	public boolean isChunkLoaded(int x, int z) {
		return level.getChunkSource().hasChunk(x >> 4, z >> 4);
	}

	@Override
	public void setBlock(int x, int y, int z, String name, int meta) {
		this.setBlock(NpcAPI.Instance().getIPos(x, y, z), name);
	}

	@Override
	public IBlock setBlock(IPos pos, String name){
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
		if(block == null){
			throw new CustomNPCsException("There is no such block: %s", name);
		}
		
		level.setBlock(pos.getMCBlockPos(), block.defaultBlockState(), 2);
		return NpcAPI.Instance().getIBlock(level, pos.getMCBlockPos());
	}

	@Override
	public void removeBlock(int x, int y, int z){
		level.removeBlock(new BlockPos(x, y, z), false);
	}

	@Override
	public void removeBlock(IPos pos){
		level.removeBlock(pos.getMCBlockPos(), false);
	}

	@Override
	public float getLightValue(int x, int y, int z){
		return level.getLightEmission(new BlockPos(x, y, z)) / 16f;
	}

	@Override
	public IBlock getSpawnPoint(){
		BlockPos pos = level.getSharedSpawnPos();
		if(pos == null)
			pos = level.getSharedSpawnPos();
		return NpcAPI.Instance().getIBlock(level, pos);
	}

	@Override
	public void setSpawnPoint(IBlock block){
		ISpawnWorldInfo info = (ISpawnWorldInfo)level.getLevelData();
		info.setSpawn(new BlockPos(block.getX(), block.getY(), block.getZ()), 0);
	}

	@Override
	public boolean isDay(){
		return level.getDayTime() % 24000 < 12000;
	}

	@Override
	public boolean isRaining(){
		return level.getLevelData().isRaining();
	}

	@Override
	public void setRaining(boolean bo){
		level.getLevelData().setRaining(bo);
	}

	@Override
	public void thunderStrike(double x, double y, double z){
		LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(level);
		bolt.moveTo(x, y, z);
		bolt.setVisualOnly(false);
        level.addFreshEntity(bolt);
	}
	
	@Override
	public void spawnParticle(String particle, double x, double y, double z, double dx, double dy, double dz, double speed, int count){
		ParticleType type = ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(particle));
		if(type == null){
			throw new CustomNPCsException("Unknown particle type: " + particle);
		}
		level.sendParticles((IParticleData)type, x, y, z, count, dx, dy, dz, speed);
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
	public IItemStack createItem(String name, int size){
		Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
		if(item == null)
			throw new CustomNPCsException("Unknown item id: " + name);
		return NpcAPI.Instance().getIItemStack(new ItemStack(item, size));
	}
	
	@Override
	public IItemStack createItemFromNbt(INbt nbt){
		ItemStack item = ItemStack.of(nbt.getMCNBT());
		if(item.isEmpty())
			throw new CustomNPCsException("Failed to create an item from given NBT");
		return NpcAPI.Instance().getIItemStack(item);		
	}
	
	@Override
	public void explode(double x, double y, double z, float range, boolean fire, boolean grief){
		level.explode(null, x, y, z, range, fire, grief ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
	}

	@Override
	public IPlayer[] getAllPlayers(){
		List<ServerPlayerEntity> list = level.getServer().getPlayerList().getPlayers();
		IPlayer[] arr = new IPlayer[list.size()];
		for(int i = 0; i < list.size(); i++){
			arr[i] = (IPlayer) NpcAPI.Instance().getIEntity(list.get(i));
		}
		
		return arr;
	}

	@Override
	public String getBiomeName(int x, int z){
		return level.getBiome(new BlockPos(x, 0, z)).getRegistryName().toString();
	}
	
	@Override
	public IEntity spawnClone(double x, double y, double z, int tab, String name){
		return NpcAPI.Instance().getClones().spawn(x, y, z, tab, name, this);
	}
	
	@Override
	public void spawnEntity(IEntity entity){
		if(entity == null)
			throw new CustomNPCsException("Entity given was null");
		Entity e = entity.getMCEntity();
		if(level.getEntity(e.getUUID()) != null)
			throw new CustomNPCsException("Entity with this UUID already exists");
		e.setPos(e.getX(), e.getY(), e.getZ());
		level.addFreshEntity(e);
	}

	@Override
	public IEntity getClone(int tab, String name) {
		return NpcAPI.Instance().getClones().get(tab, name, this);
	}
	
	@Override
	public IScoreboard getScoreboard(){
		return new ScoreboardWrapper(level.getServer());
	}
	
	@Override
	public void broadcast(String message){
		StringTextComponent text = new StringTextComponent(message);
		for(PlayerEntity p : level.getPlayers((e) -> true)){
			p.sendMessage(text, Util.NIL_UUID);
		}
	}

	@Override
	public int getRedstonePower(int x, int y, int z){
		return level.getDirectSignalTo(new BlockPos(x, y, z));
	}

	@Deprecated
	public static WorldWrapper createNew(ServerWorld level){
		return new WorldWrapper(level);
	}

	@Override
	public IDimension getDimension() {
		return dimension;
	}

	@Override
	public String getName() {
		return level.serverLevelData.getLevelName();
	}

	@Override
	public BlockPos getMCBlockPos(int x, int y, int z) {
		return new BlockPos(x, y, z);
	}

	@Override
	public void playSoundAt(IPos pos, String sound, float volume, float pitch) {
		BlockPos bp = pos.getMCBlockPos();
		Packets.sendNearby(this.level, bp, 16, new PacketPlaySound(sound, bp, volume, pitch));
	}

	@Override
	public void trigger(int id, Object... arguments) {
		EventHooks.onScriptTriggerEvent(ScriptController.Instance.forgeScripts, id, this, BlockPosWrapper.ZERO, null, arguments);
	}
}
