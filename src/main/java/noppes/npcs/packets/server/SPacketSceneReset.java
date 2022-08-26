package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketSceneReset extends PacketServerBasic {

    public SPacketSceneReset() {

    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.SCENES;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketSceneReset msg, PacketBuffer buf) {

    }

    public static SPacketSceneReset decode(PacketBuffer buf) {
        return new SPacketSceneReset();
    }

    @Override
    protected void handle() {
        if(CustomNpcs.SceneButtonsEnabled) {
            DataScenes.Reset(player.createCommandSourceStack(), null);
        }
    }
}