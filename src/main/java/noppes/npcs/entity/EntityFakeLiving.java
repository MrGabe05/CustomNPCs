package noppes.npcs.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;

public class EntityFakeLiving extends LivingEntity{

	public EntityFakeLiving(World par1World) {
		super(CustomEntities.entityCustomNpc, par1World);
	}

	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return null;
	}

	@Override
	public ItemStack getItemBySlot(EquipmentSlotType slotIn) {
		return null;
	}

	@Override
	public void setItemSlot(EquipmentSlotType slotIn, ItemStack stack) {

	}

	@Override
	public HandSide getMainArm() {
		return null;
	}

}
