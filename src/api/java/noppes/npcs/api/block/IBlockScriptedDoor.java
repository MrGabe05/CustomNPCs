package noppes.npcs.api.block;

import noppes.npcs.api.ITimers;

public interface IBlockScriptedDoor extends IBlock{
	
	ITimers getTimers();

	boolean getOpen();

	void setOpen(boolean open);

	/**
	 * @param name The items name for the blocks model to be set
	 */
    void setBlockModel(String name);

	String getBlockModel();
	
	/**
	 * @return Harvesting hardness (-1 makes it unharvestable)
	 */
    float getHardness();
	
	void setHardness(float hardness);
	
	/**
	 * @return Explosion resistance (-1 makes it unexplodable)
	 */
    float getResistance();
	
	void setResistance(float resistance);

}
