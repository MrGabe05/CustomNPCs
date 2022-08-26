package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.entity.EntityNPCInterface;

public class ContainerNPCInv extends Container{

    public ContainerNPCInv(int containerId, PlayerInventory playerInventory, int entityId){
        super(CustomContainer.container_inv, containerId);
        EntityNPCInterface npc = (EntityNPCInterface) playerInventory.player.level.getEntity(entityId);
    	addSlot(new SlotNPCArmor(npc.inventory, 0, 9, 22, EquipmentSlotType.HEAD));
    	addSlot(new SlotNPCArmor(npc.inventory, 1, 9, 40, EquipmentSlotType.CHEST));
    	addSlot(new SlotNPCArmor(npc.inventory, 2, 9, 58, EquipmentSlotType.LEGS));
    	addSlot(new SlotNPCArmor(npc.inventory, 3, 9, 76, EquipmentSlotType.FEET));

        addSlot(new Slot(npc.inventory, 4, 81, 22));
        addSlot(new Slot(npc.inventory, 5, 81, 40));
        addSlot(new Slot(npc.inventory, 6, 81, 58));

        for(int l = 0; l < 9; l++){
        	addSlot(new Slot(npc.inventory,  l + 7 , 191 , 16 + l * 21));
        }

        for(int i1 = 0; i1 < 3; i1++){
            for(int l1 = 0; l1 < 9; l1++){
            	addSlot(new Slot(playerInventory, l1 + i1 * 9 + 9, l1 * 18 + 8, 113 + i1 * 18));
            }

        }

        for(int j1 = 0; j1 < 9; j1++){
        	addSlot(new Slot(playerInventory, j1, j1 * 18 + 8, 171));
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
