package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;


public class PacketNpcVisibleFalse extends PacketBasic {
	private final int id;

    public PacketNpcVisibleFalse(int id) {
    	this.id = id;
    }

    public static void encode(PacketNpcVisibleFalse msg, PacketBuffer buf) {
    	buf.writeInt(msg.id);
    }

    public static PacketNpcVisibleFalse decode(PacketBuffer buf) {
        return new PacketNpcVisibleFalse(buf.readInt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        ClientWorld w = Minecraft.getInstance().level;
        Entity entity = w.getEntity(id);
        if(entity == null || !(entity instanceof EntityNPCInterface))
            return;
        w.removeEntity(id);
	}
}