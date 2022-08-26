package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketQuestRemove extends PacketServerBasic {
    private int id;
    public SPacketQuestRemove(int id) {
        this.id = id;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_QUEST;
    }

    public static void encode(SPacketQuestRemove msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketQuestRemove decode(PacketBuffer buf) {
        return new SPacketQuestRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        Quest quest = QuestController.instance.quests.get(id);
        if(quest != null){
            QuestController.instance.removeQuest(quest);
            Packets.send(player, new PacketGuiUpdate());
        }
    }
}