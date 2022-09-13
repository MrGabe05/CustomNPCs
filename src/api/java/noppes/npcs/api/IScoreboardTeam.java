package noppes.npcs.api;

public interface IScoreboardTeam {

	String getName();

	String getDisplayName();

	/**
	 * @param name Name used as display (1-32 chars)
	 */
	void setDisplayName(String name);

	void addPlayer(String player);

	boolean hasPlayer(String player);

	void removePlayer(String player);

	String[] getPlayers();

	void clearPlayers();

	boolean getFriendlyFire();

	void setFriendlyFire(boolean bo);

	/**
	 * @param color Valid color values are "black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", and "white". Or "reset" if you want default 
	 */
	void setColor(String color);

	/**
	 * @return Returns color string. Returns null if no color was set
	 */
	String getColor();

	void setSeeInvisibleTeamPlayers(boolean bo);

	boolean getSeeInvisibleTeamPlayers();

}
