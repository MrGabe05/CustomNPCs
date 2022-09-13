package noppes.npcs.api.entity.data;

public interface INPCAi {

	int getAnimation();

	void setAnimation(int type);

	/**
	 * @return Returns the npcs current animation. E.g. when npc is set to LYING it wont be lying while walking so it will be NORMAL
	 * @see noppes.npcs.api.constants.AnimationType
	 */
    int getCurrentAnimation();

	/**
	 * @param bo Whether or not the npc will try to return to his home position
	 */
    void setReturnsHome(boolean bo);

	boolean getReturnsHome();

	/**
	 * @return Retaliation type. 0:Normal, 1:Panic, 2:Retreat, 3:Nothing
	 */
    int getRetaliateType();

	/**
	 * @param type Retaliation type. 0:Normal, 1:Panic, 2:Retreat, 3:Nothing
	 */
    void setRetaliateType(int type);

	/**
	 * @return 0:Standing, 1:Wandering, 2:MovingPath
	 */
    int getMovingType();

	/**
	 * @param type 0:Standing, 1:Wandering, 2:MovingPath
	 */
    void setMovingType(int type);

	/**
	 * @return type 0:Ground, 1:Flying, 2:Swimming
	 */
    int getNavigationType();

	/**
	 * @param type 0:Ground, 1:Flying, 2:Swimming
	 */
    void setNavigationType(int type);

	/**
	 * @return 0:RotateBody, 1:NoRotation, 2:Stalking, 3:HeadRotation
	 */
    int getStandingType();

	/**
	 * @param type 0:RotateBody, 1:NoRotation, 2:Stalking, 3:HeadRotation
	 */
    void setStandingType(int type);

	/**
	 * @return Returns whether or not he can attack invisible entities
	 */
    boolean getAttackInvisible();

	void setAttackInvisible(boolean attack);

	int getWanderingRange();

	/**
	 * @param range (1-50)
	 */
    void setWanderingRange(int range);

	boolean getInteractWithNPCs();

	void setInteractWithNPCs(boolean interact);

	boolean getStopOnInteract();

	void setStopOnInteract(boolean stopOnInteract);

	int getWalkingSpeed();

	/**
	 * @param speed 0-10
	 */
    void setWalkingSpeed(int speed);
	
	/**
	 * @return 0:Looping, 1:Backtracking
	 */
    int getMovingPathType();

	boolean getMovingPathPauses();

	/**
	 * @param type 0:Looping, 1:Backtracking
	 */
    void setMovingPathType(int type, boolean pauses);

	int getDoorInteract();

	void setDoorInteract(int type);

	boolean getCanSwim();

	void setCanSwim(boolean canSwim);

	/**
	 * @return 0:Darkness, 1:Sunlight, 2:Disabled
	 */
    int getSheltersFrom();

	/**
	 * @param type 0:Darkness, 1:Sunlight, 2:Disabled
	 */
    void setSheltersFrom(int type);

	/**
	 * @return Whether the NPC requires Direct Line of Sight to Attack
	 */
    boolean getAttackLOS();

	/**
	 * @param enabled Whether the NPC requires Direct Line of Sight to Attack
	 */
    void setAttackLOS(boolean enabled);

	boolean getAvoidsWater();

	void setAvoidsWater(boolean enabled);

	boolean getLeapAtTarget();

	void setLeapAtTarget(boolean leap);
	
}
