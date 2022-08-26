package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollList;
import noppes.npcs.packets.client.PacketGuiScrollSelected;

import java.util.Vector;

public class SPacketLinkedGet extends PacketServerBasic {

    public SPacketLinkedGet() {

    }

    public static void encode(SPacketLinkedGet msg, PacketBuffer buf) {

    }

    public static SPacketLinkedGet decode(PacketBuffer buf) {
        return new SPacketLinkedGet();
    }

    @Override
    protected void handle() {
        Vector<String> list = new Vector<String>();
        for(LinkedNpcController.LinkedData data : LinkedNpcController.Instance.list)
            list.add(data.name);
        Packets.send(player, new PacketGuiScrollList(list));
        if(npc != null)
            Packets.send(player, new PacketGuiScrollSelected(npc.linkedName));
    }
}