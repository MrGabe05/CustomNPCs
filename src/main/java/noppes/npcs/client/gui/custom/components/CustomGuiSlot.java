package noppes.npcs.client.gui.custom.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.containers.ContainerCustomGui;

public class CustomGuiSlot extends Slot {

    PlayerEntity player;
    IItemSlot slot;

    public CustomGuiSlot(IInventory inventoryIn, int index, IItemSlot slot, PlayerEntity player) {
        super(inventoryIn, index, slot.getPosX(), slot.getPosY());
        this.player = player;
        this.slot = slot;
    }

    @Override
    public void setChanged() {
        if(!player.level.isClientSide){
            if(getItem() != slot.getStack().getMCItemStack()) {
                slot.setStack(NpcAPI.Instance().getIItemStack(getItem()));

                if(player.containerMenu instanceof ContainerCustomGui) {
                    EventHooks.onCustomGuiSlot((PlayerWrapper)NpcAPI.Instance().getIEntity(player), ((ContainerCustomGui)player.containerMenu).customGui, getSlotIndex());
                }
            }
        }
        super.setChanged();
    }

}
