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

class SlotNPCArmor extends Slot{

    final EquipmentSlotType armorType; /* synthetic field */

    SlotNPCArmor(IInventory iinventory, int i, int j, int k, EquipmentSlotType l){
        super(iinventory, i, j, k);
        armorType = l;
    }

    @Override
    public int getMaxStackSize(){
        return 1;
    }

    @OnlyIn(Dist.CLIENT)
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon(){
        return Pair.of(PlayerContainer.BLOCK_ATLAS, SlotCompanionArmor.ARMOR_SLOT_TEXTURES[armorType.getIndex()]);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        if(itemstack.getItem() instanceof ArmorItem){
            return ((ArmorItem)itemstack.getItem()).getSlot() == armorType;
        }
        if(itemstack.getItem() instanceof BlockItem){
            return armorType == EquipmentSlotType.HEAD;
        }
        return false;
    }
}
