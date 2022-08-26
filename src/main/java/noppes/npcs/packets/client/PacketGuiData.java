package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;


public class PacketGuiData extends PacketBasic {
	private final CompoundNBT data;

    public PacketGuiData(CompoundNBT data) {
        this.data = data;
    }

    public static void encode(PacketGuiData msg, PacketBuffer buf) {
        buf.writeNbt(msg.data);
    }

    public static PacketGuiData decode(PacketBuffer buf) {
        return new PacketGuiData(buf.readNbt(new NBTSizeTracker(Long.MAX_VALUE)));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Screen gui = Minecraft.getInstance().screen;
        if(gui == null)
            return;

        if(gui instanceof IGuiInterface && ((IGuiInterface)gui).hasSubGui()){
            gui = ((IGuiInterface) gui).getSubGui();
        }
        if(gui instanceof IGuiData)
            ((IGuiData)gui).setGuiData(data);
	}
}