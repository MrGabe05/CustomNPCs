package noppes.npcs.packets.server;

import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomItems;
import noppes.npcs.EventHooks;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.containers.ContainerNPCBankInterface;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.BankData;
import noppes.npcs.controllers.data.PlayerBankData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketBankUpgrade extends PacketServerBasic {

    public SPacketBankUpgrade() {

    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    public static void encode(SPacketBankUpgrade msg, PacketBuffer buf) {

    }

    public static SPacketBankUpgrade decode(PacketBuffer buf) {
        return new SPacketBankUpgrade();
    }

    @Override
    protected void handle() {
        if(npc.role.getType() != RoleType.BANK)
            return;

        Container con = player.containerMenu;
        if(con == null || !(con instanceof ContainerNPCBankInterface))
            return;

        ContainerNPCBankInterface container = (ContainerNPCBankInterface) con;
        Bank bank = BankController.getInstance().getBank(container.bankid);
        ItemStack item = bank.upgradeInventory.getItem(container.slot);
        if(item == null || item.isEmpty())
            return;

        int price = item.getCount();
        ItemStack currency = container.currencyMatrix.getItem(0);
        if(currency == null || currency.isEmpty() || price > currency.getCount())
            return;
        if(currency.getCount() - price == 0)
            container.currencyMatrix.setItem(0, ItemStack.EMPTY);
        else
            currency = currency.split(price);
        player.closeContainer();
        PlayerBankData data = PlayerDataController.instance.getBankData(player,bank.id);
        BankData bankData = data.getBank(bank.id);
        bankData.upgradedSlots.put(container.slot, true);

        RoleEvent.BankUpgradedEvent event = new RoleEvent.BankUpgradedEvent (player, npc.wrappedNPC, container.slot);
        EventHooks.onNPCRole(npc, event);

        bankData.openBankGui(player, npc, bank.id, container.slot);
    }
}