package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.containers.ContainerManageBanks;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketBankGet extends PacketServerBasic {

    private final int bank;

    public SPacketBankGet(int bank) {
        this.bank = bank;
    }


    public static void encode(SPacketBankGet msg, PacketBuffer buf) {
        buf.writeInt(msg.bank);
    }

    public static SPacketBankGet decode(PacketBuffer buf) {
        return new SPacketBankGet(buf.readInt());
    }

    @Override
    protected void handle() {
        sendBank(player,BankController.getInstance().getBank(this.bank));
    }

    public static void sendBank(ServerPlayerEntity player, Bank bank) {
        CompoundNBT compound = new CompoundNBT();
        bank.addAdditionalSaveData(compound);
        Packets.send(player, new PacketGuiData(compound));

        if(player.containerMenu instanceof ContainerManageBanks){
            ((ContainerManageBanks)player.containerMenu).setBank(bank);
        }
        player.refreshContainer(player.containerMenu, player.containerMenu.getItems());
    }
}