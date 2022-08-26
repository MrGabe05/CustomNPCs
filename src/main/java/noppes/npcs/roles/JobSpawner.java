package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.CloneSpawnData;
import noppes.npcs.packets.server.SPacketToolMobSpawner;
import org.apache.commons.lang3.RandomStringUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.data.role.IJobSpawner;
import noppes.npcs.entity.EntityNPCInterface;

public class JobSpawner extends JobInterface implements IJobSpawner{
	public Map<Integer, CloneSpawnData> data = new HashMap<>();

	private int number = 0;
	
	public List<LivingEntity> spawned  = new ArrayList<LivingEntity>();
	
	private Map<String,Long> cooldown = new HashMap<String,Long>();
	
	private String id = RandomStringUtils.random(8, true, true);
	public boolean doesntDie = false;
	
	public int spawnType = 0;
	
	public int xOffset = 0;
	public int yOffset = 0;
	public int zOffset = 0;
	
	private LivingEntity target;
	
	public boolean despawnOnTargetLost = true;

	public JobSpawner(EntityNPCInterface npc) {
		super(npc);
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.put("SpawnerData", CloneSpawnData.save(data));
		
		compound.putString("SpawnerId", id);
		compound.putBoolean("SpawnerDoesntDie", doesntDie);
		compound.putInt("SpawnerType", spawnType);
		compound.putInt("SpawnerXOffset", xOffset);
		compound.putInt("SpawnerYOffset", yOffset);
		compound.putInt("SpawnerZOffset", zOffset);
		
		compound.putBoolean("DespawnOnTargetLost", despawnOnTargetLost);
		return compound;
	}

	public String getTitle(int slot) {
		CloneSpawnData sd = data.get(slot);
		if(sd == null)
			return "gui.selectnpc";
		return sd.tab + ": " + sd.name;
	}
	
	
	@Override
	public void load(CompoundNBT compound) {
		this.data = CloneSpawnData.load(compound.getList("SpawnerData", 10));
		
		id = compound.getString("SpawnerId");
		doesntDie = compound.getBoolean("SpawnerDoesntDie");
		spawnType = compound.getInt("SpawnerType");
		xOffset = compound.getInt("SpawnerXOffset");
		yOffset = compound.getInt("SpawnerYOffset");
		zOffset = compound.getInt("SpawnerZOffset");
		
		despawnOnTargetLost = compound.getBoolean("DespawnOnTargetLost");
	}
    
    public void setJobCompound(int slot, int tab, String name){
    	data.put(slot, new CloneSpawnData(tab, name));
    }


	public void remove(int i){
		data.remove(i);
	}

	@Override
	public void aiUpdateTask() {
		if(spawned.isEmpty()){
			if(spawnType == 0){
				if(spawnEntity(number) == null && !doesntDie){
					npc.remove();
				}
			}
			if(spawnType == 1){
				if(number >= 6 && !doesntDie)
					npc.remove();
				else{
					spawnEntity(0);
					spawnEntity(1);
					spawnEntity(2);
					spawnEntity(3);
					spawnEntity(4);
					spawnEntity(5);
					number = 6;
				}
			}
			if(spawnType == 2){
				ArrayList<CompoundNBT> list = new ArrayList<CompoundNBT>();
				for(CloneSpawnData d : data.values()){
					CompoundNBT c = d.getCompound();
					if(c != null && c.contains("id"))
						list.add(c);
				}
				
				if(!list.isEmpty()){
					CompoundNBT compound = list.get(npc.getRandom().nextInt(list.size()));
					spawnEntity(compound);
				}
				else if(!doesntDie)
					npc.remove();
			}
		}
		else{
			checkSpawns();
		}
		
	}
	
	public void checkSpawns(){
		Iterator<LivingEntity> iterator = spawned.iterator();
		while(iterator.hasNext()){
			LivingEntity spawn = iterator.next();
			if(shouldDelete(spawn)){
				spawn.removed = true;
				iterator.remove();
			}
			else{
				checkTarget(spawn);
			}
		}
	}
	
	public void checkTarget(LivingEntity entity){
		if(entity instanceof MobEntity){
			MobEntity liv = (MobEntity) entity;
			if(liv.getTarget() == null || npc.getRandom().nextInt(100) == 1)
				liv.setTarget(target);
		}
		else if(entity.getLastHurtByMob() == null || npc.getRandom().nextInt(100) == 1){
			entity.setLastHurtByMob(target);
		}
	}
	
	public boolean shouldDelete(LivingEntity entity){
		return !npc.isInRange(entity, 60) || entity.removed || entity.getHealth() <= 0 || despawnOnTargetLost && target == null;
	}
	
	private LivingEntity getTarget() {
		LivingEntity target = getTarget(npc);
		if(target != null)
			return target;

		for(LivingEntity entity : spawned){
			target = getTarget(entity);
			if(target != null)
				return target;
		}
		return null;
	}
	
	private LivingEntity getTarget(LivingEntity entity){
		if(entity instanceof MobEntity){
			target = ((MobEntity)entity).getTarget();
			if(target != null && !target.removed && target.getHealth() > 0)
				return target;
		}
		target = entity.getLastHurtByMob();
		if(target != null && !target.removed && target.getHealth() > 0)
			return target;
		return null;
	}
	
	private void setTarget(LivingEntity base, LivingEntity target) {
		if (base instanceof MobEntity)
			((MobEntity) base).setTarget(target);
		else
			base.setLastHurtByMob(target);
	}

	@Override
	public boolean aiShouldExecute() {
		if(data.isEmpty() || npc.isKilled())
			return false;
		
		target = getTarget();
		if(npc.getRandom().nextInt(30) == 1){
			if(spawned.isEmpty())
				spawned = getNearbySpawned();
		}
		if(!spawned.isEmpty())
			checkSpawns();
		return target != null;
	}

	@Override
	public boolean aiContinueExecute() {
		return aiShouldExecute();
	}

	@Override
	public void stop() {
		reset();
	}

	@Override
	public void aiStartExecuting() {
		number = 0;
		for(LivingEntity entity : spawned){
			int i = entity.getPersistentData().getInt("NpcSpawnerNr");
			if(i > number)
				number = i;
			setTarget(entity, npc.getTarget());
		}
	}

	@Override
	public void reset() {
		number = 0;
		if(spawned.isEmpty())
			spawned = getNearbySpawned();
		
		target = null;
		
		checkSpawns();
	}
	
	@Override
	public void killed() {
		reset();
	}
	
	private LivingEntity spawnEntity(CompoundNBT compound){
		if(compound == null || !compound.contains("id"))
			return null;
		double x = npc.getX() + xOffset - 0.5 + npc.getRandom().nextFloat();
		double y = npc.getY() + yOffset;
		double z = npc.getZ() + zOffset - 0.5 + npc.getRandom().nextFloat();
		Entity entity = SPacketToolMobSpawner.spawnClone(compound, x, y, z, npc.level);
		if(entity == null || !(entity instanceof LivingEntity))
			return null;
		LivingEntity living = (LivingEntity) entity;
		living.getPersistentData().putString("NpcSpawnerId", id);
		living.getPersistentData().putInt("NpcSpawnerNr", number);
		setTarget(living, npc.getTarget());
		living.setPos(x, y, z);
		if(living instanceof EntityNPCInterface){
			EntityNPCInterface snpc = (EntityNPCInterface) living;
			snpc.stats.spawnCycle = 4;
			snpc.stats.respawnTime = 0;
			snpc.ais.returnToStart = false;
		}
		spawned.add(living);
		return living;
	}

	private CompoundNBT getCompound(int i) {
		for(Entry<Integer, CloneSpawnData> entry  : data.entrySet()){
			if(i <= entry.getKey()){
				CompoundNBT compound = entry.getValue().getCompound();
				if(compound != null && compound.contains("id")){
					number = entry.getKey() + 1;
					return compound;
				}
			}
		}
		return null;
	}

	
	private List<LivingEntity> getNearbySpawned(){
		List<LivingEntity> spawnList = new ArrayList<LivingEntity>();
		List<LivingEntity> list = npc.level.getEntitiesOfClass(LivingEntity.class, npc.getBoundingBox().inflate(40, 40, 40));
		for(LivingEntity entity : list){
			if(entity.getPersistentData().getString("NpcSpawnerId").equals(id) && !entity.removed)
				spawnList.add(entity);
		}
		return spawnList;
	}

	public boolean isOnCooldown(String name) {
		if(!cooldown.containsKey(name))
			return false;
		
		long time = cooldown.get(name);
		return System.currentTimeMillis() < time + 1200000; //20 minutes cooldown
	}
	
	@Override
	public IEntityLiving spawnEntity(int i){
		CompoundNBT compound = getCompound(i);
		if(compound == null){
			return null;
		}
		LivingEntity base = spawnEntity(compound);
		if(base == null)
			return null;
		
		return (IEntityLiving) NpcAPI.Instance().getIEntity(base);
	}
	
	@Override
	public void removeAllSpawned(){
		for(LivingEntity entity : spawned){
			entity.removed = true;
		}
		spawned = new ArrayList<LivingEntity>();
	}

	@Override
	public int getType() {
		return JobType.SPAWNER;
	}
}
