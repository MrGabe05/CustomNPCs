package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import noppes.npcs.CustomContainer;

public class ContainerNPCBankUpgrade extends ContainerNPCBankInterface
{

    public ContainerNPCBankUpgrade(int containerId, PlayerInventory playerInventory, int slot, int bankid) {
        super(CustomContainer.container_bankupgrade, containerId, playerInventory,slot,bankid);
    }

    public boolean isAvailable(){
    	return true;
    }
    public boolean canBeUpgraded(){
    	return true;
    }
    public int xOffset(){
    	return 54;
    }
    public int getRowNumber() {
		return 3;
	}
}
