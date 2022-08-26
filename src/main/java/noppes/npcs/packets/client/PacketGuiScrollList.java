package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.common.PacketBasic;

import java.util.Vector;


public class PacketGuiScrollList extends PacketBasic {
	private final Vector<String> data;

    public PacketGuiScrollList(Vector<String> data) {
    	this.data = data;
    }

    public static void encode(PacketGuiScrollList msg, PacketBuffer buf) {
        buf.writeInt(msg.data.size());
        for(String s : msg.data){
            buf.writeUtf(s);
        }
    }

    public static PacketGuiScrollList decode(PacketBuffer buf) {
        Vector<String> data = new Vector<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            data.add(buf.readUtf(32767));
        }
        return new PacketGuiScrollList(data);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Screen gui = Minecraft.getInstance().screen;
        if(gui instanceof GuiNPCInterface && ((GuiNPCInterface)gui).hasSubGui())
            gui = ((GuiNPCInterface)gui).getSubGui();
        if(gui == null || !(gui instanceof IScrollData))
            return;
        ((IScrollData)gui).setData(data,null);
	}
}