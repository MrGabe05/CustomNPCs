package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.gui.GuiAchievement;
import noppes.npcs.shared.common.PacketBasic;

public class PacketAchievement extends PacketBasic {
    private final ITextComponent title;
    private final ITextComponent message;
    private final int type;

    public PacketAchievement(ITextComponent title, ITextComponent message, int type) {
        this.title = title;
    	this.message = message;
        this.type = type;
    }

    public static void encode(PacketAchievement msg, PacketBuffer buf) {
        buf.writeComponent(msg.title);
        buf.writeComponent(msg.message);
        buf.writeInt(msg.type);
    }

    public static PacketAchievement decode(PacketBuffer buf) {
        return new PacketAchievement(buf.readComponent(), buf.readComponent(), buf.readInt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Minecraft.getInstance().getToasts().addToast(new GuiAchievement(title, message, type));
	}

}