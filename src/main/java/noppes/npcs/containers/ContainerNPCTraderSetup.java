package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleTrader;

public class ContainerNPCTraderSetup extends Container
{
	public RoleTrader role;

    public ContainerNPCTraderSetup(int containerId, PlayerInventory playerInventory, int entityId){
        super(CustomContainer.container_tradersetup, containerId);
        EntityNPCInterface npc = (EntityNPCInterface) playerInventory.player.level.getEntity(entityId);
        role = (RoleTrader) npc.role;
        
        for(int i = 0; i < 18; i++)
        {
        	int x = 7;
        	x += i%3 * 94;
        	int y = 15;
        	y += i/3 * 22;
        	addSlot(new Slot(role.inventoryCurrency, i + 18, x, y));
        	addSlot(new Slot(role.inventoryCurrency, i, x + 18, y));
        	addSlot(new Slot(role.inventorySold, i, x + 43, y));

        }   

        for(int i1 = 0; i1 < 3; i1++){
            for(int l1 = 0; l1 < 9; l1++){
            	addSlot(new Slot(playerInventory, l1 + i1 * 9 + 9, 48 + l1 * 18, 147 + i1 * 18));
            }

        }

        for(int j1 = 0; j1 < 9; j1++){
        	addSlot(new Slot(playerInventory, j1, 48 + j1 * 18, 205));
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
