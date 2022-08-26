package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketTransportRemove extends PacketServerBasic {
    private int id;
    public SPacketTransportRemove(int id) {
        this.id = id;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_TRANSPORT;
    }

    public static void encode(SPacketTransportRemove msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketTransportRemove decode(PacketBuffer buf) {
        return new SPacketTransportRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        TransportLocation loc = TransportController.getInstance().removeLocation(id);
        if(loc != null)
            SPacketTransportGet.sendTransportData(player,loc.category.id);
    }
}