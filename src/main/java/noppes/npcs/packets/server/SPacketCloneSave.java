package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCloneSave extends PacketServerBasic {
    private final String name;
    private final int tab;

    public SPacketCloneSave(String name, int tab) {
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

    public static void encode(SPacketCloneSave msg, PacketBuffer buf) {
        buf.writeUtf(msg.name);
        buf.writeInt(msg.tab);
    }

    public static SPacketCloneSave decode(PacketBuffer buf) {
        return new SPacketCloneSave(buf.readUtf(32767), buf.readInt());
    }

    @Override
    protected void handle() {
        PlayerData data = PlayerData.get(player);
        if(data.cloned == null)
            return;
        ServerCloneController.Instance.addClone(data.cloned, name, tab);
    }

}