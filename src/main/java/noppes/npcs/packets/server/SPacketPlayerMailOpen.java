package noppes.npcs.packets.server;

import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.packets.PacketServerBasic;

import java.util.Iterator;

public class SPacketPlayerMailOpen extends PacketServerBasic {
    private final long time;
    private final String username;
    public SPacketPlayerMailOpen(long time, String username) {
        this.time = time;
        this.username = username;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketPlayerMailOpen msg, PacketBuffer buf) {
        buf.writeLong(msg.time);
        buf.writeUtf(msg.username);
    }

    public static SPacketPlayerMailOpen decode(PacketBuffer buf) {
        return new SPacketPlayerMailOpen(buf.readLong(), buf.readUtf(32767));
    }

    @Override
    protected void handle() {
        player.closeContainer();
        PlayerMailData data = PlayerData.get(player).mailData;

        Iterator<PlayerMail> it = data.playermail.iterator();
        while(it.hasNext()){
            PlayerMail mail = it.next();
            if(mail.time == time && mail.sender.equals(username)){
                ContainerMail.staticmail = mail;
                NoppesUtilServer.openContainerGui(player, EnumGuiType.PlayerMailman, (buf) -> {
                    buf.writeBoolean(false);
                    buf.writeBoolean(false);
                });
                break;
            }
        }
    }
}