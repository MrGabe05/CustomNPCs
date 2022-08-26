package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomContainer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketQuestOpen extends PacketServerBasic {
    private EnumGuiType gui;
    private CompoundNBT data;
    public SPacketQuestOpen(EnumGuiType gui, CompoundNBT data) {
        this.gui = gui;
        this.data = data;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_DIALOG;
    }

    public static void encode(SPacketQuestOpen msg, PacketBuffer buf) {
        buf.writeEnum(msg.gui);
        buf.writeNbt(msg.data);
    }

    public static SPacketQuestOpen decode(PacketBuffer buf) {
        return new SPacketQuestOpen(buf.readEnum(EnumGuiType.class), buf.readNbt());
    }

    @Override
    protected void handle() {
        Quest quest = new Quest(null);
        quest.readNBT(data);
        NoppesUtilServer.setEditingQuest(player,quest);
        NoppesUtilServer.openContainerGui(player, gui, (buf) -> {
            buf.writeBlockPos(BlockPos.ZERO);
        });
    }
}