package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomItems;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketNpcRoleGet extends PacketServerBasic {

    public SPacketNpcRoleGet() {

    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    public static void encode(SPacketNpcRoleGet msg, PacketBuffer buf) {

    }

    public static SPacketNpcRoleGet decode(PacketBuffer buf) {
        return new SPacketNpcRoleGet();
    }

    @Override
    protected void handle() {
        if(npc.role.getType() == RoleType.NONE)
            return;
        CompoundNBT compound = new CompoundNBT();
        compound.putBoolean("RoleData", true);
        Packets.send(player, new PacketGuiData(npc.role.save(compound)));
    }
}