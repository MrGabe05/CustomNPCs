package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketBankRemove extends PacketServerBasic {

    private int bank;

    public SPacketBankRemove(int bank) {
        this.bank = bank;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_BANK;
    }

    public static void encode(SPacketBankRemove msg, PacketBuffer buf) {
        buf.writeInt(msg.bank);
    }

    public static SPacketBankRemove decode(PacketBuffer buf) {
        return new SPacketBankRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        BankController.getInstance().removeBank(bank);
        SPacketBanksGet.sendBankDataAll(player);
        SPacketBankGet.sendBank(player,new Bank());
    }
}