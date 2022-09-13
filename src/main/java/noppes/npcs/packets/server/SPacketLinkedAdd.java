package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollList;

import java.util.Vector;

public class SPacketLinkedAdd extends PacketServerBasic {

    private final String name;

    public SPacketLinkedAdd(String name) {
        this.name = name;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_LINKED;
    }

    public static void encode(SPacketLinkedAdd msg, PacketBuffer buf) {
        buf.writeUtf(msg.name, 32767);
    }

    public static SPacketLinkedAdd decode(PacketBuffer buf) {
        return new SPacketLinkedAdd(buf.readUtf(32767));
    }

    @Override
    protected void handle() {
        LinkedNpcController.Instance.addData(name);

        Vector<String> list = new Vector<String>();
        for(LinkedNpcController.LinkedData data : LinkedNpcController.Instance.list)
            list.add(data.name);
        Packets.send(player, new PacketGuiScrollList(list));
    }
}