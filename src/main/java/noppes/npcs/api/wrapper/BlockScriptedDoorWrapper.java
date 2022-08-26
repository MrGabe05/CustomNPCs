package noppes.npcs.api.wrapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.block.IBlockScriptedDoor;
import noppes.npcs.blocks.tiles.TileScriptedDoor;

public class BlockScriptedDoorWrapper extends BlockWrapper implements IBlockScriptedDoor{
	private TileScriptedDoor tile;

	public BlockScriptedDoorWrapper(World level, Block block, BlockPos pos) {
		super(level, block, pos);
		tile = (TileScriptedDoor) super.tile;
	}

	@Override
	public boolean getOpen(){
		BlockState state = level.getMCWorld().getBlockState(pos);
		return state.getValue(DoorBlock.OPEN).equals(true);
	}

	@Override
	public void setOpen(boolean open){
		if(getOpen() == open || isRemoved())
			return;
		
		BlockState state = level.getMCWorld().getBlockState(pos);
		
		((DoorBlock)block).setOpen(level.getMCWorld(), state, pos, open);
	}
	
	@Override
	public void setBlockModel(String name){
		Block b = null;
		if(name != null){
			b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
		}
		tile.setItemModel(b);
	}

	@Override
	public String getBlockModel(){
		return ForgeRegistries.BLOCKS.getKey(tile.blockModel) + "";
	}

	@Override
	public ITimers getTimers() {
		return tile.timers;
	}

	@Override
	public float getHardness() {
		return tile.blockHardness;
	}

	@Override
	public void setHardness(float hardness) {
		tile.blockHardness = hardness;
	}

	@Override
	public float getResistance() {
		return tile.blockResistance;
	}

	@Override
	public void setResistance(float resistance) {
		tile.blockResistance = resistance;		
	}

	@Override
	protected void setTile(TileEntity tile){
		this.tile = (TileScriptedDoor) tile;
		super.setTile(tile);
	}
}
