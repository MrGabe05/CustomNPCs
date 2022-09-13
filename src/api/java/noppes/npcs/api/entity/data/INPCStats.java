package noppes.npcs.api.entity.data;

public interface INPCStats {

	int getMaxHealth();

	void setMaxHealth(int maxHealth);
	
	/**
	 * @param type 0:Melee, 1:Ranged, 2:Explosion, 3:Knockback
	 * @return Returns value between 0 and 2. 0 being no resistance so increased damage and 2 being fully resistant. Normal is 1
	 */
    float getResistance(int type);
	
	void setResistance(int type, float value);

	/**
	 * @return Returns the combat health regen per second
	 */
    int getCombatRegen();

	/**
	 * @param regen The combat health regen per second
	 */
    void setCombatRegen(int regen);

	/**
	 * @return Returns the health regen per second when not in combat
	 */
    int getHealthRegen();

	/**
	 * @param regen The health regen per second when not in combat
	 */
    void setHealthRegen(int regen);

	INPCMelee getMelee();

	INPCRanged getRanged();

	/**
	 * @param type 0:Potion, 1:Falldamage, 2:Sunburning, 3:Fire, 4:Drowning, 5:Cobweb
	 */
    boolean getImmune(int type);

	/**
	 * @param type 0:Potion, 1:Falldamage, 2:Sunburning, 3:Fire, 4:Drowning, 5:Cobweb
	 */
    void setImmune(int type, boolean bo);

	/**
	 * (0=Normal, 1=Undead, 2=Arthropod) Only used for damage calculations with enchants
	 */
    void setCreatureType(int type);

	/**
	 * (0=Normal, 1=Undead, 2=Arthropod) Only used for damage calculations with enchants
	 */
    int getCreatureType();

	/**
	 * @return 0:Yes, 1:Day, 2:Night, 3:No, 4:Naturally
	 */
    int getRespawnType();

	/**
	 * @param type 0:Yes, 1:Day, 2:Night, 3:No, 4:Naturally
	 */
    void setRespawnType(int type);

	int getRespawnTime();

	void setRespawnTime(int seconds);

	boolean getHideDeadBody();

	void setHideDeadBody(boolean hide);

	int getAggroRange();

	void setAggroRange(int range);

}
