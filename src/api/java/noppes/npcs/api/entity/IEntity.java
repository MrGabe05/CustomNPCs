package noppes.npcs.api.entity;

import net.minecraft.entity.Entity;
import noppes.npcs.api.INbt;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IRayTrace;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.constants.EntitiesType;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;

public interface IEntity<T extends Entity> {
	
	double getX();
	
	void setX(double x);
	
	double getY();
	
	void setY(double y);
	
	double getZ();
	
	void setZ(double z);

	int getBlockX();
	
	int getBlockY();
	
	int getBlockZ();
	
	IPos getPos();
	
	void setPos(IPos pos);
	
	void setPosition(double x, double y, double z);
		
	/**
	 * @param rotation The rotation to be set (0-360)
	 */
	void setRotation(float rotation);
	
	/**
	 * @return Current rotation of the entity
	 */
	float getRotation();
	
	/**
	 * @return Returns the height of the bounding box
	 */
	float getHeight();

	/**
	 * @return Returns the eye height of the entity, used in this like canSee and such
	 */
	float getEyeHeight();
		
	/**
	 * @return Returns the width of the bounding box
	 */
	float getWidth();
	
	/**
	 * @param pitch The viewing pitch
	 */
	void setPitch(float pitch);
	
	/**
	 * @return Entities viewing pitch
	 */
	float getPitch();

	IEntity getMount();
	
	void setMount(IEntity entity);

	/**
	 * @return Returns the entities riding this entity
	 */
	IEntity[] getRiders();
	
	/**
	 * @return Returns the entities riding this entity including the entities riding those entities
	 */
	IEntity[] getAllRiders();

	void addRider(IEntity entity);

	void clearRiders();
	
	/**
	 * @param power How strong the knockback is
	 * @param direction The direction in which he flies back (0-360). Usually based on getRotation()
	 */
	void knockback(int power, float direction);
	
	boolean isSneaking();
	
	boolean isSprinting();
	
	IEntityItem dropItem(IItemStack item);
	
	boolean inWater();
	
	boolean inFire();
	
	boolean inLava();
	
	/**
	 * Temp data stores anything but only untill it's reloaded
	 */
	IData getTempdata();
	
	/**
	 * Stored data persists through world restart. Unlike tempdata only Strings and Numbers can be saved
	 */
	IData getStoreddata();
	
	/**
	 * The Entity's extra stored NBT data
	 * @return The Entity's extra stored NBT data
	 */
	INbt getNbt();
	
	boolean isAlive();
	/**
	 * @return The age of this entity in ticks
	 */
	long getAge();
	
	/**
	 * Despawns this entity. Removes it permanently
	 */
	void despawn();
	
	/**
	 * Spawns this entity into the world (For NPCs dont forget to set their home position)
	 */
	void spawn();

	/**
	 * Kill the entity, doesnt't despawn it
	 */
	void kill();
		
	/**
	 * @return Return whether or not this entity is on fire
	 */
	boolean isBurning();
	
	/**
	 * @param seconds Amount of seconds this entity will burn. 
	 */
	void setBurning(int seconds);

	/**
	 * Removes fire from this entity
	 */
	void extinguish();
	
	/**
	 * @return Returns the {@link noppes.npcs.api.IWorld}
	 */
	IWorld getWorld();

	/**
	 * @return Name as which it's registered in minecraft
	 */
	String getTypeName();
	/**
	 * @return Returns the {@link EntitiesType} of this entity
	 */
	int getType();
	
	/**
	 * @param type {@link EntitiesType} to check
	 * @return Returns whether the entity is type of the given {@link EntitiesType}
	 */
	boolean typeOf(int type);

	/**
	 * Expert users only
	 * @return Returns minecrafts entity
	 */
	T getMCEntity();
	
	String getUUID();
	
	String generateNewUUID();
	
	/**
	 * Stores the entity as clone server side
	 * @param tab
	 * @param name
	 */
	void storeAsClone(int tab, String name);

	/**
	 * This is not a function you should be calling every tick.
	 * Returns the entire entity as nbt
	 */
	INbt getEntityNbt();

	/**
	 * This is not a function you should be calling every tick
	 */
	void setEntityNbt(INbt nbt);

	/**
	 * Gets the first block within distance the npc is looking at
	 * @param distance
	 * @param stopOnLiquid
	 * @param ignoreBlockWithoutBoundingBox
	 * @return
	 */
	IRayTrace rayTraceBlock(double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox);

	/**
	 * Gets the entities within distance the npc is looking at sorted by distance
	 * @param distance
	 * @param stopOnLiquid
	 * @param ignoreBlockWithoutBoundingBox
	 * @return
	 */
	IEntity[] rayTraceEntities(double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox);
	
	/**
	 * Tags are used by scoreboards and can be used in commands
	 */
	String[] getTags();
	
	void addTag(String tag);
	
	boolean hasTag(String tag);
	
	void removeTag(String tag);

	/**
	 * Play specific minecraft animations client side
	 * 0 and 3 are for LivingEntity entities and 2 is only for players
	 * @param type 0:Swing main hand, 1:Hurt animation, 2:Wakeup Player 3:Swing offhand hand, 4:Crit particle, 5:Spell crit particle
	 */
	void playAnimation(int type);

	void damage(float amount);

	double getMotionX();
	
	double getMotionY();
	
	double getMotionZ();

	void setMotionX(double motion);
	
	void setMotionY(double motion);
	
	void setMotionZ(double motion);
	
	/**
	 * @return Returns the current name displayed by the entity
	 */
	String getName();
	
	/**
	 * @param name Set a custom name for this entity
	 */
	void setName(String name);

	boolean hasCustomName();

	/**
	 * @return Returns the original name incase a custom name has been set
	 */
	String getEntityName();
}
