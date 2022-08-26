package noppes.npcs.client;

import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import noppes.npcs.CustomItems;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.*;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent.Phase;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.gui.player.GuiQuestLog;
import noppes.npcs.client.renderer.RenderNPCInterface;
import org.lwjgl.glfw.GLFW;

public class ClientTickHandler{

	private World prevWorld;
	private boolean otherContainer = false;
	
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onClientTick(TickEvent.ClientTickEvent event){
		if(event.phase == Phase.END)
			return;
		Minecraft mc = Minecraft.getInstance();
		if(mc.player != null && mc.player.containerMenu instanceof PlayerContainer){
			if(otherContainer){
		    	Packets.sendServer(new SPacketQuestCompletionCheckAll());
				otherContainer = false;
			}
		}
		else
			otherContainer = true;
		CustomNpcs.ticks++;
		RenderNPCInterface.LastTextureTick++;
		if(prevWorld != mc.level){
			prevWorld = mc.level;
			MusicController.Instance.stopMusic();
		}
	}

	@SubscribeEvent
	public void onKey(InputEvent.KeyInputEvent event){
		Minecraft mc = Minecraft.getInstance();
		if(mc == null || mc.level == null || mc.getConnection() == null){
			return;
		}
		if(CustomNpcs.SceneButtonsEnabled){
			if(ClientProxy.Scene1.isDown()){
				Packets.sendServer(new SPacketSceneStart(1));
			}
			if(ClientProxy.Scene2.isDown()){
				Packets.sendServer(new SPacketSceneStart(2));
			}
			if(ClientProxy.Scene3.isDown()){
				Packets.sendServer(new SPacketSceneStart(3));
			}
			if(ClientProxy.SceneReset.isDown()){
				Packets.sendServer(new SPacketSceneReset());
			}
		}
		if(ClientProxy.QuestLog.isDown()){
			if(mc.screen == null)
				NoppesUtil.openGUI(mc.player, new GuiQuestLog(mc.player));
			else if(mc.screen instanceof GuiQuestLog)
				mc.mouseHandler.grabMouse();
		}

		if(event.getAction() == GLFW.GLFW_PRESS || event.getAction() == GLFW.GLFW_RELEASE) {
			boolean isCtrlPressed = InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341) || InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
			boolean isShiftPressed = InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
			boolean isAltPressed = InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342) || InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
			boolean isMetaPressed = InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343) || InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
			String openGui = mc.screen == null ? "" : mc.screen.getClass().getName();
			Packets.sendServer(new SPacketPlayerKeyPressed(event.getKey(), isCtrlPressed, isShiftPressed, isAltPressed, isMetaPressed, event.getAction() == GLFW.GLFW_RELEASE, openGui));
		}
	}

	@SubscribeEvent
	public void invoke(PlayerInteractEvent.LeftClickEmpty event) {
		if(event.getHand() != Hand.MAIN_HAND)
			return;
		Packets.sendServer(new SPacketPlayerLeftClicked());
	}

	private final int[] ignoreKeys = new int[]{341, 340, 342, 343, 345, 344, 346, 347};
	private boolean isIgnoredKey(int key) {
		for(int i : ignoreKeys) {
			if(i == key)
				return true;
		}
		return false;
	}
}
