package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;

public class VersionChecker extends Thread{
	
	public void run(){
		String name = '\u00A7'+ "2CustomNpcs" + '\u00A7' + "f";
		String link = '\u00A7'+"9"+'\u00A7' + "nClick here"; 
		String text =  name +" installed. For more info " + link;
		
        PlayerEntity player;
		try{
			player = Minecraft.getInstance().player;
		}
		catch(NoSuchMethodError e){
			return;
		}
        while((player = Minecraft.getInstance().player) == null){
        	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        
        TranslationTextComponent message = new TranslationTextComponent(text);
		message.setStyle(message.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.kodevelopment.nl/minecraft/customnpcs/")));
        player.sendMessage(message, Util.NIL_UUID);
	}
}
