package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.entity.data.INPCJob;

public interface IJobPuppet extends INPCJob{
	
	boolean getIsAnimated();
	
	void setIsAnimated(boolean bo);
	
	/**
	 * @return (0-7)
	 */
    int getAnimationSpeed();
	
	/**
	 * @param speed (0-7)
	 */
    void setAnimationSpeed(int speed);

	/**
	 * Part 6-11 are for animation
	 * @param part 0:head, 1:left arm, 2:right arm, 3:body, 4:left leg, 5:right leg, 6:head2, 7:left arm2, 8:right arm2, 9:body2, 10:left leg2, 11:right leg2
	 * @return returns the part
	 */
    IJobPuppetPart getPart(int part);

	interface IJobPuppetPart{
		
		int getRotationX();
		
		int getRotationY();
		
		int getRotationZ();
		
		void setRotation(int x, int y, int z);
	}
}
