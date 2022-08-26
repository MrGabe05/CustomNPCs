package noppes.npcs.controllers.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import noppes.npcs.NBTTags;

public class SpawnData extends WeightedRandom.Item{
	public List<ResourceLocation> biomes = new ArrayList<ResourceLocation>();
	public int id = -1;
	public String name = "";
	public Map<Integer, CloneSpawnData> data = new HashMap<>();
	public boolean liquid = false;
	public int type = 0; //0:any, 1:dark only

	private static Field f = null;
	static {
		f = WeightedRandom.Item.class.getDeclaredFields()[0];
		f.setAccessible(true);
	}

	public SpawnData() {
		super(10);
	}

	public void readNBT(CompoundNBT compound) {
		id = compound.getInt("SpawnId");
		name = compound.getString("SpawnName");
		setWeight(compound.getInt("SpawnWeight"));


		biomes = NBTTags.getResourceLocationList(compound.getList("SpawnBiomes", 10));
		data = CloneSpawnData.load(compound.getList("SpawnData", 10));

		type = compound.getInt("SpawnType");
	}

	public CompoundNBT writeNBT(CompoundNBT compound) {
		compound.putInt("SpawnId", id);
		compound.putString("SpawnName", name);
		compound.putInt("SpawnWeight", weight);
		
		compound.put("SpawnBiomes", NBTTags.nbtResourceLocationList(biomes));

		compound.put("SpawnData", CloneSpawnData.save(data));

		compound.putInt("SpawnType", type);
		return compound;
	}
	public void setWeight(int weight){
		if(weight == 0)
			weight = 1;
		try {
			f.set(this, weight);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void setClone(int slot, int tab, String name) {
		data.put(slot, new CloneSpawnData(tab, name));
	}

	public CompoundNBT getCompound(int slot) {
		CloneSpawnData sd = data.get(slot);
		if(sd == null){
			return null;
		}
		return sd.getCompound();
	}
}
