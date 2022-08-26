package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;

public class ContainerNPCFollowerSetup extends Container
{
	private RoleFollower role;

    public ContainerNPCFollowerSetup(int containerId, PlayerInventory playerInventory, int entityId){
        super(CustomContainer.container_followersetup, containerId);
        EntityNPCInterface npc = (EntityNPCInterface) playerInventory.player.level.getEntity(entityId);
        role = (RoleFollower) npc.role;
    	
        for(int i1 = 0; i1 < 3; i1++)
        	addSlot(new Slot(role.inventory, i1, 44, 39 + i1 * 25));
        
        for(int i1 = 0; i1 < 3; i1++)
        {
            for(int l1 = 0; l1 < 9; l1++)
            {
            	addSlot(new Slot(playerInventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 113 + i1 * 18));
            }

        }

        for(int j1 = 0; j1 < 9; j1++)
        {
        	addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 171));
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity par1PlayerEntity, int i)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)slots.get(i);
        if(slot != null && slot.hasItem())
        {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if(i >= 0 && i < 3)
            {
                if(!moveItemStackTo(itemstack1, 3, 38, true))
                {
                    return ItemStack.EMPTY;
                }
            } else
            if(i >= 3 && i < 30)
            {
                if(!moveItemStackTo(itemstack1, 30, 38, false))
                {
                    return ItemStack.EMPTY;
                }
            } else
            if(i >= 30 && i < 38)
            {
                if(!moveItemStackTo(itemstack1, 3, 29, false))
                {
                    return ItemStack.EMPTY;
                }
            } else
            if(!moveItemStackTo(itemstack1, 3, 38, false))
            {
                return ItemStack.EMPTY;
            }
            if(itemstack1.getCount() == 0)
            {
                slot.set(ItemStack.EMPTY);
            } else
            {
                slot.setChanged();
            }
            if(itemstack1.getCount() != itemstack.getCount())
            {
                slot.onTake(par1PlayerEntity, itemstack1);
            } else
            {
                return ItemStack.EMPTY;
            }
        }
        return itemstack;
    }
	@Override
	public boolean stillValid(PlayerEntity entityplayer) {
		return true;
	}
}
