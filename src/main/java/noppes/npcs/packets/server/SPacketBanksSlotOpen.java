package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.BankData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketBanksSlotOpen extends PacketServerBasic {
    private final int slot;
    private final int bankId;

    public SPacketBanksSlotOpen(int slot, int bankId) {
        this.slot = slot;
        this.bankId = bankId;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    public static void encode(SPacketBanksSlotOpen msg, PacketBuffer buf) {
        buf.writeInt(msg.slot);
        buf.writeInt(msg.bankId);
    }

    public static SPacketBanksSlotOpen decode(PacketBuffer buf) {
        return new SPacketBanksSlotOpen(buf.readInt(), buf.readInt());
    }

    @Override
    protected void handle() {
        if(npc.role.getType() != RoleType.BANK)
            return;
        BankData data = PlayerDataController.instance.getBankData(player,bankId).getBankOrDefault(bankId);
        data.openBankGui(player, npc, bankId, slot);
    }

}