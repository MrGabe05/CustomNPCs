package noppes.npcs.api.wrapper;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import noppes.npcs.api.IDimension;

public class DimensionWrapper implements IDimension{
	
	private final ResourceLocation id;
	
	private final DimensionType type;

	public DimensionWrapper(ResourceLocation id, DimensionType type) {
		this.id = id;
		this.type = type;
	}

	@Override
	public String getId() {
		return id.toString();
	}

}
