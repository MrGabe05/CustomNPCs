package noppes.npcs.packets.client;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.shared.common.PacketBasic;

public class PacketItemUpdate extends PacketBasic {
	private final int id;
	private final CompoundNBT data;

    public PacketItemUpdate(int id, CompoundNBT data) {
    	this.id = id;
    	this.data = data;
    }

    public static void encode(PacketItemUpdate msg, PacketBuffer buf) {
    	buf.writeInt(msg.id);
    	buf.writeNbt(msg.data);
    }

    public static PacketItemUpdate decode(PacketBuffer buf) {
        return new PacketItemUpdate(buf.readInt(), buf.readNbt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        ItemStack stack = player.inventory.getItem(id);
        if(!stack.isEmpty()) {
            ((ItemStackWrapper) NpcAPI.Instance().getIItemStack(stack)).setMCNbt(data);
        }
	}
}