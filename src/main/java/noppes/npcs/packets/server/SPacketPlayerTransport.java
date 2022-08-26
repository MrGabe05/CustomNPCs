package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.roles.RoleTransporter;

public class SPacketPlayerTransport extends PacketServerBasic {
    private final String name;

    public SPacketPlayerTransport(String name) {
        this.name = name;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    public static void encode(SPacketPlayerTransport msg, PacketBuffer buf) {
        buf.writeUtf(msg.name);
    }

    public static SPacketPlayerTransport decode(PacketBuffer buf) {
        return new SPacketPlayerTransport(buf.readUtf(32767));
    }

    @Override
    protected void handle() {
        if(npc.role.getType() != RoleType.TRANSPORTER)
            return;
        ((RoleTransporter)npc.role).transport(player, name);
    }
}