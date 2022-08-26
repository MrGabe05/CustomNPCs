package noppes.npcs.controllers.data;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockData{
	public BlockPos pos;
	public BlockState state;
	public CompoundNBT tile;
	
	private ItemStack stack;
	
	public BlockData(BlockPos pos, BlockState state, CompoundNBT tile){
		this.pos = pos;
		this.state = state;
		this.tile = tile;
	}

	public CompoundNBT getNBT() {
		CompoundNBT compound = new CompoundNBT();
		compound.putInt("BuildX", pos.getX());
		compound.putInt("BuildY", pos.getY());
		compound.putInt("BuildZ", pos.getZ());
		compound.putString("Block", ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString());
		if(tile != null)
			compound.put("Tile", tile);
		return compound;
	}
	
	public static BlockData getData(CompoundNBT compound){
		BlockPos pos = new BlockPos(compound.getInt("BuildX"), compound.getInt("BuildY"), compound.getInt("BuildZ"));
		Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compound.getString("Block")));
		if(b == null)
			return null;
		CompoundNBT tile = null;
		if(compound.contains("Tile"))
			tile = compound.getCompound("Tile");
		return new BlockData(pos, b.defaultBlockState(), tile);
		
	}
	
	public ItemStack getStack(){
		if(stack == null)
			stack = new ItemStack(state.getBlock(), 1);
		return stack;
	}
}
