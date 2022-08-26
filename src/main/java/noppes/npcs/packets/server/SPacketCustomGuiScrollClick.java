package noppes.npcs.packets.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.controllers.CustomGuiController;
import noppes.npcs.packets.PacketServerBasic;

import java.io.IOException;

public class SPacketCustomGuiScrollClick extends PacketServerBasic {
    private final int slotId;
    private final int scrollId;
    private final boolean doubleClicked;
    private final CompoundNBT selectedData;

    public SPacketCustomGuiScrollClick(int scrollId, int slotId, boolean doubleClicked, CompoundNBT selectedData) {
        this.scrollId = scrollId;
        this.slotId = slotId;
        this.doubleClicked = doubleClicked;
        this.selectedData = selectedData;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketCustomGuiScrollClick msg, PacketBuffer buf) {
        buf.writeInt(msg.scrollId);
        buf.writeInt(msg.slotId);
        buf.writeBoolean(msg.doubleClicked);
        buf.writeNbt(msg.selectedData);
    }

    public static SPacketCustomGuiScrollClick decode(PacketBuffer buf) {
        return new SPacketCustomGuiScrollClick(buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readNbt());
    }

    @Override
    protected void handle() {
        if(player.containerMenu instanceof ContainerCustomGui) {
            EventHooks.onCustomGuiScrollClick((PlayerWrapper)NpcAPI.Instance().getIEntity(player), ((ContainerCustomGui)player.containerMenu).customGui, scrollId, slotId, readScrollSelection(), doubleClicked);
        }
    }

    private String[] readScrollSelection() {
        ListNBT list = selectedData.getList("selection", Constants.NBT.TAG_STRING);
        String[] selection = new String[list.size()];
        for(int i = 0; i < list.size(); i++) {
            selection[i] = list.get(i).getAsString();
        }
        return selection;
    }
}