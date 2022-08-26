package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomItems;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.packets.PacketServerBasic;

import java.util.HashMap;
import java.util.Map;

public class SPacketFactionsGet extends PacketServerBasic {

    public SPacketFactionsGet() {

    }

    public boolean toolAllowed(ItemStack item){
        return true;
    }


    public static void encode(SPacketFactionsGet msg, PacketBuffer buf) {

    }

    public static SPacketFactionsGet decode(PacketBuffer buf) {
        return new SPacketFactionsGet();
    }

    @Override
    protected void handle() {
        sendFactionDataAll(player);
    }

    public static void sendFactionDataAll(ServerPlayerEntity player) {
        Map<String,Integer> map = new HashMap<String,Integer>();
        for(Faction faction : FactionController.instance.factions.values()){
            map.put(faction.name, faction.id);
        }
        NoppesUtilServer.sendScrollData(player, map);
    }

}