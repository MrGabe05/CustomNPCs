package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomItems;
import noppes.npcs.controllers.data.*;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketQuestCompletionCheckAll extends PacketServerBasic {

    public SPacketQuestCompletionCheckAll() {

    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketQuestCompletionCheckAll msg, PacketBuffer buf) {

    }

    public static SPacketQuestCompletionCheckAll decode(PacketBuffer buf) {
        return new SPacketQuestCompletionCheckAll();
    }

    @Override
    protected void handle() {
        PlayerQuestData playerdata = PlayerData.get(player).questData;
        playerdata.checkQuestCompletion(player, -1);
    }
}