package noppes.npcs.api.entity;

import net.minecraft.entity.LivingEntity;
import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.api.item.IItemStack;

public interface IEntityLiving<T extends LivingEntity> extends IEntity<T>{

	float getHealth();
	
	void setHealth(float health);
	
	float getMaxHealth();
	
	void setMaxHealth(float health);
	
	boolean isAttacking();
	
	void setAttackTarget(IEntityLiving living);
	
	IEntityLiving getAttackTarget();
	
	/**
	 * @return Returns the last Entity this Entity attacked
	 */
	IEntityLiving getLastAttacked();

	/**
	 * @return Returns the age of this entity when it was last attacked
	 */
	int getLastAttackedTime();
	
	boolean canSeeEntity(IEntity entity);

	void swingMainhand();

	void swingOffhand();
	
	IItemStack getMainhandItem();

	void setMainhandItem(IItemStack item);

	IItemStack getOffhandItem();

	void setOffhandItem(IItemStack item);
	
	/**
	 * Note not all Living Entities support this
	 * @param slot Slot of what armor piece to get, 0:boots, 1:pants, 2:body, 3:head
	 * @return The item in the given slot
	 */
	IItemStack getArmor(int slot);
	
	/**
	 * @param slot Slot of what armor piece to set, 0:boots, 1:pants, 2:body, 3:head
	 * @param item Item to be set
	 */
	void setArmor(int slot, IItemStack item);
	
	/**
	 * Works the same as the <a href="http://minecraft.gamepedia.com/Commands#effect">/effect command</a>
	 * @param effect
	 * @param duration The duration in seconds
	 * @param strength The amplifier of the potion effect
	 * @param hideParticles Whether or not you want to hide potion particles
	 */
	void addPotionEffect(int effect, int duration, int strength, boolean hideParticles);
	
	void clearPotionEffects();
	
	int getPotionEffect(int effect);
	
	IMark addMark(int type);
	
	void removeMark(IMark mark);
	
	IMark[] getMarks();
	
	boolean isChild();

	@Override
	T getMCEntity();

	float getMoveForward();

	void setMoveForward(float move);

	float getMoveStrafing();

	void setMoveStrafing(float move);

	float getMoveVertical();

	void setMoveVertical(float move);
}
