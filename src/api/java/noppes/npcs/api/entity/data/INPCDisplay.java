package noppes.npcs.api.entity.data;

import noppes.npcs.api.entity.IPlayer;

public interface INPCDisplay {

	String getName();

	void setName(String name);

	String getTitle();

	void setTitle(String title);

	String getSkinUrl();

	void setSkinUrl(String url);

	String getSkinPlayer();

	void setSkinPlayer(String name);

	String getSkinTexture();

	void setSkinTexture(String texture);

	boolean getHasLivingAnimation();

	void setHasLivingAnimation(boolean enabled);

	/**
	 * @return 0:visible, 1:invisible, 2:semi-invisible
	 */
    int getVisible();

	/**
	 * @param type 0:visible, 1:invisible, 2:semi-invisible
	 */
    void setVisible(int type);

	/**
	 * If the availability is set, you can check if its visible to the player or not
	 */
    boolean isVisibleTo(IPlayer player);

	/**
	 * @return 0:invisible, 1:visible, 2:when-attacking
	 */
    int getBossbar();

	/**
	 * @param type 0:invisible, 1:visible, 2:when-attacking
	 */
    void setBossbar(int type);
	
	/**
	 * @return 1-30
	 */
    int getSize();

	/**
	 * @param size 1-30
	 */
    void setSize(int size);

	int getTint();

	/**
	 * @param color E.g. setTint(0xFF0000) sets the tint to red. (depending on the language you might want to look up how to convert hexadecimals to integers)
	 */
    void setTint(int color);

	/**
	 * @return 0:visible, 1:invisible, 2:when-attacking
	 */
    int getShowName();
	
	/**
	 * @param type 0:visible, 1:invisible, 2:when-attacking
	 */
    void setShowName(int type);

	void setCapeTexture(String texture);

	String getCapeTexture();

	void setOverlayTexture(String texture);

	String getOverlayTexture();

	/**
	 * @param part 0:Head, 1:Body, 2:ArmLeft, 3:ArmRight, 4:LegLeft, 5:LegRight
	 */
    void setModelScale(int part, float x, float y, float z);

	/**
	 * @param part 0:Head, 1:Body, 2:ArmLeft, 3:ArmRight, 4:LegLeft, 5:LegRight
	 * @return Returns a float array
	 */
    float[] getModelScale(int part);

	/**
	 * @return 0:Pink, 1:Blue, 2:Red, 3:Green, 4:Yellow, 5:Purple, 6:White
	 */
    int getBossColor();

	/**
	 * @param color 0:Pink, 1:Blue, 2:Red, 3:Green, 4:Yellow, 5:Purple, 6:White
	 */
    void setBossColor(int color);
	
	/**
	 * @param model Entity id from <a href="https://minecraft.gamepedia.com/Java_Edition_data_values/Entity_IDs">here</a>
	 */
    void setModel(String model);
	
	String getModel();

	/**
	 * @param state 0:Normal, 1:None, 2:Solid
	 */
    void setHitboxState(byte state);

	/**
	 * @return 0:Normal, 1:None, 2:Solid
	 */
    byte getHitboxState();

}
