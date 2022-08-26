package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilServer;

public class SlotNpcCrafting extends CraftingResultSlot{

    private final CraftingInventory craftMatrix;

    public SlotNpcCrafting(PlayerEntity player, CraftingInventory craftingInventory, IInventory inventory, int slotIndex, int x, int y) {
        super(player, craftingInventory, inventory, slotIndex, x, y);
		this.craftMatrix = craftingInventory;
	}
	
	@Override
    public ItemStack onTake(PlayerEntity player, ItemStack itemStack) {
        this.checkTakeAchievements(itemStack);

        for (int i = 0; i < this.craftMatrix.getContainerSize(); ++i)
        {
            ItemStack itemstack1 = this.craftMatrix.getItem(i);

            if (!NoppesUtilServer.IsItemStackNull(itemstack1))
            {
                this.craftMatrix.removeItem(i, 1);

                if (itemstack1.getItem().hasContainerItem(itemstack1))
                {
                    ItemStack itemstack2 = itemstack1.getItem().getContainerItem(itemstack1);

                    if (!NoppesUtilServer.IsItemStackNull(itemstack2) && itemstack2.isDamageableItem() && itemstack2.getDamageValue() > itemstack2.getMaxDamage())
                    {
                        continue;
                    }

                    if (!player.inventory.add(itemstack2))
                    {
                        if (NoppesUtilServer.IsItemStackNull(this.craftMatrix.getItem(i)))
                        {
                            this.craftMatrix.setItem(i, itemstack2);
                        }
                        else
                        {
                            player.drop(itemstack2, false);
                        }
                    }
                }
            }
        }
        return itemStack;
    }
}
