package noppes.npcs.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.roles.RoleCompanion;

class SlotCompanionWeapon extends Slot{

    final RoleCompanion role;

    public SlotCompanionWeapon(RoleCompanion role, IInventory iinventory, int id, int x, int y){
        super(iinventory, id, x, y);
        this.role = role;
    }

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean mayPlace(ItemStack itemstack) {
		if(NoppesUtilServer.IsItemStackNull(itemstack))
			return false;
		return role.canWearSword(NpcAPI.Instance().getIItemStack(itemstack));
	}
}
