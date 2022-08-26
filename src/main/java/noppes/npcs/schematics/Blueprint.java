package noppes.npcs.schematics;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.World;

/**
 * @author JTK222 
 */
public class Blueprint implements ISchematic {

	private List<String> requiredMods;
	private short sizeX, sizeY, sizeZ;
	private short palleteSize;
	private BlockState[] pallete;
	private String name;
	private String[] architects;
	
	private short[][][] structure;
	private CompoundNBT[] tileEntities;
	
	public Blueprint(short sizeX, short sizeY, short sizeZ, short palleteSize, BlockState[] pallete, short[][][] structure, CompoundNBT[] tileEntities, List<String> requiredMods){
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.palleteSize = palleteSize;
		this.pallete = pallete;
		this.structure = structure;
		this.tileEntities = tileEntities;
		this.requiredMods = requiredMods;
	}
	
	public void build(World level, BlockPos pos){
		BlockState[] pallete = this.getPallete();
		short[][][] structure = this.getStructure();
		for(short y = 0; y < this.getSizeY(); y ++){
			for(short z = 0; z < this.getSizeZ(); z++){
				for(short x = 0; x < this.getSizeX(); x++){
					BlockState state = pallete[structure[y][z][x] & 0xFFFF];
					if(state.getBlock() == Blocks.STRUCTURE_VOID)
						continue;
					if(state.isCollisionShapeFullBlock(EmptyBlockReader.INSTANCE, BlockPos.ZERO))
						level.setBlock(pos.offset(x, y, z), state, 2);
				}
			}
		}
		for(short y = 0; y < this.getSizeY(); y ++){
			for(short z = 0; z < this.getSizeZ(); z++){
				for(short x = 0; x < this.getSizeX(); x++){
					BlockState state = pallete[structure[y][z][x]];
					if(state.getBlock() == Blocks.STRUCTURE_VOID)
						continue;
					if(!state.isCollisionShapeFullBlock(EmptyBlockReader.INSTANCE, BlockPos.ZERO))
						level.setBlock(pos.offset(x, y, z), state, 2);
				}
			}
		}
		if(this.getTileEntities() != null){
			for(CompoundNBT tag : this.getTileEntities()){
				TileEntity te = level.getBlockEntity(pos.offset(tag.getShort("x"), tag.getShort("y"), tag.getShort("z")));
				tag.putInt("x", pos.getX() + tag.getShort("x"));
				tag.putInt("y", pos.getY() + tag.getShort("y"));
				tag.putInt("z", pos.getZ() + tag.getShort("z"));
				te.deserializeNBT(tag);
			}
		}
	}

	public short getSizeX() {
		return sizeX;
	}

	public short getSizeY() {
		return sizeY;
	}

	public short getSizeZ() {
		return sizeZ;
	}

	public short getPalleteSize() {
		return palleteSize;
	}

	public BlockState[] getPallete() {
		return pallete;
	}

	public short[][][] getStructure() {
		return structure;
	}

	public CompoundNBT[] getTileEntities() {
		return tileEntities;
	}

	public List<String> getRequiredMods() {
		return requiredMods;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String[] getArchitects() {
		return architects;
	}


	public void setArchitects(String[] architects) {
		this.architects = architects;
	}

	@Override
	public short getWidth() {
		return getSizeX();
	}

	@Override
	public short getHeight() {
		return getSizeZ();
	}

	@Override
	public short getLength() {
		return getSizeY();
	}

	@Override
	public int getBlockEntitySize() {
		return tileEntities.length;
	}

	@Override
	public CompoundNBT getBlockEntity(int i) {
		return tileEntities[i];
	}

	@Override
	public BlockState getBlockState(int x, int y, int z) {
		return pallete[structure[y][z][x]];
	}

	@Override
	public BlockState getBlockState(int i) {
		int x = (int) (i % getWidth());
		int z = (int)((i - x)/getWidth()) % getLength();
		int y = (int)(((i - x)/getWidth()) - z) / getLength();
		return getBlockState(x, y, z);
	}

	@Override
	public CompoundNBT getNBT() {
		return BlueprintUtil.writeBlueprintToNBT(this);
	}
}