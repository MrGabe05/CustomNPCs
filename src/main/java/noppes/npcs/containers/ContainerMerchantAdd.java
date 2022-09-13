package noppes.npcs.containers;

import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.ServerEventsHandler;

public class ContainerMerchantAdd extends ContainerNpcInterface{
    private final IMerchant theMerchant;
    private final Inventory merchantInventory;

    private final World level;

    public ContainerMerchantAdd(int containerId, PlayerInventory playerInventory)
    {
    	super(CustomContainer.container_merchantadd, containerId, playerInventory);
        this.theMerchant = ServerEventsHandler.Merchant;
        this.level = playerInventory.player.level;
        this.merchantInventory = new Inventory(3);
        this.addSlot(new Slot(this.merchantInventory, 0, 36, 53));
        this.addSlot(new Slot(this.merchantInventory, 1, 62, 53));
        this.addSlot(new Slot(this.merchantInventory, 2, 120, 53));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
//
//    @Override
//    public void broadcastChanges()
//    {
//        super.broadcastChanges();
//    }
//
//    @Override
//    public void slotsChanged(IInventory par1IInventory)
//    {
//        //this.merchantInventory.resetRecipeAndSlots();
//        super.slotsChanged(par1IInventory);
//    }
//
//
//    public void setCurrentRecipeIndex(int par1)
//    {
//        //this.merchantInventory.setCurrentRecipeIndex(par1);
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void updateProgressBar(int par1, int limbSwingAmount) {}


    @Override
    public ItemStack quickMoveStack(PlayerEntity par1PlayerEntity, int limbSwingAmount)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(limbSwingAmount);

        if (slot != null && slot.hasItem())
        {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (limbSwingAmount != 0 && limbSwingAmount != 1 && limbSwingAmount != 2)
            {
                if (limbSwingAmount >= 3 && limbSwingAmount < 30)
                {
                    if (!this.moveItemStackTo(itemstack1, 30, 39, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (limbSwingAmount >= 30 && limbSwingAmount < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(itemstack1, 3, 39, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0)
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(par1PlayerEntity, itemstack1);
        }

        return itemstack;
    }

    @Override
    public void removed(PlayerEntity par1PlayerEntity)
    {
        super.removed(par1PlayerEntity);
        //this.theMerchant.setCustomer((PlayerEntity)null);
        super.removed(par1PlayerEntity);

        if (!this.level.isClientSide)
        {
            ItemStack itemstack = this.merchantInventory.removeItemNoUpdate(0);

            if (!NoppesUtilServer.IsItemStackNull(itemstack))
            {
                par1PlayerEntity.drop(itemstack, false);
            }

            itemstack = this.merchantInventory.removeItemNoUpdate(1);

            if (!NoppesUtilServer.IsItemStackNull(itemstack))
            {
                par1PlayerEntity.drop(itemstack, false);
            }
        }
    }
}
