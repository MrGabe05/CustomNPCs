package noppes.npcs.api;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

/**
 * @author Karel
 *
 */
public interface INbt {
	
	void remove(String key);

	boolean has(String key);

	boolean getBoolean(String key);

	void setBoolean(String key, boolean value);

	short getShort(String key);

	void setShort(String key, short value);

	int getInteger(String key);

	void setInteger(String key, int value);

	byte getByte(String key);

	void setByte(String key, byte value);

	long getLong(String key);

	void setLong(String key, long value);

	double getDouble(String key);

	void setDouble(String key, double value);

	float getFloat(String key);

	void setFloat(String key, float value);

	String getString(String key);

	void putString(String key, String value);

	byte[] getByteArray(String key);

	void setByteArray(String key, byte[] value);

	int[] getIntegerArray(String key);

	void setIntegerArray(String key, int[] value);

	/**
	 * @param key
	 * @param type The Type of the list 3:Integer, 5:Float, 6:Double, 8:String,
	 *             10:INbt, 11:Integer[]
	 * @return
	 */
	Object[] getList(String key, int type);

	/**
	 * @param key
	 * @return 3:Integer, 5:Float, 6:Double, 8:String, 10:INbt, 11:Integer[]
	 */
	int getListType(String key);

	void setList(String key, Object[] value);

	INbt getCompound(String key);

	void setCompound(String key, INbt value);

	String[] getKeys();

	/**
	 * @param key
	 * @return 1:Byte, 2:Short 3:Integer, 4:Long, 5:Float, 6:Double, 7:Byte[],
	 *         8:String, 9:List, 10:INbt, 11:Integer[]
	 */
	int getType(String key);

	CompoundNBT getMCNBT();

	String toJsonString();

	/**
	 * Compares if two nbt tags are the same/contain the same data
	 */
	boolean isEqual(INbt nbt);

	/**
	 * Clears all tags
	 */
	void clear();

	/**
	 * Merges two nbt tabs, note that nbt tags will be overwritten if they have the
	 * same keys
	 */
	void merge(INbt nbt);

	void mcSetTag(String key, INBT base);
	
	INBT mcGetTag(String key);
}
