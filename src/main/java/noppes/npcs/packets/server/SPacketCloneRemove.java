package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCloneRemove extends PacketServerBasic {
    private final String name;
    private final int tab;

    public SPacketCloneRemove(String name, int tab) {
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

    public static void encode(SPacketCloneRemove msg, PacketBuffer buf) {
        buf.writeUtf(msg.name);
        buf.writeInt(msg.tab);
    }

    public static SPacketCloneRemove decode(PacketBuffer buf) {
        return new SPacketCloneRemove(buf.readUtf(32767), buf.readInt());
    }

    @Override
    protected void handle() {
        ServerCloneController.Instance.removeClone(name, tab);
        SPacketCloneList.sendList(player, tab);
    }

}