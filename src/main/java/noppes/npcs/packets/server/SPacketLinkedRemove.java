package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollList;

import java.util.Vector;

public class SPacketLinkedRemove extends PacketServerBasic {

    private String name;

    public SPacketLinkedRemove(String name) {
        this.name = name;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_LINKED;
    }

    public static void encode(SPacketLinkedRemove msg, PacketBuffer buf) {
        buf.writeUtf(msg.name, 32767);
    }

    public static SPacketLinkedRemove decode(PacketBuffer buf) {
        return new SPacketLinkedRemove(buf.readUtf(32767));
    }

    @Override
    protected void handle() {
        LinkedNpcController.Instance.removeData(name);

        Vector<String> list = new Vector<String>();
        for(LinkedNpcController.LinkedData data : LinkedNpcController.Instance.list)
            list.add(data.name);
        Packets.send(player, new PacketGuiScrollList(list));
    }
}