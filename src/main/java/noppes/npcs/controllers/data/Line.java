package noppes.npcs.controllers.data;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import noppes.npcs.api.entity.data.ILine;


public class Line implements ILine {

	public Line(){
		
	}
	public Line(String text){
		this.text = text;
	}

	protected String text = "";
	protected String sound = "";
	private boolean showText = true;
	
	public Line copy() {
		Line line = new Line(text);
		line.sound = sound;
		line.showText = showText;
		return line;
	}
	
	public static Line formatTarget(Line line, LivingEntity entity) {
		if(entity == null)
			return line;
		Line line2 = line.copy();
		if(entity instanceof PlayerEntity)
			line2.text = line2.text.replace("@target", entity.getDisplayName().getString());
		else
			line2.text = line2.text.replace("@target", entity.getName().getString());
		return line;
	}
	@Override
	public String getText() {
		return text;
	}
	
	@Override
	public void setText(String text) {
		if(text == null)
			text = "";
		this.text = text;
	}
	
	@Override
	public String getSound() {
		return sound;
	}
	
	@Override
	public void setSound(String sound) {
		if(sound == null)
			sound = "";
		this.sound = sound;
	}
	
	@Override
	public boolean getShowText() {
		return showText;
	}
	
	@Override
	public void setShowText(boolean show) {
		showText = show;
	}
}
