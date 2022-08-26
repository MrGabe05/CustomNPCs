package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;

public class ContainerNPCFollowerHire extends ContainerNpcInterface
{
    public Inventory currencyMatrix;
	public RoleFollower role;

    public ContainerNPCFollowerHire(int containerId, PlayerInventory playerInventory, int entityId){
        super(CustomContainer.container_followerhire, containerId, playerInventory);
        EntityNPCInterface npc = (EntityNPCInterface) player.level.getEntity(entityId);
        role = (RoleFollower) npc.role;

    	currencyMatrix = new Inventory(1);
    	addSlot(new SlotNpcMercenaryCurrency(role,currencyMatrix, 0, 44, 35));
        
        for(int i1 = 0; i1 < 3; i1++)
        {
            for(int l1 = 0; l1 < 9; l1++)
            {
            	addSlot(new Slot(player.inventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 84 + i1 * 18));
            }

        }

        for(int j1 = 0; j1 < 9; j1++)
        {
        	addSlot(new Slot(player.inventory, j1, 8 + j1 * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity par1PlayerEntity, int i)
    {
        return ItemStack.EMPTY;
    }
    @Override
    public void removed(PlayerEntity entityplayer)
    {
        super.removed(entityplayer);

        if (!entityplayer.level.isClientSide){
	        ItemStack itemstack = currencyMatrix.removeItemNoUpdate(0);
	        if(!NoppesUtilServer.IsItemStackNull(itemstack) && !entityplayer.level.isClientSide)
	        {
	            entityplayer.spawnAtLocation(itemstack,0f);
	        }
        }
    }
}
