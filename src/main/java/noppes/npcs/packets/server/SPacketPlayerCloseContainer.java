package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketPlayerCloseContainer extends PacketServerBasic {

    public SPacketPlayerCloseContainer() {
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketPlayerCloseContainer msg, PacketBuffer buf) {
    }

    public static SPacketPlayerCloseContainer decode(PacketBuffer buf) {
        return new SPacketPlayerCloseContainer();
    }

    @Override
    protected void handle() {
        player.closeContainer();
    }
}