package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;

import java.util.Set;

public class SPacketNpcJobSave extends PacketServerBasic {
    private final CompoundNBT data;

    public SPacketNpcJobSave(CompoundNBT data) {
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

    public static void encode(SPacketNpcJobSave msg, PacketBuffer buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketNpcJobSave decode(PacketBuffer buf) {
        return new SPacketNpcJobSave(buf.readNbt());
    }

    @Override
    protected void handle() {
        CompoundNBT original = npc.job.save(new CompoundNBT());
        Set<String> names = data.getAllKeys();
        for(String name : names)
            original.put(name, data.get(name));
        npc.job.load(original);
        npc.updateClient = true;
    }
}