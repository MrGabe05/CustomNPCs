package noppes.npcs.api.handler.data;

import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;

public interface IFaction {

	int getId();
	
	String getName();
	
	int getDefaultPoints();
	
	void setDefaultPoints(int points);
	
	int getColor();
	
	/**
	 * @return Returns -1:Unfriendly, 0:Neutral, 1:Friendly
	 */
	int playerStatus(IPlayer player);
	
	boolean hostileToNpc(ICustomNpc npc);
	
	boolean hostileToFaction(int factionId);
	
	int[] getHostileList();
	
	void addHostile(int id);
	
	void removeHostile(int id);
	
	boolean hasHostile(int id);
	
	boolean getIsHidden();
	
	void setIsHidden(boolean bo);
	
	boolean getAttackedByMobs();
	
	void setAttackedByMobs(boolean bo);
	
	void save();
}
