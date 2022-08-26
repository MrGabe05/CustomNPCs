package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;


public class PacketNpcUpdate extends PacketBasic {
	private final int id;
	private final CompoundNBT data;

    public PacketNpcUpdate(int id, CompoundNBT data) {
        this.id = id;
        this.data = data;
    }

    public static void encode(PacketNpcUpdate msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
        buf.writeNbt(msg.data);
    }

    public static PacketNpcUpdate decode(PacketBuffer buf) {
        return new PacketNpcUpdate(buf.readInt(), buf.readNbt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Entity entity = Minecraft.getInstance().level.getEntity(id);
        if(entity == null || !(entity instanceof EntityNPCInterface))
            return;
        ((EntityNPCInterface)entity).readSpawnData(data);
	}
}