package noppes.npcs.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotApi extends Slot{
	public SlotApi(IInventory par1iInventory, int index, int xPosition, int yPosition) {
		super(par1iInventory, index, xPosition, yPosition);
	}
}
