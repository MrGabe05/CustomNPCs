package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.packets.PacketServerBasic;

import java.util.HashMap;
import java.util.Map;

public class SPacketBanksGet extends PacketServerBasic {
    public SPacketBanksGet() {

    }

    public static void encode(SPacketBanksGet msg, PacketBuffer buf) {

    }

    public static SPacketBanksGet decode(PacketBuffer buf) {
        return new SPacketBanksGet();
    }

    @Override
    protected void handle() {
        sendBankDataAll(player);
    }

    public static void sendBankDataAll(ServerPlayerEntity player) {
        Map<String,Integer> map = new HashMap<String,Integer>();
        for(Bank bank : BankController.getInstance().banks.values()){
            map.put(bank.name, bank.id);
        }
        NoppesUtilServer.sendScrollData(player, map);
    }
}