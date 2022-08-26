package net.minecraft.network;

import noppes.npcs.mixin.ProtocolTypeMixin;

import java.util.function.Supplier;

public class CNpcsNetworkHelper {

    public static void addPacket(Class c, Supplier sup){
        ProtocolTypeMixin type = (ProtocolTypeMixin)(Object)ProtocolType.PLAY;
        ProtocolType.PacketList list = (ProtocolType.PacketList) type.getFows().get(PacketDirection.SERVERBOUND);
        list.addPacket(c, sup);

        type.getProtocols().put(c, ProtocolType.PLAY);
    }
}
