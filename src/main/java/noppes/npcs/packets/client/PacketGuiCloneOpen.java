package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.GuiNpcMobSpawnerAdd;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiCloneOpen extends PacketBasic {
    private final CompoundNBT data;

    public PacketGuiCloneOpen(CompoundNBT data) {
    	this.data = data;
    }

    public static void encode(PacketGuiCloneOpen msg, PacketBuffer buf) {
        buf.writeNbt(msg.data);
    }

    public static PacketGuiCloneOpen decode(PacketBuffer buf) {
        return new PacketGuiCloneOpen(buf.readNbt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        NoppesUtil.openGUI(player, new GuiNpcMobSpawnerAdd(data));
	}
}