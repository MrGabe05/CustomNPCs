package noppes.npcs.api.wrapper.gui;

import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;

public class CustomGuiItemSlotWrapper extends CustomGuiComponentWrapper implements IItemSlot {

    IItemStack stack = ItemStackWrapper.AIR;

    public CustomGuiItemSlotWrapper(){}

    public CustomGuiItemSlotWrapper(int x, int y, IItemStack stack) {
        setPos(x,y);
        setStack(stack);
    }

    @Override
    public boolean hasStack() {
        return !stack.isEmpty();
    }

    @Override
    public IItemStack getStack() {
        return stack;
    }

    @Override
    public IItemSlot setStack(IItemStack itemStack) {
        if(itemStack == null) {
            this.stack = ItemStackWrapper.AIR;
        }
        else {
            this.stack = itemStack;
        }
        return this;
    }

    @Override
    public Slot getMCSlot() {
        return null;
    }

    @Override
    public int getType() {
        return GuiComponentType.ITEM_SLOT;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT nbt) {
        super.toNBT(nbt);
        nbt.put("stack", stack.getMCItemStack().serializeNBT());
        return nbt;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(CompoundNBT nbt) {
        super.fromNBT(nbt);
        setStack(NpcAPI.Instance().getIItemStack(ItemStack.of(nbt.getCompound("stack"))));
        return this;
    }
}
