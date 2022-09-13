package noppes.npcs.api.block;

/**
 * Used for certain technical mods which use FluidContainer blocks *
 */
public interface IBlockFluidContainer extends IBlock{

	float getFluidPercentage();

	float getFuildDensity();

	float getFuildTemperature();

	String getFluidName();

}
