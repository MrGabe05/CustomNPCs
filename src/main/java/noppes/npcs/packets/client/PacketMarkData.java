package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.shared.common.PacketBasic;


public class PacketMarkData extends PacketBasic {
	private final int id;
    private final CompoundNBT data;

    public PacketMarkData(int id, CompoundNBT data) {
    	this.id = id;
    	this.data = data;
    }

    public static void encode(PacketMarkData msg, PacketBuffer buf) {
    	buf.writeInt(msg.id);
        buf.writeNbt(msg.data);
    }

    public static PacketMarkData decode(PacketBuffer buf) {
        return new PacketMarkData(buf.readInt(), buf.readNbt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Entity entity = Minecraft.getInstance().level.getEntity(id);
        if(entity == null || !(entity instanceof LivingEntity))
            return;
        MarkData mark = MarkData.get((LivingEntity) entity);
        mark.setNBT(data);
	}
}