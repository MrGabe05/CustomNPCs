package noppes.npcs.api.entity.data;

public interface ILine {
	
	String getText();
	
	void setText(String text);
	
	String getSound();
	
	void setSound(String sound);
	
	/**
	 * @return If false the text will not show in the chat only in the text bubble
	 */
    boolean getShowText();
	
	void setShowText(boolean show);
}
