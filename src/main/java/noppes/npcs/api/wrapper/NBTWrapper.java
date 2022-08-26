package noppes.npcs.api.wrapper;

import net.minecraft.nbt.*;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.util.NBTJsonUtil;

public class NBTWrapper implements INbt {
	
	private CompoundNBT compound;
	
	public NBTWrapper(CompoundNBT compound){
		this.compound = compound;
	}

	@Override
	public void remove(String key) {
		compound.remove(key);
	}

	@Override
	public boolean has(String key) {
		return compound.contains(key);
	}

	@Override
	public boolean getBoolean(String key) {
		return compound.getBoolean(key);
	}

	@Override
	public void setBoolean(String key, boolean value) {
		compound.putBoolean(key, value);
	}

	@Override
	public short getShort(String key) {
		return compound.getShort(key);
	}

	@Override
	public void setShort(String key, short value) {
		compound.putShort(key, value);
	}

	@Override
	public int getInteger(String key) {
		return compound.getInt(key);
	}

	@Override
	public void setInteger(String key, int value) {
		compound.putInt(key, value);
	}

	@Override
	public byte getByte(String key) {
		return compound.getByte(key);
	}

	@Override
	public void setByte(String key, byte value) {
		compound.putByte(key, value);
	}

	@Override
	public long getLong(String key) {
		return compound.getLong(key);
	}

	@Override
	public void setLong(String key, long value) {
		compound.putLong(key, value);
	}

	@Override
	public double getDouble(String key) {
		return compound.getDouble(key);
	}

	@Override
	public void setDouble(String key, double value) {
		compound.putDouble(key, value);
	}

	@Override
	public float getFloat(String key) {
		return compound.getFloat(key);
	}

	@Override
	public void setFloat(String key, float value) {
		compound.putFloat(key, value);
	}

	@Override
	public String getString(String key) {
		return compound.getString(key);
	}

	@Override
	public void putString(String key, String value) {
		compound.putString(key, value);
	}

	@Override
	public byte[] getByteArray(String key) {
		return compound.getByteArray(key);
	}

	@Override
	public void setByteArray(String key, byte[] value) {
		compound.putByteArray(key, value);
	}

	@Override
	public int[] getIntegerArray(String key) {
		return compound.getIntArray(key);
	}

	@Override
	public void setIntegerArray(String key, int[] value) {
		compound.putIntArray(key, value);
	}

	@Override
	public Object[] getList(String key, int type) {
		ListNBT list = compound.getList(key, type);
		Object[] nbts = new Object[list.size()];
		
		for(int i = 0; i < list.size(); i++){
			if(list.getElementType() == 10)
				nbts[i] = NpcAPI.Instance().getINbt(list.getCompound(i));
			else if(list.getElementType() == 8)
				nbts[i] = list.getString(i);
			else if(list.getElementType() == 6)
				nbts[i] = list.getDouble(i);
			else if(list.getElementType() == 5)
				nbts[i] = list.getFloat(i);
			else if(list.getElementType() == 3)
				nbts[i] = list.getInt(i);
			else if(list.getElementType() == 11)
				nbts[i] = list.getIntArray(i);
		}
		return nbts;
	}

	@Override
	public int getListType(String key){
		INBT b = compound.get(key);
		if(b == null)
			return 0;
		if(b.getId() != 9)
			throw new CustomNPCsException("NBT tag " + key + " isn't a list");
		return ((ListNBT)b).getElementType();
	}

	@Override
	public void setList(String key, Object[] value) {
		ListNBT list = new ListNBT();
		for(Object nbt : value){
			if(nbt instanceof INbt)
				list.add(((INbt)nbt).getMCNBT());
			else if(nbt instanceof String)
				list.add(StringNBT.valueOf((String)nbt));
			else if(nbt instanceof Double)
				list.add(DoubleNBT.valueOf((Double)nbt));
			else if(nbt instanceof Float)
				list.add(FloatNBT.valueOf((Float)nbt));
			else if(nbt instanceof Integer)
				list.add(IntNBT.valueOf((Integer)nbt));
			else if(nbt instanceof int[])
				list.add(new IntArrayNBT((int[]) nbt));
		}
		compound.put(key, list);
	}

	@Override
	public INbt getCompound(String key) {
		return NpcAPI.Instance().getINbt(compound.getCompound(key));
	}

	@Override
	public void setCompound(String key, INbt value) {
		if(value == null)
			throw new CustomNPCsException("Value cant be null");
		compound.put(key, value.getMCNBT());
	}

	@Override
	public String[] getKeys() {
		return compound.getAllKeys().toArray(new String[compound.getAllKeys().size()]);
	}

	@Override
	public int getType(String key) {
		return compound.get(key).getId();
	}

	@Override
	public CompoundNBT getMCNBT() {
		return compound;
	}

	@Override
	public String toJsonString() {
		return NBTJsonUtil.Convert(compound);
	}

	@Override
	public boolean isEqual(INbt nbt) {
		if(nbt == null)
			return false;
		return compound.equals(nbt.getMCNBT());
	}

	@Override
	public void clear() {
		for(String name : compound.getAllKeys()) {
			compound.remove(name);
		}
	}

	@Override
	public void merge(INbt nbt) {
		compound.merge(nbt.getMCNBT());
	}

	@Override
	public void mcSetTag(String key, INBT base) {
		compound.put(key, base);
	}

	@Override
	public INBT mcGetTag(String key) {
		return compound.get(key);
	}
}
