package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketTransportCategoryRemove extends PacketServerBasic {
    private int id;
    public SPacketTransportCategoryRemove(int id) {
        this.id = id;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_TRANSPORT;
    }

    public static void encode(SPacketTransportCategoryRemove msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketTransportCategoryRemove decode(PacketBuffer buf) {
        return new SPacketTransportCategoryRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        TransportController.getInstance().removeCategory(id);
        SPacketTransportCategoriesGet.sendTransportCategoryData(player);
    }
}