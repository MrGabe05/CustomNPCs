package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import noppes.npcs.CustomContainer;

public class ContainerNPCBankUnlock extends ContainerNPCBankInterface
{

    public ContainerNPCBankUnlock(int containerId, PlayerInventory playerInventory, int slot, int bankid) {
        super(CustomContainer.container_bankunlock, containerId, playerInventory,slot,bankid);
    }
}
