package noppes.npcs.schematics;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Schematic implements ISchematic{
	private static final HashMap<String, BlockState> staticBlockIds = new HashMap<>();
	static {
		InputStream stream = MinecraftServer.class.getResourceAsStream("/data/customnpcs/legacy_blockids.json");

		Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
		JsonObject result  = new Gson().fromJson(reader, JsonObject.class).getAsJsonObject("blocks");
		for(Map.Entry<String, JsonElement> entry : result.entrySet()){
			String val = entry.getValue().getAsString();
			String[] properties = null;
			if(val.indexOf('[') > 0){
				properties = val.substring(val.indexOf('[') + 1, val.length() - 1).split(",");
				val = val.substring(0, val.indexOf('['));
			}
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(val));
			if (block != null) {
				BlockState state = block.defaultBlockState();
				if(properties != null){
					for(Property<?> prop : state.getProperties()){
						for(String r : properties){
							if(r.startsWith(prop.getName() + "=")){
								state = setValue(state, prop, r.split("=")[1]);
							}
						}
					}
				}
				staticBlockIds.put(entry.getKey(), state);
			}
		}
	}

	private static <T extends Comparable<T>> BlockState setValue(BlockState state, Property<T> prop, String val){
		Optional<T> optional = prop.getValue(val);
		return optional.map(t -> state.setValue(prop, t)).orElse(state);
	}
	
	public String name;
	public short width, height, length;

	private ListNBT entityList;
	public ListNBT tileList;
	
	public short[] blockArray;
	public byte[] blockDataArray;
	public HashMap<String, BlockState> blockIds = staticBlockIds;
        
    public Schematic(String name){
    	this.name = name;
    }
    
    public void load(final CompoundNBT compound){
		width = compound.getShort("Width");
		height = compound.getShort("Height");
		length = compound.getShort("Length");
		
		byte[] addId = compound.contains("AddBlocks")?compound.getByteArray("AddBlocks"):new byte[0];
		setBlockBytes(compound.getByteArray("Blocks"), addId);
		
		blockDataArray = compound.getByteArray("Data");
		entityList = compound.getList("Entities", (byte)10);
		tileList = compound.getList("TileEntities", (byte)10);
        if (compound.contains("BlockIDs", Constants.NBT.TAG_COMPOUND)) {
			CompoundNBT comp = compound.getCompound("BlockIDs");
			blockIds = new HashMap<>();
			for (String idStr : comp.getAllKeys()) {
				String key = comp.getString(idStr);
				try {
					int id = Integer.parseInt(idStr);
					Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key));
					if(block != null) {
						blockIds.put(id + ":0", block.defaultBlockState());
					}
				}
				catch (NumberFormatException ignored) {
				}
			}
		}
    }
    
    public CompoundNBT getNBT(){
    	CompoundNBT compound = new CompoundNBT();
    	compound.putShort("Width", width);
    	compound.putShort("Height", height);
    	compound.putShort("Length", length);
    	
    	byte[][] arr = getBlockBytes();
    	compound.putByteArray("Blocks", arr[0]);
    	if(arr.length > 1)
        	compound.putByteArray("AddBlocks", arr[1]);
    	compound.putByteArray("Data", blockDataArray);

    	//compound.put("Entities", entityList);
    	compound.put("TileEntities", tileList);

		CompoundNBT comp = new CompoundNBT();
		for(Map.Entry<String, BlockState> entry : blockIds.entrySet()){
			comp.putString(Block.getId(entry.getValue()) + "", entry.getValue().getBlock().getRegistryName().toString());
		}
		compound.put("BlockIDs", comp);

		return compound;
    }
    
    public void setBlockBytes(byte[] blockId, byte[] addId){
		blockArray = new short[blockId.length];
		
        for (int index = 0; index < blockId.length; index++) {
        	short id = (short)(blockId[index] & 0xFF);
            if ((index >> 1) < addId.length) {
                if ((index & 1) == 0) 
                	id += (short) ((addId[index >> 1] & 0x0F) << 8);
                else 
                	id += (short) ((addId[index >> 1] & 0xF0) << 4);  
            }
        	blockArray[index] = id;
        }
    }
    
    public byte[][] getBlockBytes(){
        byte[] blocks = new byte[blockArray.length];
        byte[] addBlocks = null;
        
        for(int i = 0; i < blocks.length; i++){
        	short id = blockArray[i];
        	if(id > 255){
                if (addBlocks == null) {
                    addBlocks = new byte[(blocks.length >> 1) + 1];
                }
                if((i & 1) == 0)
                	addBlocks[i >> 1] = (byte) (addBlocks[i >> 1] & 0xF0 | (id >> 8) & 0xF);
                else
                	addBlocks[i >> 1] = (byte) (addBlocks[i >> 1] & 0xF | ((id >> 8) & 0xF) << 4);
        	}
        	blocks[i] = (byte)id;
        }
        if(addBlocks == null)
        	return new byte[][]{blocks};
        return new byte[][]{blocks, addBlocks};
    }
	
	public int xyzToIndex(int x, int y, int z){
		return (y * length + z) * width + x;
	}
    
    public BlockState getBlockState(int x, int y, int z){
    	return getBlockState(xyzToIndex(x, y, z));
    }

	@Override
	public BlockState getBlockState(int i) {
		BlockState b = blockIds.get(blockArray[i] + ":" + blockDataArray[i]);
    	if(b == null)
    		return Blocks.AIR.defaultBlockState();
    	return b;
	}

	@Override
	public short getWidth() {
		return width;
	}

	@Override
	public short getHeight() {
		return height;
	}

	@Override
	public short getLength() {
		return length;
	}

	@Override
	public int getBlockEntitySize() {
		if(tileList == null)
			return 0;
		return tileList.size();
	}

	@Override
	public CompoundNBT getBlockEntity(int i) {
		return tileList.getCompound(i);
	}

	@Override
	public String getName() {
		return name;
	}

}