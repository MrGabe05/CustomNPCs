package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.roles.RoleFollower;

public class SPacketFollowerState extends PacketServerBasic {

    public SPacketFollowerState() {
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    public static void encode(SPacketFollowerState msg, PacketBuffer buf) {
    }

    public static SPacketFollowerState decode(PacketBuffer buf) {
        return new SPacketFollowerState();
    }

    @Override
    protected void handle() {
        if(npc.role.getType() != RoleType.FOLLOWER)
            return;

        RoleFollower role = (RoleFollower) npc.role;
        if(role.owner == null || !role.owner.getName().equals(player.getName()))
            return;

        role.isFollowing = !role.isFollowing;
        Packets.send(player, new PacketGuiData(npc.role.save(new CompoundNBT())));
    }
}