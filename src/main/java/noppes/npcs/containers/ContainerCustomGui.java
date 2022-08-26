package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.client.gui.custom.components.CustomGuiSlot;
import noppes.npcs.CustomContainer;
import noppes.npcs.util.CustomNPCsScheduler;

public class ContainerCustomGui extends Container {

    public CustomGuiWrapper customGui;
    public IInventory guiInventory;

    public ContainerCustomGui(int containerId, PlayerInventory playerInventory, int size) {
        super(CustomContainer.container_customgui, containerId);
        this.guiInventory = new Inventory(size);
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }

    public void setGui(CustomGuiWrapper gui, PlayerEntity player) {
        this.customGui = gui;
        int index = 0;
        for(IItemSlot slot : customGui.getSlots()) {
            this.addSlot(new CustomGuiSlot(this.guiInventory, index, slot, player));
            this.guiInventory.setItem(index, slot.getStack().getMCItemStack());
            index++;
        }
        if(customGui.getShowPlayerInv()) {
            addPlayerInventory(player, customGui.getPlayerInvX(), customGui.getPlayerInvY());
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem())
        {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < this.guiInventory.getContainerSize())
            {
                if (!this.moveItemStackTo(itemstack1, this.guiInventory.getContainerSize(), this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(itemstack1, 0, this.guiInventory.getContainerSize(), false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        if(slotId < 0)
            return super.clicked(slotId, dragType, clickTypeIn, player);
        if(!player.level.isClientSide && slotId >= 0) {
            Slot slot = getSlot(slotId);
            if(!EventHooks.onCustomGuiSlotClicked((PlayerWrapper)NpcAPI.Instance().getIEntity(player), ((ContainerCustomGui)player.containerMenu).customGui, slotId, dragType, clickTypeIn.toString())) {
                ItemStack item = super.clicked(slotId, dragType, clickTypeIn, player);
                ServerPlayerEntity p = (ServerPlayerEntity) player;
                CustomNPCsScheduler.runTack(() -> {p.refreshContainer(this);}, 10);

                return item;
            }

        }
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(PlayerEntity player){
        super.removed(player);
        if(!player.level.isClientSide){
            EventHooks.onCustomGuiClose((PlayerWrapper) NpcAPI.Instance().getIEntity(player), customGui);
        }
    }

    void addPlayerInventory(PlayerEntity player, int x, int y) {
        for (int row = 0; row < 3; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                this.addSlot(new Slot(player.inventory, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }

        for (int row = 0; row < 9; ++row)
        {
            this.addSlot(new Slot(player.inventory, row, x + row * 18, y + 58));
        }
    }

}
