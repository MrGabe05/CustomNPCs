package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.EventHooks;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketPlayerSoundPlays extends PacketServerBasic {
    private final String sound;
    private final String category;
    private final boolean looping;

    public SPacketPlayerSoundPlays(String sound, String category, boolean looping) {
        this.sound = sound;
        this.category = category;
        this.looping = looping;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketPlayerSoundPlays msg, PacketBuffer buf) {
        buf.writeUtf(msg.sound == null ? "" : msg.sound);
        buf.writeUtf(msg.category == null ? "" : msg.category);
        buf.writeBoolean(msg.looping);
    }

    public static SPacketPlayerSoundPlays decode(PacketBuffer buf) {
        return new SPacketPlayerSoundPlays(buf.readUtf(), buf.readUtf(), buf.readBoolean());
    }

    @Override
    protected void handle() {
        EventHooks.onPlayerPlaySound(player, sound, category, looping);
    }
}