package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import noppes.npcs.CustomContainer;

public class ContainerNPCBankSmall extends ContainerNPCBankInterface
{

    public ContainerNPCBankSmall(int containerId, PlayerInventory playerInventory, int slot, int bankid) {
        super(CustomContainer.container_banksmall, containerId, playerInventory,slot,bankid);
    }

    public boolean isAvailable(){
    	return true;
    }
    public int getRowNumber() {
		return 3;
	}
}
