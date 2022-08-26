package noppes.npcs.packets.client;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.shared.common.PacketBasic;


public class PacketDialogDummy extends PacketBasic {
	private final String name;
    private final CompoundNBT data;

    public PacketDialogDummy(String name, CompoundNBT data) {
    	this.name = name;
    	this.data = data;
    }

    public static void encode(PacketDialogDummy msg, PacketBuffer buf) {
        buf.writeUtf(msg.name);
        buf.writeNbt(msg.data);
    }

    public static PacketDialogDummy decode(PacketBuffer buf) {
        return new PacketDialogDummy(buf.readUtf(32767), buf.readNbt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        EntityDialogNpc npc = new EntityDialogNpc(player.level);
        npc.display.setName(I18n.get(name));
        EntityUtil.Copy(player, npc);
        Dialog dialog = new Dialog(null);
        dialog.readNBT(data);
        PacketDialog.openDialog(dialog, npc, player);
	}
}