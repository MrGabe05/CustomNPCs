package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCustomGuiButton extends PacketServerBasic {
    private final int buttonId;
    public SPacketCustomGuiButton(int buttonId) {
        this.buttonId = buttonId;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketCustomGuiButton msg, PacketBuffer buf) {
        buf.writeInt(msg.buttonId);
    }

    public static SPacketCustomGuiButton decode(PacketBuffer buf) {
        return new SPacketCustomGuiButton(buf.readInt());
    }

    @Override
    protected void handle() {
        if(player.containerMenu instanceof ContainerCustomGui) {
            EventHooks.onCustomGuiButton((PlayerWrapper)NpcAPI.Instance().getIEntity(player), ((ContainerCustomGui)player.containerMenu).customGui, buttonId);
        }
    }
}