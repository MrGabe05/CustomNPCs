package noppes.npcs.api;

public interface IScoreboardObjective {

	String getName();

	String getDisplayName();

	/**
	 * @param name Name used for display (1-32 chars)
	 */
	void setDisplayName(String name);

	String getCriteria();

	/**
	 * @return Return whether or not the objective value can be changed. E.g. player health can't be changed
	 */
	boolean isReadyOnly();
	
	IScoreboardScore[] getScores();
	
	IScoreboardScore getScore(String player);
	
	boolean hasScore(String player);
	
	IScoreboardScore createScore(String player);
	
	void removeScore(String player);

}
