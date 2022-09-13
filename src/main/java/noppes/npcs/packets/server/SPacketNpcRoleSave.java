package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNpcRoleSave extends PacketServerBasic {
    private final CompoundNBT data;

    public SPacketNpcRoleSave(CompoundNBT data) {
        this.data = data;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcRoleSave msg, PacketBuffer buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketNpcRoleSave decode(PacketBuffer buf) {
        return new SPacketNpcRoleSave(buf.readNbt());
    }

    @Override
    protected void handle() {
        npc.role.load(data);
        npc.updateClient = true;
    }
}