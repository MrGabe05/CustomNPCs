package noppes.npcs.packets.client;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketBasic;


public class PacketGuiOpen extends PacketBasic {
	private final EnumGuiType gui;
	private final BlockPos pos;

    public PacketGuiOpen(EnumGuiType gui, BlockPos pos) {
        this.gui = gui;
        this.pos = pos;
    }

    public static void encode(PacketGuiOpen msg, PacketBuffer buf) {
        buf.writeEnum(msg.gui);
        buf.writeBlockPos(msg.pos);
    }

    public static PacketGuiOpen decode(PacketBuffer buf) {
        return new PacketGuiOpen(buf.readEnum(EnumGuiType.class), buf.readBlockPos());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        try{
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeBlockPos(pos);
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(ClientProxy.getGui(gui, NoppesUtil.getLastNpc(), buffer));
        }
        catch(Exception e){
            LogWriter.error("Error in gui: " + gui, e);
        }
	}
}