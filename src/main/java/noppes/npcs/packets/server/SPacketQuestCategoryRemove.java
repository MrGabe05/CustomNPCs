package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketQuestCategoryRemove extends PacketServerBasic {
    private final int id;
    public SPacketQuestCategoryRemove(int id) {
        this.id = id;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_QUEST;
    }

    public static void encode(SPacketQuestCategoryRemove msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketQuestCategoryRemove decode(PacketBuffer buf) {
        return new SPacketQuestCategoryRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        QuestController.instance.removeCategory(id);
        Packets.send(player, new PacketGuiUpdate());
    }
}