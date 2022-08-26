package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import noppes.npcs.shared.common.PacketBasic;

import java.lang.reflect.Constructor;


public class PacketNpcVisibleTrue extends PacketBasic {
    private final static Constructor<FMLPlayMessages.SpawnEntity> constructor;
    static {
        Constructor<FMLPlayMessages.SpawnEntity> con = null;
        try {
            con = FMLPlayMessages.SpawnEntity.class.getDeclaredConstructor(Entity.class);
            con.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        constructor = con;
    }
	private final FMLPlayMessages.SpawnEntity pkt;
    private final int id;

    public PacketNpcVisibleTrue(Entity entity) {
        id = entity.getId();
        FMLPlayMessages.SpawnEntity p = null;
        try {
            p = constructor.newInstance(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pkt = p;
    }
    public PacketNpcVisibleTrue(int id, FMLPlayMessages.SpawnEntity pkt) {
        this.id = id;
        this.pkt = pkt;
    }

    public static void encode(PacketNpcVisibleTrue msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
        FMLPlayMessages.SpawnEntity.encode(msg.pkt, buf);
    }

    public static PacketNpcVisibleTrue decode(PacketBuffer buf) {
        return new PacketNpcVisibleTrue(buf.readInt(), FMLPlayMessages.SpawnEntity.decode(buf));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        ClientWorld w = Minecraft.getInstance().level;
        Entity entity = w.getEntity(id);
        if(entity == null){
            FMLPlayMessages.SpawnEntity.handle(pkt, ctx);
        }

	}
}