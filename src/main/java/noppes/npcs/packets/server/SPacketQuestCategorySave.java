package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.packets.IPacketServer;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

import java.io.IOException;

public class SPacketQuestCategorySave extends IPacketServer {
    private CompoundNBT data;
    public SPacketQuestCategorySave(CompoundNBT data) {
        this.data = data;
    }

    public SPacketQuestCategorySave(){

    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_QUEST;
    }


    @Override
    public void handle() {
        QuestCategory category = new QuestCategory();
        category.readNBT(data);
        QuestController.instance.saveCategory(category);
        Packets.send(player, new PacketGuiUpdate());
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        data = buf.readNbt();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeNbt(data);
    }
}