package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.Iterator;

public class SPacketPlayerMailDelete extends PacketServerBasic {
    private final long time;
    private final String username;
    public SPacketPlayerMailDelete(long time, String username) {
        this.time = time;
        this.username = username;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketPlayerMailDelete msg, PacketBuffer buf) {
        buf.writeLong(msg.time);
        buf.writeUtf(msg.username);
    }

    public static SPacketPlayerMailDelete decode(PacketBuffer buf) {
        return new SPacketPlayerMailDelete(buf.readLong(), buf.readUtf(32767));
    }

    @Override
    protected void handle() {
        PlayerMailData data = PlayerData.get(player).mailData;

        Iterator<PlayerMail> it = data.playermail.iterator();
        while(it.hasNext()){
            PlayerMail mail = it.next();
            if(mail.time == time && mail.sender.equals(username)){
                it.remove();
            }
        }
        Packets.send(player, new PacketGuiData(data.saveNBTData(new CompoundNBT())));
    }
}