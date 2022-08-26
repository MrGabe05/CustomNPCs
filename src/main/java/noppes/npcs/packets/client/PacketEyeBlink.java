package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.ModelData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketEyeBlink extends PacketBasic {
	private final int id;

    public PacketEyeBlink(int id) {
    	this.id = id;
    }

    public static void encode(PacketEyeBlink msg, PacketBuffer buf) {
    	buf.writeInt(msg.id);
    }

    public static PacketEyeBlink decode(PacketBuffer buf) {
        return new PacketEyeBlink(buf.readInt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Entity entity = Minecraft.getInstance().level.getEntity(id);
        if(entity == null || !(entity instanceof EntityNPCInterface))
            return;
        ModelData data = ((EntityCustomNpc)entity).modelData;
        data.eyes.blinkStart = System.currentTimeMillis();
	}
}