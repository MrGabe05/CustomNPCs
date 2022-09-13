package noppes.npcs.api;

public interface ITimers {


	/**
	 * Used for timer events, will throw an error if a timer with the id is already started
	 * @param id The timers id
	 * @param ticks After how many ticks the timer triggers
	 * @param repeat Whether it resets the timer when done or deletes it
	 */
	void start(int id, int ticks, boolean repeat);

	/**
	 * Used for timer events, wont throw an error if an timer with this id already exists and will overwrite it with this new one
	 * @param id The timers id
	 * @param ticks After how many ticks the timer triggers
	 * @param repeat Whether it resets the timer when done or deletes it
	 */
	void forceStart(int id, int ticks, boolean repeat);

	/**
	 * @return Returns true if a timer with this id is already active
	 */
	boolean has(int id);
	
	/**
	 * @return Returns false if there was no timer with the giver id
	 */
	boolean stop(int id);
	
	/**
	 * Resets the timer back to 0
	 */
	void reset(int id);
	
	void clear();
}
