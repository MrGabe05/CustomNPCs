package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.shared.client.gui.listeners.IGuiClose;
import noppes.npcs.shared.common.PacketBasic;


public class PacketGuiClose extends PacketBasic {
	private final CompoundNBT data;

    public PacketGuiClose(CompoundNBT data) {
        this.data = data;
    }

    public PacketGuiClose() {
        this(new CompoundNBT());
    }

    public static void encode(PacketGuiClose msg, PacketBuffer buf) {
        buf.writeNbt(msg.data);
    }

    public static PacketGuiClose decode(PacketBuffer buf) {
        return new PacketGuiClose(buf.readNbt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Screen gui = Minecraft.getInstance().screen;
        if(gui == null)
            return;

        if(gui instanceof IGuiClose){
            ((IGuiClose)gui).setClose(data);
        }

        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(null);
        mc.mouseHandler.grabMouse();
	}
}