package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.api.wrapper.gui.CustomGuiComponentWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCustomGuiTextUpdate extends PacketServerBasic {
    private final int id;
    private final CompoundNBT data;
    public SPacketCustomGuiTextUpdate(int id, CompoundNBT data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketCustomGuiTextUpdate msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
        buf.writeNbt(msg.data);
    }

    public static SPacketCustomGuiTextUpdate decode(PacketBuffer buf) {
        return new SPacketCustomGuiTextUpdate(buf.readInt(), buf.readNbt());
    }

    @Override
    protected void handle() {
        if(player.containerMenu instanceof ContainerCustomGui) {
            CustomGuiWrapper gui = ((ContainerCustomGui)player.containerMenu).customGui;
            CustomGuiComponentWrapper comp = (CustomGuiComponentWrapper) gui.getComponent(id);
            if(comp != null){
                comp.fromNBT(data);
            }
        }
    }
}