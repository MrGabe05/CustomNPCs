package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomContainer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketMailSetup extends PacketServerBasic {
    private CompoundNBT data;
    public SPacketMailSetup(CompoundNBT data) {
        this.data = data;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketMailSetup msg, PacketBuffer buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketMailSetup decode(PacketBuffer buf) {
        return new SPacketMailSetup(buf.readNbt());
    }

    @Override
    protected void handle() {
        PlayerMail mail = new PlayerMail();
        mail.readNBT(data);
        ContainerMail.staticmail = mail;
        NoppesUtilServer.openContainerGui(player, EnumGuiType.PlayerMailman, (buf) -> {
            buf.writeBoolean(true);
            buf.writeBoolean(false);
        });
    }
}