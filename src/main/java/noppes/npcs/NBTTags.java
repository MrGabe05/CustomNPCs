package noppes.npcs;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;

import java.util.*;

public class NBTTags {

	public static void getItemStackList(ListNBT tagList, NonNullList<ItemStack> items) {
		items.clear();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            try{
            	items.set(nbttagcompound.getByte("Slot") & 0xff, ItemStack.of(nbttagcompound));
            }
            catch(ClassCastException e){
            	items.set(nbttagcompound.getInt("Slot"), ItemStack.of(nbttagcompound));
            }
        }
	}

	public static Map<Integer, IItemStack> getIItemStackMap(ListNBT tagList) {
		Map<Integer, IItemStack> list = new HashMap<Integer, IItemStack>();
        for(int i = 0; i < tagList.size(); i++){
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            ItemStack item = ItemStack.of(nbttagcompound);
            if(item.isEmpty())
            	continue;
            try{
            	list.put(nbttagcompound.getByte("Slot") & 0xff, NpcAPI.Instance().getIItemStack(item));
            }
            catch(ClassCastException e){
            	list.put(nbttagcompound.getInt("Slot"), NpcAPI.Instance().getIItemStack(item));
            }
        }
		return list;
	}
	
	public static ItemStack[] getItemStackArray(
			ListNBT tagList) {
		ItemStack[] list = new ItemStack[tagList.size()];
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT nbttagcompound = tagList.getCompound(i);
        	list[nbttagcompound.getByte("Slot") & 0xff] = ItemStack.of(nbttagcompound);
        }
		return list;
	}
	
	public static NonNullList<Ingredient> getIngredientList(ListNBT tagList) {
		NonNullList<Ingredient> list = NonNullList.create();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            list.add(nbttagcompound.getByte("Slot") & 0xff, Ingredient.of(ItemStack.of(nbttagcompound)));
        }
		return list;
	}
	
	public static ArrayList<int[]> getIntegerArraySet(ListNBT tagList) {
		ArrayList<int[]> set = new ArrayList<int[]>();
        for(int i = 0; i < tagList.size(); i++)
        {
        	CompoundNBT compound = tagList.getCompound(i);
        	set.add(compound.getIntArray("Array"));
        }
		return set;
	}

	public static HashMap<Integer, Boolean> getBooleanList(ListNBT tagList) {
		HashMap<Integer, Boolean> list = new HashMap<Integer, Boolean>();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getInt("Slot"), nbttagcompound.getBoolean("Boolean"));
        }
		return list;
	}
	
	public static HashMap<Integer, Integer> getIntegerIntegerMap(
			ListNBT tagList) {
		HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getInt("Slot"), nbttagcompound.getInt("Integer"));
        }
		return list;
	}
	
	public static HashMap<Integer, Long> getIntegerLongMap(
			ListNBT tagList) {
		HashMap<Integer, Long> list = new HashMap<Integer, Long>();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getInt("Slot"), nbttagcompound.getLong("Long"));
        }
		return list;
	}
	
	public static HashSet<Integer> getIntegerSet(ListNBT tagList) {
		HashSet<Integer> list = new HashSet<Integer>();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            list.add(nbttagcompound.getInt("Integer"));
        }
		return list;
	}
	
	public static List<Integer> getIntegerList(ListNBT tagList) {
		List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            list.add(nbttagcompound.getInt("Integer"));
        }
		return list;
	}

	public static HashMap<String, String> getStringStringMap(ListNBT tagList) {
		HashMap<String, String> list = new HashMap<String, String>();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getString("Slot"), nbttagcompound.getString("Value"));
        }
		return list;
	}

	public static HashMap<Integer, String> getIntegerStringMap(ListNBT tagList) {
		HashMap<Integer, String> list = new HashMap<Integer, String>();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getInt("Slot"), nbttagcompound.getString("Value"));
        }
		return list;
	}
	
	public static HashMap<String, Integer> getStringIntegerMap(ListNBT tagList) {
		HashMap<String, Integer> list = new HashMap<String, Integer>();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getString("Slot"), nbttagcompound.getInt("Value"));
        }
		return list;
	}
	public static HashMap<String, Vector<String>> getVectorMap(ListNBT tagList) {
		HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
        for(int i = 0; i < tagList.size(); i++)
        {
        	Vector<String> values = new Vector<String>();
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            ListNBT list = nbttagcompound.getList("Values", 10);
            for(int j = 0; j < list.size(); j++)
            {
                CompoundNBT value = list.getCompound(j);
                values.add(value.getString("Value"));
            }
            
            map.put(nbttagcompound.getString("Key"), values);
        }
		return map;
	}


	public static List<String> getStringList(ListNBT tagList) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT nbttagcompound = tagList.getCompound(i);
			String line = nbttagcompound.getString("Line");
			list.add(line);
		}
		return list;
	}

	public static List<ResourceLocation> getResourceLocationList(ListNBT tagList) {
		List<ResourceLocation> list = new ArrayList<ResourceLocation>();
		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT nbttagcompound = tagList.getCompound(i);
			ResourceLocation line = new ResourceLocation(nbttagcompound.getString("Line"));
			list.add(line);
		}
		return list;
	}
	
	public static String[] getStringArray(ListNBT tagList, int size) {
		String[] arr = new String[size];
		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT nbttagcompound = tagList.getCompound(i);
			String line = nbttagcompound.getString("Value");
			int slot = nbttagcompound.getInt("Slot");
			arr[slot] = line;
		}
		return arr;
	}
	
    public static ListNBT nbtIntegerArraySet(List<int[]> set) {
        ListNBT nbttaglist = new ListNBT();
    	if(set == null)
    		return nbttaglist;
        for(int[] arr : set)
        {
            CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putIntArray("Array", arr);
            nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
	}
	
    public static ListNBT nbtItemStackList(NonNullList<ItemStack> inventory) {
        ListNBT nbttaglist = new ListNBT();
        for(int slot = 0; slot < inventory.size(); slot++){
        	ItemStack item = inventory.get(slot);
        	if(item.isEmpty())
        		continue;
            CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putByte("Slot", (byte)slot);
            
            item.save(nbttagcompound);
            
            nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
	}
	
    public static ListNBT nbtIItemStackMap(Map<Integer, IItemStack> inventory) {
        ListNBT nbttaglist = new ListNBT();
    	if(inventory == null)
    		return nbttaglist;
        for(int slot : inventory.keySet()){
        	IItemStack item = inventory.get(slot);
        	if(item == null)
        		continue;
            CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putByte("Slot", (byte)slot);
            
            item.getMCItemStack().save(nbttagcompound);
            
            nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
	}
	
    public static ListNBT nbtItemStackArray(ItemStack[] inventory) {
        ListNBT nbttaglist = new ListNBT();
    	if(inventory == null)
    		return nbttaglist;
        for(int slot = 0 ; slot < inventory.length; slot++)
        {
        	ItemStack item = inventory[slot];
            CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putByte("Slot", (byte)slot);

        	if(item != null)
        		item.save(nbttagcompound);
            
            nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
	}
	
    public static ListNBT nbtIngredientList(NonNullList<Ingredient> inventory) {
        ListNBT nbttaglist = new ListNBT();
    	if(inventory == null)
    		return nbttaglist;
        for(int slot = 0 ; slot < inventory.size(); slot++)
        {
        	Ingredient ingredient = inventory.get(slot);
            CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putByte("Slot", (byte)slot);

        	if(ingredient != null && ingredient.getItems().length > 0)
        		ingredient.getItems()[0].save(nbttagcompound);
            
            nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
	}

	public static ListNBT nbtBooleanList(HashMap<Integer, Boolean> updatedSlots) {
        ListNBT nbttaglist = new ListNBT();
    	if(updatedSlots == null)
    		return nbttaglist;
        HashMap<Integer,Boolean> inventory2 = updatedSlots;
        for(Integer slot : inventory2.keySet())
        {
            CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putInt("Slot", slot);
            nbttagcompound.putBoolean("Boolean", inventory2.get(slot));
            
            nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
	}

	public static ListNBT nbtIntegerIntegerMap(Map<Integer, Integer> lines) {
		ListNBT nbttaglist = new ListNBT();
		if(lines == null)
			return nbttaglist;
		for (int slot : lines.keySet()) {
			CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putInt("Slot", slot);
            nbttagcompound.putInt("Integer", lines.get(slot));
			nbttaglist.add(nbttagcompound);
		}
		return nbttaglist;
	}

	public static ListNBT nbtIntegerLongMap(HashMap<Integer, Long> lines) {
		ListNBT nbttaglist = new ListNBT();
		if(lines == null)
			return nbttaglist;
		for (int slot : lines.keySet()) {
			CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putInt("Slot", slot);
            nbttagcompound.putLong("Long", lines.get(slot));
			nbttaglist.add(nbttagcompound);
		}
		return nbttaglist;
	}

	public static ListNBT nbtIntegerCollection(Collection<Integer> set) {
		ListNBT nbttaglist = new ListNBT();
		if(set == null)
			return nbttaglist;
		for (int slot : set) {
			CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putInt("Integer", slot);
			nbttaglist.add(nbttagcompound);
		}
		return nbttaglist;
	}

	public static ListNBT nbtVectorMap(HashMap<String, Vector<String>> map) {
        ListNBT list = new ListNBT();
        if(map == null)
        	return list;
        for(String key : map.keySet()){
        	CompoundNBT compound = new CompoundNBT();
        	compound.putString("Key", key);
            ListNBT values = new ListNBT();
        	for(String value : map.get(key)){
            	CompoundNBT comp = new CompoundNBT();
            	comp.putString("Value", value);
            	values.add(comp);
        	}
            compound.put("Values", values);
        	list.add(compound);
        }
		return list;
	}
	
	public static ListNBT nbtStringStringMap(HashMap<String, String> map) {
        ListNBT nbttaglist = new ListNBT();
    	if(map == null)
    		return nbttaglist;
        for(String slot : map.keySet())
        {
            CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putString("Slot", slot);
            nbttagcompound.putString("Value", map.get(slot));
            
            nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
	}
	
	public static ListNBT nbtStringIntegerMap(Map<String, Integer> map) {
        ListNBT nbttaglist = new ListNBT();
    	if(map == null)
    		return nbttaglist;
        for(String slot : map.keySet())
        {
            CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putString("Slot", slot);
            nbttagcompound.putInt("Value", map.get(slot));
            
            nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
	}

	public static INBT nbtIntegerStringMap(Map<Integer, String> map) {
        ListNBT nbttaglist = new ListNBT();
    	if(map == null)
    		return nbttaglist;
        for(int slot : map.keySet())
        {
            CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putInt("Slot", slot);
            nbttagcompound.putString("Value", map.get(slot));
            
            nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
	}
	
	public static ListNBT nbtStringArray(String[] list) {
        ListNBT nbttaglist = new ListNBT();
    	if(list == null)
    		return nbttaglist;
        for(int i = 0; i < list.length; i++){ 
        	if(list[i] == null)
        		continue;
			CompoundNBT nbttagcompound = new CompoundNBT();
			nbttagcompound.putString("Value", list[i]);
			nbttagcompound.putInt("Slot", i);
			nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
	}

	public static ListNBT nbtStringList(List<String> list) {
		ListNBT nbttaglist = new ListNBT();
		for (String s : list) {
			CompoundNBT nbttagcompound = new CompoundNBT();
			nbttagcompound.putString("Line", s);
			nbttaglist.add(nbttagcompound);
		}
		return nbttaglist;
	}

	public static ListNBT nbtResourceLocationList(List<ResourceLocation> list) {
		ListNBT nbttaglist = new ListNBT();
		for (ResourceLocation s : list) {
			CompoundNBT nbttagcompound = new CompoundNBT();
			nbttagcompound.putString("Line", s.toString());
			nbttaglist.add(nbttagcompound);
		}
		return nbttaglist;
	}

	public static ListNBT nbtDoubleList(double ... par1ArrayOfDouble){
        ListNBT nbttaglist = new ListNBT();
        double[] adouble = par1ArrayOfDouble;
        int i = par1ArrayOfDouble.length;

        for (int j = 0; j < i; ++j)
        {
            double d1 = adouble[j];
            nbttaglist.add(DoubleNBT.valueOf(d1));
        }

        return nbttaglist;
    }

	public static CompoundNBT NBTMerge(CompoundNBT data, CompoundNBT merge) {
		CompoundNBT compound = (CompoundNBT) data.copy();
		Set<String> names = merge.getAllKeys();
		for(String name : names){
			INBT base = merge.get(name);
			if(base.getId() == 10)
				base = NBTMerge(compound.getCompound(name), (CompoundNBT) base);
			compound.put(name, base);
		}
		return compound;
	}
	
	public static List<ScriptContainer> GetScript(ListNBT list, IScriptHandler handler){
		List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
		for(int i = 0; i < list.size(); i++){
			CompoundNBT compoundd = list.getCompound(i);
			ScriptContainer script = new ScriptContainer(handler);
			script.load(compoundd);
			scripts.add(script);			
		}
		return scripts;
	}
	
	public static ListNBT NBTScript(List<ScriptContainer> scripts){
		ListNBT list = new ListNBT();
		for(ScriptContainer script : scripts){
			CompoundNBT compound = new CompoundNBT();
			script.save(compound);
			list.add(compound);
		}
		return list;
	}

	public static TreeMap<Long, String> GetLongStringMap(ListNBT tagList) {
		TreeMap<Long, String> list = new TreeMap<Long, String>();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getLong("Long"), nbttagcompound.getString("String"));
        }
		return list;
	}

	public static ListNBT NBTLongStringMap(Map<Long, String> map) {
        ListNBT nbttaglist = new ListNBT();
    	if(map == null)
    		return nbttaglist;
        for(long slot : map.keySet())
        {
            CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putLong("Long", slot);
            nbttagcompound.putString("String", map.get(slot));
            
            nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
	}

}
