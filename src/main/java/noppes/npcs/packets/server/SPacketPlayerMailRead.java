package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.packets.PacketServerBasic;

import java.util.Iterator;

public class SPacketPlayerMailRead extends PacketServerBasic {
    private final long time;
    private final String username;
    public SPacketPlayerMailRead(long time, String username) {
        this.time = time;
        this.username = username;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketPlayerMailRead msg, PacketBuffer buf) {
        buf.writeLong(msg.time);
        buf.writeUtf(msg.username);
    }

    public static SPacketPlayerMailRead decode(PacketBuffer buf) {
        return new SPacketPlayerMailRead(buf.readLong(), buf.readUtf(32767));
    }

    @Override
    protected void handle() {
        PlayerMailData data = PlayerData.get(player).mailData;

        Iterator<PlayerMail> it = data.playermail.iterator();
        while(it.hasNext()){
            PlayerMail mail = it.next();
            if(!mail.beenRead && mail.time == time && mail.sender.equals(username)){
                if(mail.hasQuest())
                    PlayerQuestController.addActiveQuest(mail.getQuest(), player);
                mail.beenRead = true;
            }
        }
    }
}