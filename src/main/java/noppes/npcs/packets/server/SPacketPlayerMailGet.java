package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketPlayerMailGet extends PacketServerBasic {

    public SPacketPlayerMailGet() {

    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketPlayerMailGet msg, PacketBuffer buf) {

    }

    public static SPacketPlayerMailGet decode(PacketBuffer buf) {
        return new SPacketPlayerMailGet();
    }

    @Override
    protected void handle() {
        PlayerMailData data = PlayerData.get(player).mailData;
        Packets.send(player, new PacketGuiData(data.saveNBTData(new CompoundNBT())));
    }
}