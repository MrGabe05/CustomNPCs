package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;

public class ContainerNPCFollower extends ContainerNpcInterface
{
    public InventoryNPC currencyMatrix;
	public RoleFollower role;

    public ContainerNPCFollower(int containerId, PlayerInventory playerInventory, int entityId){
        super(CustomContainer.container_follower, containerId, playerInventory);
        EntityNPCInterface npc = (EntityNPCInterface) player.level.getEntity(entityId);
        role = (RoleFollower) npc.role;

    	currencyMatrix = new InventoryNPC("currency", 1,this);
    	addSlot(new SlotNpcMercenaryCurrency(role,currencyMatrix, 0, 26, 9));
        
        for(int j1 = 0; j1 < 9; j1++)
        {
        	addSlot(new Slot(player.inventory, j1, 8 + j1 * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity par1PlayerEntity, int i){
        return ItemStack.EMPTY;
    }
    
    @Override
    public void removed(PlayerEntity entityplayer){
        super.removed(entityplayer);
        if (!entityplayer.level.isClientSide){
	        ItemStack itemstack = currencyMatrix.removeItemNoUpdate(0);
	        if(!NoppesUtilServer.IsItemStackNull(itemstack) && !entityplayer.level.isClientSide){
	            entityplayer.spawnAtLocation(itemstack,0f);
	        }
        }

    }
}
