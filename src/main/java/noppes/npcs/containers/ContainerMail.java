package noppes.npcs.containers;

import java.util.Iterator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.CustomContainer;

public class ContainerMail extends ContainerNpcInterface{
	public static PlayerMail staticmail = new PlayerMail();
	public PlayerMail mail;
    public final boolean canEdit, canSend;
	
    public ContainerMail(int containerId, PlayerInventory playerInventory, boolean canEdit, boolean canSend){
    	super(CustomContainer.container_mail, containerId, playerInventory);
		mail = staticmail;
    	staticmail = new PlayerMail();
    	this.canEdit = canEdit;
    	this.canSend = canSend;
        playerInventory.startOpen(player);
        int k;

        for (k = 0; k < 4; ++k){
            this.addSlot(new SlotValid(mail, k , 179 + k * 24, 138, canEdit));
        }
        

        for (int j = 0; j < 3; ++j){
            for (k = 0; k < 9; ++k){
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 28 + k * 18, 175 + j * 18 ));
            }
        }

        for (int j = 0; j < 9; ++j){
            this.addSlot(new Slot(playerInventory, j, 28 + j * 18, 230 ));
        }
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack quickMoveStack(PlayerEntity par1PlayerEntity, int limbSwingAmount){
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(limbSwingAmount);

        if (slot != null && slot.hasItem()){
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (limbSwingAmount < 4){
                if (!this.moveItemStackTo(itemstack1, 4, this.slots.size(), true)){
                    return ItemStack.EMPTY;
                }
            }
            else if (!canEdit || !this.moveItemStackTo(itemstack1, 0, 4, false)){
                return null;
            }

            if (itemstack1.getCount() == 0){
                slot.set(ItemStack.EMPTY);
            }
            else{
                slot.setChanged();
            }
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void removed(PlayerEntity player){
        super.removed(player);
        if(!canEdit && !player.level.isClientSide){
			PlayerMailData data = PlayerData.get(player).mailData;
			Iterator<PlayerMail> it = data.playermail.iterator();
			while(it.hasNext()){
				PlayerMail mail = it.next();
				if(mail.time == this.mail.time && mail.sender.equals(this.mail.sender)){
					mail.readNBT(this.mail.writeNBT());
					break;
				}
			}
        }
    }

}
