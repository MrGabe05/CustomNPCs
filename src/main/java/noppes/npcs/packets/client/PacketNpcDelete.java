package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;


public class PacketNpcDelete extends PacketBasic {
	private final int id;

    public PacketNpcDelete(int id) {
        this.id = id;
    }

    public static void encode(PacketNpcDelete msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static PacketNpcDelete decode(PacketBuffer buf) {
        return new PacketNpcDelete(buf.readInt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Entity entity = Minecraft.getInstance().level.getEntity(id);
        if(entity == null || !(entity instanceof EntityNPCInterface))
            return;
        ((EntityNPCInterface)entity).delete();
	}
}