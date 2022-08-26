package noppes.npcs.packets.server;

import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.containers.ContainerNPCFollower;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.roles.RoleFollower;

public class SPacketFollowerExtend extends PacketServerBasic {

    public SPacketFollowerExtend() {
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    public static void encode(SPacketFollowerExtend msg, PacketBuffer buf) {
    }

    public static SPacketFollowerExtend decode(PacketBuffer buf) {
        return new SPacketFollowerExtend();
    }

    @Override
    protected void handle() {
        if(npc.role.getType() != RoleType.FOLLOWER)
            return;
        Container con = player.containerMenu;
        if(con == null || !(con instanceof ContainerNPCFollower))
            return;

        ContainerNPCFollower container = (ContainerNPCFollower) con;
        RoleFollower role = (RoleFollower) npc.role;
        SPacketFollowerHire.followerBuy(role, container.currencyMatrix, player, npc);
        Packets.send(player, new PacketGuiData(npc.role.save(new CompoundNBT())));
    }
}