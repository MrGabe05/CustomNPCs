package noppes.npcs.api.entity.data;

public interface IData {

	void put(String key, Object value);
	
	Object get(String key);

	void remove(String key);
	
	boolean has(String key);
	
	String[] getKeys();

	/**
	 * Removes all data
	 */
	void clear();
	
}
