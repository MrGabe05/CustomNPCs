package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.packets.PacketServerBasic;

import java.util.HashMap;

public class SPacketTransportGet extends PacketServerBasic {
    private final int id;
    public SPacketTransportGet(int id) {
        this.id = id;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketTransportGet msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketTransportGet decode(PacketBuffer buf) {
        return new SPacketTransportGet(buf.readInt());
    }

    @Override
    protected void handle() {
        sendTransportData(player, id);
    }

    public static void sendTransportData(ServerPlayerEntity player, int categoryid) {
        TransportCategory category = TransportController.getInstance().categories.get(categoryid);
        if(category == null)
            return;
        HashMap<String,Integer> map = new HashMap<String,Integer>();
        for(TransportLocation transport : category.locations.values()){
            map.put(transport.name, transport.id);
        }
        NoppesUtilServer.sendScrollData(player, map);
    }
}