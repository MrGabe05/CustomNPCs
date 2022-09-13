package noppes.npcs.constants;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomItems;
import noppes.npcs.roles.RoleCompanion;

public enum EnumCompanionTalent {
	INVENTORY(Item.byBlock(Blocks.CRAFTING_TABLE)), ARMOR(Items.IRON_CHESTPLATE), 
	SWORD(Items.DIAMOND_SWORD), RANGED(Items.BOW), 
	ACROBATS(Items.LEATHER_BOOTS), INTEL(Items.BOOK);

	public ItemStack item;
	EnumCompanionTalent(Item item){
		this.item = new ItemStack(item);
	}
}
