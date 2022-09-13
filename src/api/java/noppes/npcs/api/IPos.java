package noppes.npcs.api;

import net.minecraft.util.math.BlockPos;

/**
 * All the methods in IPos create a new IPos object
 */
public interface IPos {

	int getX();

	int getY();

	int getZ();

	IPos up();

	IPos up(int n);

	IPos down();

	IPos down(int n);

	IPos north();

	IPos north(int n);

	IPos east();

	IPos east(int n);

	IPos south();

	IPos south(int n);

	IPos west();

	IPos west(int n);

	IPos add(int x, int y, int z);

	IPos add(IPos pos);

	IPos subtract(int x, int y, int z);

	IPos subtract(IPos pos);
	
	double[] normalize();

	BlockPos getMCBlockPos();

	/**
	 * @param direction {@link noppes.npcs.api.constants.SideType} 
	 */
	IPos offset(int direction);

	/**
	 * @param direction {@link noppes.npcs.api.constants.SideType} 
	 * @param n how many positions
	 */
	IPos offset(int direction, int n);
	
	double distanceTo(IPos pos);

}
