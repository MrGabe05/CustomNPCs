package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomItems;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketCloneList extends PacketServerBasic {
    private int tab;

    public SPacketCloneList(int tab) {
        this.tab = tab;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.wand || item.getItem() == CustomItems.cloner || item.getItem() == CustomItems.mount;
    }

    public static void encode(SPacketCloneList msg, PacketBuffer buf) {
        buf.writeInt(msg.tab);
    }

    public static SPacketCloneList decode(PacketBuffer buf) {
        return new SPacketCloneList(buf.readInt());
    }

    @Override
    protected void handle() {
        sendList(player, tab);
    }

    public static void sendList(ServerPlayerEntity player, int tab){
        ListNBT list = new ListNBT();

        for(String name : ServerCloneController.Instance.getClones(tab))
            list.add(StringNBT.valueOf(name));

        CompoundNBT compound = new CompoundNBT();
        compound.put("List", list);

        Packets.send(player, new PacketGuiData(compound));
    }

}