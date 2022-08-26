package noppes.npcs.controllers.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import noppes.npcs.api.entity.data.role.IRoleTransporter.ITransportLocation;

public class TransportLocation implements ITransportLocation{
	public int id = -1;
	public String name = "default name";
	public BlockPos pos;
	
	public int type = 0;
	public RegistryKey<World> dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, DimensionType.OVERWORLD_EFFECTS);
	
	public TransportCategory category;
	
	public void readNBT(CompoundNBT compound) {
		if(compound == null)
			return;
		id = compound.getInt("Id");
		pos = new BlockPos(compound.getDouble("PosX"), compound.getDouble("PosY"), compound.getDouble("PosZ"));
		type = compound.getInt("Type");
		dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, (new ResourceLocation(compound.getString("DimensionType"))));
		name = compound.getString("Name");
	}

	public CompoundNBT writeNBT() {
		CompoundNBT compound = new CompoundNBT();
		compound.putInt("Id", id);
		compound.putDouble("PosX", pos.getX());
		compound.putDouble("PosY", pos.getY());
		compound.putDouble("PosZ", pos.getZ());
		compound.putInt("Type", type);
		compound.putString("DimensionType", dimension.location().toString());
		compound.putString("Name", name);
		return compound;
	}
	
	@Override
	public int getId(){
		return id;
	}

	@Override
	public String getDimension(){
		return dimension.location().toString();
	}

	@Override
	public int getX(){
		return pos.getX();
	}

	@Override
	public int getY(){
		return pos.getY();
	}

	@Override
	public int getZ(){
		return pos.getZ();
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	public int getType(){
		return type;
	}

	public boolean isDefault() {
		return type == 1;
	}
}
