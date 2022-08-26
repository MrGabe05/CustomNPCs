package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.shared.client.gui.listeners.IGuiError;
import noppes.npcs.shared.common.PacketBasic;


public class PacketGuiError extends PacketBasic {
    private final int error;
	private final CompoundNBT data;

    public PacketGuiError(int error, CompoundNBT data) {
        this.error = error;
        this.data = data;
    }

    public static void encode(PacketGuiError msg, PacketBuffer buf) {
        buf.writeInt(msg.error);
        buf.writeNbt(msg.data);
    }

    public static PacketGuiError decode(PacketBuffer buf) {
        return new PacketGuiError(buf.readInt(), buf.readNbt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Screen gui = Minecraft.getInstance().screen;
        if(gui == null || !(gui instanceof IGuiError))
            return;

        ((IGuiError)gui).setError(error, data);
	}
}