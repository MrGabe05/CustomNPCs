package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;


public class PacketNpcRole extends PacketBasic {
	private final int id;
	private final CompoundNBT data;

    public PacketNpcRole(int id, CompoundNBT data) {
        this.id = id;
        this.data = data;
    }

    public static void encode(PacketNpcRole msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
        buf.writeNbt(msg.data);
    }

    public static PacketNpcRole decode(PacketBuffer buf) {
        return new PacketNpcRole(buf.readInt(), buf.readNbt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Entity entity = Minecraft.getInstance().level.getEntity(id);
        if(entity == null || !(entity instanceof EntityNPCInterface))
            return;
        ((EntityNPCInterface)entity).advanced.setRole(data.getInt("Role"));
        ((EntityNPCInterface)entity).role.load(data);
        NoppesUtil.setLastNpc((EntityNPCInterface) entity);
	}
}