package noppes.npcs.api.entity.data;

import noppes.npcs.api.IContainer;
import noppes.npcs.api.handler.data.IQuest;

public interface IPlayerMail {
	
	String getSender();
	
	void setSender(String sender);
	
	String getSubject();
	
	void setSubject(String subject);
	
	String[] getText();
	
	void setText(String[] text);
	
	IQuest getQuest();
	
	void setQuest(int id);
	
	IContainer getContainer();
	
}
