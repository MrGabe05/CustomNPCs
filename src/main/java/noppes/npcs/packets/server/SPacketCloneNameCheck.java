package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketCloneNameCheck extends PacketServerBasic {
    private String name;
    private int tab;

    public SPacketCloneNameCheck(String name, int tab) {
        this.name = name;
        this.tab = tab;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.cloner;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_CLONE;
    }

    public static void encode(SPacketCloneNameCheck msg, PacketBuffer buf) {
        buf.writeUtf(msg.name);
        buf.writeInt(msg.tab);
    }

    public static SPacketCloneNameCheck decode(PacketBuffer buf) {
        return new SPacketCloneNameCheck(buf.readUtf(32767), buf.readInt());
    }

    @Override
    protected void handle() {
        boolean bo = ServerCloneController.Instance.getCloneData(null, name, tab) != null;
        CompoundNBT compound = new CompoundNBT();
        compound.putBoolean("NameExists", bo);
        Packets.send(player, new PacketGuiData(compound));
    }

}