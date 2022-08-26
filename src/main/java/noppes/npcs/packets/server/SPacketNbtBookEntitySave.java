package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNbtBookEntitySave extends PacketServerBasic {
    private int id;
    private CompoundNBT data;

    public SPacketNbtBookEntitySave(int id, CompoundNBT data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.TOOL_NBTBOOK;
    }

    public static void encode(SPacketNbtBookEntitySave msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
        buf.writeNbt(msg.data);
    }

    public static SPacketNbtBookEntitySave decode(PacketBuffer buf) {
        return new SPacketNbtBookEntitySave(buf.readInt(), buf.readNbt());
    }

    @Override
    protected void handle() {
        Entity entity = player.level.getEntity(id);
        if(entity != null) {
            entity.load(data);
        }
    }


}