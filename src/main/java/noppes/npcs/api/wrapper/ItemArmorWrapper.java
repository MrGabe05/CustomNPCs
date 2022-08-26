package noppes.npcs.api.wrapper;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.constants.ItemType;
import noppes.npcs.api.item.IItemArmor;

public class ItemArmorWrapper extends ItemStackWrapper implements IItemArmor{
	protected ArmorItem armor;
	protected ItemArmorWrapper(ItemStack item) {
		super(item);
		armor = (ArmorItem) item.getItem();
	}

	@Override
	public int getType(){
		return ItemType.ARMOR;
	}

	@Override
	public int getArmorSlot() {
		return armor.getSlot().getIndex();
	}

	@Override
	public String getArmorMaterial() {
		return armor.getMaterial().getName();
	}
}
