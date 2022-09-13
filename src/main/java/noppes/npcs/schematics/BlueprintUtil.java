package noppes.npcs.schematics;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author JTK222
 */
public class BlueprintUtil {

	public static CompoundNBT writeBlueprintToNBT(Blueprint schem){
		CompoundNBT compound = new CompoundNBT();
		//Set Blueprint Version
		compound.putByte("version", (byte)1);
		//Set Blueprint Size
		compound.putShort("size_x", schem.getSizeX());
		compound.putShort("size_y", schem.getSizeY());
		compound.putShort("size_z", schem.getSizeZ());
		
		//Create Pallete
		BlockState[] palette = schem.getPallete();
		ListNBT paletteTag = new ListNBT();
		for(short i = 0; i < schem.getPalleteSize(); i++){
			paletteTag.add(NBTUtil.writeBlockState(palette[i]));
		}
		compound.put("palette", paletteTag);

		//Adding blocks
		int[] blockInt = convertBlocksToSaveData(schem.getStructure(), schem.getSizeX(), schem.getSizeY(), schem.getSizeZ());
		compound.putIntArray("blocks", blockInt);
		
		//Adding Tile Entities
		ListNBT finishedTes = new ListNBT();
		CompoundNBT[] tes = schem.getTileEntities();
		finishedTes.addAll(Arrays.asList(tes));
		compound.put("tile_entities", finishedTes);
		
		//Adding Required Mods
		List<String> requiredMods = schem.getRequiredMods();
		ListNBT modsList = new ListNBT();
		for (String requiredMod : requiredMods) {
			//modsList.set(i,);
			modsList.add(StringNBT.valueOf(requiredMod));
		}
		compound.put("required_mods", modsList);
		
		String name = schem.getName();
		String[] architects = schem.getArchitects();
		
		if(name != null){
			compound.putString("name", name);
		}
		if(architects != null){
			ListNBT architectsTag = new ListNBT();
			for(String architect : architects){
				architectsTag.add(StringNBT.valueOf(architect));
			}
			compound.put("architects", architectsTag);
		}
		
		return compound;
	}
	

	public static Blueprint readBlueprintFromNBT(CompoundNBT tag){
		byte version = tag.getByte("version");
		if(version == 1){
			short sizeX = tag.getShort("size_x"), sizeY = tag.getShort("size_y"), sizeZ = tag.getShort("size_z");
			
			
			//Reading required Mods
			List<String> requiredMods = new ArrayList<>();
			ListNBT modsList = tag.getList("required_mods", 8);
			short modListSize = (short) modsList.size();
			for(int i = 0; i < modListSize; i++){
				requiredMods.add(modsList.get(i).getAsString());
				if(!ModList.get().isLoaded(requiredMods.get(i))){
					Logger.getGlobal().log(Level.WARNING, "Couldn't load Blueprint, the following mod is missing: " + requiredMods.get(i));
					return null;
				}
			}
			
			//Reading Pallete
			ListNBT paletteTag = tag.getList("palette", 10);
			short paletteSize = (short) paletteTag.size();
			BlockState[] palette = new BlockState[paletteSize];
			for(short i = 0; i < palette.length; i++){
				palette[i] = NBTUtil.readBlockState(paletteTag.getCompound(i));
			}
			
			//Reading Blocks
			short[][][] blocks = convertSaveDataToBlocks(tag.getIntArray("blocks"), sizeX, sizeY, sizeZ);
			
			//Reading Tile Entities
			ListNBT teTag = tag.getList("tile_entities", 10);
			CompoundNBT[] tileEntities = new CompoundNBT[teTag.size()];
			for(short i = 0; i < tileEntities.length; i++){
				tileEntities[i] = teTag.getCompound(i);
			}

			Blueprint schem = new Blueprint(sizeX, sizeY, sizeZ, paletteSize, palette, blocks, tileEntities, requiredMods);
			
			if(tag.contains("name")){
				schem.setName(tag.getString("name"));
			}
			if(tag.contains("architects")){
				ListNBT architectsTag = tag.getList("architects", 8);
				String[] architects = new String[architectsTag.size()];
				for(int i = 0; i < architectsTag.size(); i++){
					architects[i] = architectsTag.getString(i);
				}
				schem.setArchitects(architects);
			}
			
			return schem;
		}
		return null;
	}
	
	private static int[] convertBlocksToSaveData(short[][][] multDimArray, short sizeX, short sizeY, short sizeZ){
		//Converting 3 Dimensional Array to One DImensional
		short[] oneDimArray = new short[sizeX * sizeY * sizeZ];
 		
		int j = 0;
		for(short y = 0; y < sizeY; y ++){
			for(short z = 0; z < sizeZ; z++){
				for(short x = 0; x < sizeX; x++){
					oneDimArray[j++] = multDimArray[y][z][x];
				}
			}
		}
		
		//Converting short Array to int Array
		int[] ints = new int[(int) Math.ceil(oneDimArray.length / 2f)];
		
		int currentInt;
		for(int i = 1; i < oneDimArray.length; i += 2){
			currentInt = oneDimArray[i-1];
			currentInt = currentInt << 16 | oneDimArray[i];
			ints[(int) Math.ceil(i/ 2f) - 1] = currentInt;
		}
		if(oneDimArray.length % 2 == 1){
			currentInt = oneDimArray[oneDimArray.length-1] << 16;
			ints[ints.length - 1] = currentInt;
		}
		return ints;
	}
	
	public static short[][][] convertSaveDataToBlocks(int[] ints, short sizeX, short sizeY, short sizeZ){
		//Convert int array to short array
		short[] oneDimArray = new short[ints.length * 2];
		
		for(int i = 0; i < ints.length; i++){
			oneDimArray[i * 2] = (short) (ints[i] >> 16);
			oneDimArray[(i * 2) + 1] = (short) (ints[i]);
		}
		
		//Convert 1 Dimensional Array to 3 Dimensional Array
		short[][][] multDimArray = new short[sizeY][sizeZ][sizeX];
 		
		int i = 0;
		for(short y = 0; y < sizeY; y ++){
			for(short z = 0; z < sizeZ; z++){
				for(short x = 0; x < sizeX; x++){
					multDimArray[y][z][x] = oneDimArray[i++];
				}
			}
		}
		return multDimArray;
	}
}