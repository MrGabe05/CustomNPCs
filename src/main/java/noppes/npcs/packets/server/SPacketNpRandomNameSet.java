package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketNpRandomNameSet extends PacketServerBasic {
    private int id;
    private int gender;

    public SPacketNpRandomNameSet(int id, int gender) {
        this.id = id;
        this.gender = gender;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_DISPLAY;
    }

    public static void encode(SPacketNpRandomNameSet msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
        buf.writeInt(msg.gender);
    }

    public static SPacketNpRandomNameSet decode(PacketBuffer buf) {
        return new SPacketNpRandomNameSet(buf.readInt(), buf.readInt());
    }

    @Override
    protected void handle() {
        npc.display.setMarkovGeneratorId(id);
        npc.display.setMarkovGender(gender);
        npc.display.setName(npc.display.getRandomName());

        CompoundNBT data = new CompoundNBT();
        npc.display.save(data);
        Packets.send(player, new PacketGuiData(data));
    }
}