package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketSceneStart extends PacketServerBasic {

    private int scene;

    public SPacketSceneStart(int scene) {
        this.scene = scene;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.SCENES;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketSceneStart msg, PacketBuffer buf) {
        buf.writeInt(msg.scene);
    }

    public static SPacketSceneStart decode(PacketBuffer buf) {
        return new SPacketSceneStart(buf.readInt());
    }

    @Override
    protected void handle() {
        if(CustomNpcs.SceneButtonsEnabled) {
            DataScenes.Toggle(player.createCommandSourceStack(), scene + "btn");
        }
    }
}