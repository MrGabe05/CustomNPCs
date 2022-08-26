package noppes.npcs.packets.client;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPlaySound extends PacketBasic {
	private final String name;
    private final BlockPos pos;
    private final float volume;
    private final float pitch;

    public PacketPlaySound(String name, BlockPos pos, float volume, float pitch) {
    	this.name = name;
    	this.pos = pos;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static void encode(PacketPlaySound msg, PacketBuffer buf) {
    	buf.writeUtf(msg.name);
        buf.writeBlockPos(msg.pos);
        buf.writeFloat(msg.volume);
        buf.writeFloat(msg.pitch);
    }

    public static PacketPlaySound decode(PacketBuffer buf) {
        return new PacketPlaySound(buf.readUtf(32767), buf.readBlockPos(), buf.readFloat(), buf.readFloat());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        MusicController.Instance.playSound(SoundCategory.VOICE, name, pos, volume, pitch);
    }
}