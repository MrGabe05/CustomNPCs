package noppes.npcs.packets.client;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.shared.common.PacketBasic;

public class PacketChat extends PacketBasic {
    private final ITextComponent message;

    public PacketChat(ITextComponent message) {
    	this.message = message;
    }

    public static void encode(PacketChat msg, PacketBuffer buf) {
        buf.writeComponent(msg.message);
    }

    public static PacketChat decode(PacketBuffer buf) {
        return new PacketChat(buf.readComponent());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        player.sendMessage(message, Util.NIL_UUID);
	}
}