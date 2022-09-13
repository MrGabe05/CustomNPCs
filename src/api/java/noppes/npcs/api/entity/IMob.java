package noppes.npcs.api.entity;

import net.minecraft.entity.MobEntity;
import noppes.npcs.api.IPos;

public interface IMob<T extends MobEntity> extends IEntityLiving<T> {

	/**
	 * @return Whether or not this entity is navigating somewhere
	 */
	boolean isNavigating();
	
	/**
	 * Stop navigating wherever this npc was walking to
	 */
	void clearNavigation();
	
	/**
	 * Start path finding toward this target
	 * @param x Destination x position
	 * @param y Destination x position
	 * @param z Destination x position
	 */
	void navigateTo(double x, double y, double z, double speed);

	void jump();

	@Override
	T getMCEntity();

	IPos getNavigationPath();
	
}
