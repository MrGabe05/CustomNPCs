package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.packets.PacketServerBasic;

import java.util.HashMap;

public class SPacketTransportCategoriesGet extends PacketServerBasic {
    public SPacketTransportCategoriesGet() {

    }

    public static void encode(SPacketTransportCategoriesGet msg, PacketBuffer buf) {

    }

    public static SPacketTransportCategoriesGet decode(PacketBuffer buf) {
        return new SPacketTransportCategoriesGet();
    }

    @Override
    protected void handle() {
        sendTransportCategoryData(player);
    }

    public static void sendTransportCategoryData(ServerPlayerEntity player) {
        HashMap<String,Integer> map = new HashMap<String,Integer>();
        for(TransportCategory category : TransportController.getInstance().categories.values()){
            map.put(category.title, category.id);
        }
        NoppesUtilServer.sendScrollData(player, map);
    }
}