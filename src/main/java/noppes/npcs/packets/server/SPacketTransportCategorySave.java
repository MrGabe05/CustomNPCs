package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketTransportCategorySave extends PacketServerBasic {
    private final int id;
    private final String name;
    public SPacketTransportCategorySave(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_TRANSPORT;
    }

    public static void encode(SPacketTransportCategorySave msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
        buf.writeUtf(msg.name);
    }

    public static SPacketTransportCategorySave decode(PacketBuffer buf) {
        return new SPacketTransportCategorySave(buf.readInt(), buf.readUtf(32767));
    }

    @Override
    protected void handle() {
        TransportController.getInstance().saveCategory(name, id);
    }
}