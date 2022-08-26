package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.packets.IPacketServer;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

import java.io.IOException;

public class SPacketQuestSave extends IPacketServer {
    private int categoryId;
    private CompoundNBT data;
    public SPacketQuestSave(int categoryId, CompoundNBT data) {
        this.data = data;
        this.categoryId = categoryId;
    }

    public SPacketQuestSave(){

    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_QUEST;
    }


    @Override
    public void handle() {
        QuestCategory category = QuestController.instance.categories.get(categoryId);
        if(category == null)
            return;
        Quest quest = new Quest(category);
        quest.readNBT(data);
        QuestController.instance.saveQuest(category, quest);
        Packets.send(player, new PacketGuiUpdate());
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        categoryId = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeInt(categoryId);
        buf.writeNbt(data);
    }
}