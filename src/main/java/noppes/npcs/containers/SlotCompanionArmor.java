package noppes.npcs.containers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.roles.RoleCompanion;

public class SlotCompanionArmor extends Slot{
	public static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS, PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS, PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE, PlayerContainer.EMPTY_ARMOR_SLOT_HELMET};


	final EquipmentSlotType armorType;
    final RoleCompanion role;

    public SlotCompanionArmor(RoleCompanion role, IInventory iinventory, int id, int x, int y, EquipmentSlotType type){
        super(iinventory, id, x, y);
        armorType = type;
        this.role = role;
    }

	@Override
	public int getMaxStackSize() {
		return 1;
	}

    @OnlyIn(Dist.CLIENT)
	@Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon(){
        return Pair.of(PlayerContainer.BLOCK_ATLAS, SlotCompanionArmor.ARMOR_SLOT_TEXTURES[armorType.getIndex()]);
    }

	@Override
	public boolean mayPlace(ItemStack itemstack) {
		if (itemstack.getItem() instanceof ArmorItem && role.canWearArmor(itemstack))
			return ((ArmorItem)itemstack.getItem()).getSlot() == armorType;
		
		if (itemstack.getItem() instanceof BlockItem)
			return armorType == EquipmentSlotType.HEAD;
		
		return false;
	}
}
