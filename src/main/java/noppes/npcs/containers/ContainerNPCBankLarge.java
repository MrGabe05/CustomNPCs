package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import noppes.npcs.CustomContainer;

public class ContainerNPCBankLarge extends ContainerNPCBankInterface
{

    public ContainerNPCBankLarge(int containerId, PlayerInventory playerInventory, int slot, int bankid)
    {
    	super(CustomContainer.container_banklarge, containerId, playerInventory,slot,bankid);
    }
    
    public boolean isUpgraded(){
    	return true;
    }
    public boolean isAvailable(){
    	return true;
    }
    public int getRowNumber() {
		return 6;
	}
}
