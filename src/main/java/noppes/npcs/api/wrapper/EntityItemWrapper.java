package noppes.npcs.api.wrapper;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.EntitiesType;
import noppes.npcs.api.entity.IEntityItem;
import noppes.npcs.api.item.IItemStack;

import java.util.UUID;

public class EntityItemWrapper<T extends ItemEntity> extends EntityWrapper<T> implements IEntityItem {
	
	public EntityItemWrapper(T entity) {
		super(entity);
	}

	@Override
	public String getOwner() {
		if(this.entity.getOwner() == null)
			return null;
		return this.entity.getOwner().toString();
	}

	@Override
	public void setOwner(String name) {
		this.entity.setOwner(UUID.fromString(name));
	}

	@Override
	public int getPickupDelay() {
		return this.entity.pickupDelay;
	}

	@Override
	public void setPickupDelay(int delay) {
		this.entity.setPickUpDelay(delay);
	}

	@Override
	public int getType() {
		return EntitiesType.ITEM;
	}

	@Override
	public long getAge() {
		return this.entity.age;
	}

	@Override
	public void setAge(long age) {
		age = Math.max(Math.min(age, Integer.MAX_VALUE), Integer.MIN_VALUE);
		this.entity.age = (int)age;
	}

	@Override
	public int getLifeSpawn() {
		return this.entity.lifespan;
	}

	@Override
	public void setLifeSpawn(int age) {
		this.entity.lifespan = age;
	}

	@Override
	public IItemStack getItem() {
		return NpcAPI.Instance().getIItemStack(this.entity.getItem());
	}

	@Override
	public void setItem(IItemStack item) {
		ItemStack stack = item == null? ItemStack.EMPTY:item.getMCItemStack();
		this.entity.setItem(stack);
	}
}
