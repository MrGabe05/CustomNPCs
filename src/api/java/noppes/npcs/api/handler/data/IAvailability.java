package noppes.npcs.api.handler.data;

import noppes.npcs.api.entity.IPlayer;

public interface IAvailability {
	
	boolean isAvailable(IPlayer player);
	
	/**
	 * @return 0:Always, 1:Night, 2:Day
	 */
	int getDaytime();
	
	/**
	 * @param type 0:Always, 1:Night, 2:Day
	 */
	void setDaytime(int type);

	int getMinPlayerLevel();

	void setMinPlayerLevel(int level);

	/**
	 * @param i (0-3)
	 * @return Returns dialog id, -1 if no dialog was set
	 */
	int getDialog(int i);

	/**
	 * @param i (0-3)
	 * @param id Dialog id
	 * @param type 0:Always, 1:After, 2:Before
	 */
	void setDialog(int i, int id, int type);

	/**
	 * @param i (0-3)
	 */
	void removeDialog(int i);

	/**
	 * @param i (0-3)
	 * @return Returns quest id, -1 if no quest was set
	 */
	int getQuest(int i);

	/**
	 * @param i (0-3)
	 * @param id Quest id
	 * @param type 0:Always, 1:After, 2:Before, 3:Active, 4:NotActive, 5:Completed
	 */
	void setQuest(int i, int id, int type);

	/**
	 * @param i (0-1)
	 */
	void removeQuest(int i);

	/**
	 * @param i (0-1)
	 * @param id Faction id
	 * @param type 0:Always, 1:Is, 2:IsNot
	 * @param stance 0:Friendly, 1:Neutral, 2:Hostile
	 */
	void setFaction(int i, int id, int type, int stance);

	/**
	 * @param i (0-1)
	 */
	void removeFaction(int i);

	/**
	 * @param i (0-1)
	 * @param objective Scoreboard Objective
	 * @param type 0:Smalle, 1:Equals, 2:Bigger
	 * @param value Scoreboard score value
	 */
	void setScoreboard(int i, String objective, int type, int value);
}
