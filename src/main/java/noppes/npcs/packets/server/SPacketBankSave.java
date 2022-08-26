package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketBankSave extends PacketServerBasic {

    private CompoundNBT data;

    public SPacketBankSave(CompoundNBT data) {
        this.data = data;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_BANK;
    }

    public static void encode(SPacketBankSave msg, PacketBuffer buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketBankSave decode(PacketBuffer buf) {
        return new SPacketBankSave(buf.readNbt());
    }

    @Override
    protected void handle() {
        Bank bank = new Bank();
        bank.readAdditionalSaveData(data);
        BankController.getInstance().saveBank(bank);
        SPacketBanksGet.sendBankDataAll(player);
        SPacketBankGet.sendBank(player,bank);
    }
}