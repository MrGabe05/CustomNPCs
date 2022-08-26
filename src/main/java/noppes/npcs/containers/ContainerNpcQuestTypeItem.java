package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.quests.QuestItem;

public class ContainerNpcQuestTypeItem extends Container{

    public ContainerNpcQuestTypeItem(int containerId, PlayerInventory playerInventory){
        super(CustomContainer.container_questtypeitem, containerId);
    	Quest quest = NoppesUtilServer.getEditingQuest(playerInventory.player);
        for(int i1 = 0; i1 < 3; i1++)
        	addSlot(new Slot(((QuestItem)quest.questInterface).items, i1, 44, 39 + i1 * 25));
                
        for(int i1 = 0; i1 < 3; i1++)
        {
            for(int l1 = 0; l1 < 9; l1++)
            {
            	addSlot(new Slot(playerInventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 113 + i1 * 18));
            }

        }

        for(int j1 = 0; j1 < 9; j1++)
        {
        	addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 171));
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity par1PlayerEntity, int i){
        return null;
    }
    
	@Override
	public boolean stillValid(PlayerEntity entityplayer) {
		return true;
	}
}
