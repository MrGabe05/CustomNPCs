package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.NpcGuiHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.shared.common.PacketBasic;


public class PacketGuiUpdate extends PacketBasic {

    public PacketGuiUpdate() {

    }

    public static void encode(PacketGuiUpdate msg, PacketBuffer buf) {

    }

    public static PacketGuiUpdate decode(PacketBuffer buf) {
        return new PacketGuiUpdate();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Screen gui = Minecraft.getInstance().screen;
        if(gui == null)
            return;
        NpcGuiHelper.initGui(gui);
	}
}