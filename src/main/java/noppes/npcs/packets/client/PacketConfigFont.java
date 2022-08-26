package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.common.PacketBasic;


public class PacketConfigFont extends PacketBasic {
    private final String font;
    private final int size;

    public PacketConfigFont(String font, int size) {
        this.font = font;
        this.size = size;
    }

    public static void encode(PacketConfigFont msg, PacketBuffer buf) {
        buf.writeUtf(msg.font);
        buf.writeInt(msg.size);
    }

    public static PacketConfigFont decode(PacketBuffer buf) {
        return new PacketConfigFont(buf.readUtf(32767), buf.readInt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Runnable run = () -> {
            if (!font.isEmpty()) {
                CustomNpcs.FontType = font;
                CustomNpcs.FontSize = size;
                ClientProxy.Font.clear();
                ClientProxy.Font = new ClientProxy.FontContainer(CustomNpcs.FontType, CustomNpcs.FontSize);
                CustomNpcs.Config.updateConfig();
                player.sendMessage(new TranslationTextComponent("Font set to %s", ClientProxy.Font.getName()), Util.NIL_UUID);
            } else
                player.sendMessage(new TranslationTextComponent("Current font is "+ ClientProxy.Font.getName()), Util.NIL_UUID);
        };
        Minecraft.getInstance().submit(run);
	}
}