package noppes.npcs.api.entity;

import net.minecraft.entity.projectile.ThrowableEntity;
import noppes.npcs.api.item.IItemStack;

public interface IProjectile<T extends ThrowableEntity> extends IThrowable<T> {

	IItemStack getItem();
	
	void setItem(IItemStack item);

	/**
	 * @return Returns whether the arrow flies in a straight line or not
	 */
	boolean getHasGravity();

	/**
	 * @param bo Whether the arrow flies in a straight line or not
	 */
	void setHasGravity(boolean bo);

	int getAccuracy();

	void setAccuracy(int accuracy);

	/**
	 * Entity where the projectile heads towards
	 * The position for the projectile needs to have been set for this
	 */
	void setHeading(IEntity entity);

	/**
	 * Position where the projectile heads towards.
	 * The position for the projectile needs to have been set for this
	 */
	void setHeading(double x, double y, double z);

	/**
	 * @param yaw Rotation yaw
	 * @param pitch Rotation pitch
	 */
	void setHeading(float yaw, float pitch);
	
	/**
	 * For scripters to enable projectile events in their current scripting container
	 */
	void enableEvents();

	
}
