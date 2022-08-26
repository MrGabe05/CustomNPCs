package noppes.npcs;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ModelPartData {
	private static Map<String, ResourceLocation> resources = new HashMap<String, ResourceLocation>();
	public int color = 0xFFFFFF;
	public int colorPattern = 0xFFFFFF;
	public byte type = 0;
	public byte pattern = 0;
	public boolean playerTexture = false;
	public String name;
	
	private ResourceLocation location;
	
	public ModelPartData(String name) {
		this.name = name;
	}

	public CompoundNBT save(){
		CompoundNBT compound = new CompoundNBT();
		compound.putByte("Type", type);
		compound.putInt("Color", color);
		compound.putBoolean("PlayerTexture", playerTexture);
		compound.putByte("Pattern", pattern);
		return compound;
	}
	
	public void load(CompoundNBT compound){
		if(!compound.contains("Type")){
			type = -1;
			return;
		}
		type = compound.getByte("Type");
		color = compound.getInt("Color");
		playerTexture = compound.getBoolean("PlayerTexture");
		pattern = compound.getByte("Pattern");
		location = null;
	}
	
	public ResourceLocation getResource(){
		if(location != null)
			return location;
		String texture = name + "/" + type;
		
		if((location = resources.get(texture)) != null)
			return location;
		
		location = new ResourceLocation("moreplayermodels:textures/" + texture + ".png");
		resources.put(texture, location);
		return location;
	}
	
	public void setType(int type){
		this.type = (byte) type;
		location = null;
	}
	
	public String toString(){
		return "Color: " + color + " Type: " + type;
	}

	public String getColor() {
		String str = Integer.toHexString(color);

    	while(str.length() < 6)
    		str = "0" + str;
    	
    	return str;
	}
}
