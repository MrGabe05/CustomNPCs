package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobItemGiver;

public class ContainerNpcItemGiver extends Container
{    
    private JobItemGiver role;

    public ContainerNpcItemGiver(int containerId, PlayerInventory playerInventory, int entityId){
        super(CustomContainer.container_itemgiver, containerId);
        EntityNPCInterface npc = (EntityNPCInterface) playerInventory.player.level.getEntity(entityId);
    	this.role = (JobItemGiver) npc.job;
        for(int j1 = 0; j1 < 9; j1++)
        {
        	addSlot(new Slot(role.inventory, j1, 6 + j1 * 18, 90));
        }

        for(int i1 = 0; i1 < 3; i1++)
        {
            for(int l1 = 0; l1 < 9; l1++)
            {
            	addSlot(new Slot(playerInventory, l1 + i1 * 9 + 9, 6 + l1 * 18, 116 + i1 * 18));
            }

        }

        for(int j1 = 0; j1 < 9; j1++)
        {
        	addSlot(new Slot(playerInventory, j1, 6 + j1 * 18, 174));
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity par1PlayerEntity, int i){
        return ItemStack.EMPTY;
    }
    
	@Override
	public boolean stillValid(PlayerEntity entityplayer) {
		return true;
	}
}
