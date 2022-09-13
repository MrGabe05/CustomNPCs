package noppes.npcs.api.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.EntitiesType;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IProjectile;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.entity.EntityProjectile;

public class ProjectileWrapper<T extends EntityProjectile> extends ThrowableWrapper<T> implements IProjectile {

	public ProjectileWrapper(T entity) {
		super(entity);
	}

	@Override
	public IItemStack getItem() {
		return NpcAPI.Instance().getIItemStack(entity.getItemDisplay());
	}
	
	@Override
	public void setItem(IItemStack item) {
		if(item == null)
			entity.setThrownItem(ItemStack.EMPTY);
		else
			entity.setThrownItem(item.getMCItemStack());
	}

	@Override
	public boolean getHasGravity() {
		return entity.hasGravity();
	}

	@Override
	public void setHasGravity(boolean bo) {
		entity.setHasGravity(bo);
	}

	@Override
	public int getAccuracy() {
		return entity.accuracy;
	}

	@Override
	public void setAccuracy(int accuracy) {
		entity.accuracy = accuracy;
	}

	@Override
	public void setHeading(IEntity entity) {
		setHeading(entity.getX(), entity.getMCEntity().getBoundingBox().minY + (double)(entity.getHeight() / 2.0F), entity.getZ());
	}

	@Override
	public void setHeading(double x, double y, double z) {
        x = x - entity.getX();
		y = y - entity.getY();
		z = z - entity.getZ();
		float varF = entity.hasGravity() ? MathHelper.sqrt(x * x + z * z) : 0.0F;
		float angle = entity.getAngleForXYZ(x, y, z, varF, false);
		float acc = 20.0F - MathHelper.floor(entity.accuracy / 5.0F);
		entity.shoot(x, y, z, angle, acc);
	}

	@Override
	public void setHeading(float yaw, float pitch) {
		entity.yRotO = entity.yRot = yaw;
		entity.xRotO = entity.xRot = pitch;

        double varX = -MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI);
        double varZ = MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI);
        double varY = -MathHelper.sin(pitch / 180.0F * (float)Math.PI);

		float acc = 20.0F - MathHelper.floor(entity.accuracy / 5.0F);
		entity.shoot(varX, varY, varZ, -pitch, acc);
	}

	@Override
	public int getType() {
		return EntitiesType.PROJECTILE;
	}

	@Override
	public boolean typeOf(int type){
		return type == EntitiesType.PROJECTILE || super.typeOf(type);
	}

	@Override
	public void enableEvents() {
		if(ScriptContainer.Current == null)
			throw new CustomNPCsException("Can only be called during scripts");
		
		if(!entity.scripts.contains(ScriptContainer.Current)) {
			entity.scripts.add(ScriptContainer.Current);
		}
	}

}
