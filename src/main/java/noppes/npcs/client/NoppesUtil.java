package noppes.npcs.client;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.Sound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class NoppesUtil {

	public static void requestOpenGUI(EnumGuiType gui) {
		requestOpenGUI(gui, BlockPos.ZERO);
	}

	public static void requestOpenGUI(EnumGuiType gui, BlockPos pos) {
		Packets.sendServer(new SPacketGuiOpen(gui, pos));
	}


	private static EntityNPCInterface lastNpc;
	public static EntityNPCInterface getLastNpc() {
		return lastNpc;
	}
	public static void setLastNpc(EntityNPCInterface npc) {
		lastNpc = npc;
	}

	public static void openGUI(PlayerEntity player, Object guiscreen) {
		CustomNpcs.proxy.openGui(player, guiscreen);
	}
	
	public static void openFolder(File dir){
        Util.getPlatform().openFile(dir);
	}

	public static void clickSound() {		
        Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
	}

}
