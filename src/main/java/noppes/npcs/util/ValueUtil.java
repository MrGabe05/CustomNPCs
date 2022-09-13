package noppes.npcs.util;

import com.google.gson.*;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ValueUtil {

	public static final UUID EMPTY_UUID = new UUID(0L, 0L);

	public static float correctFloat(float given, float min, float max){
		if(given < min)
			return min;
		return Math.min(given, max);
	}

	public static int CorrectInt(int given, int min, int max) {
		if(given < min)
			return min;
		return Math.min(given, max);
	}

	public static String nbtToJson(CompoundNBT nbt){
		return new Gson().toJson(getJsonValue((nbt)));
	}

	private static JsonElement getJsonValue(INBT value){
		if(value.getType() == CompoundNBT.TYPE){
			CompoundNBT nbt = (CompoundNBT) value;
			JsonObject root = new JsonObject();
			for(String key : nbt.getAllKeys()){
				INBT n = nbt.get(key);
				JsonElement ele = getJsonValue(n);
				if(ele == null)
					continue;
				JsonObject ob = new JsonObject();
				ob.addProperty("type", n.getType().getName());
				ob.addProperty("type_id", n.getId());
				ob.addProperty("pretty_type", n.getType().getPrettyName());
				ob.add("value", ele);
				root.add(key, ob);
			}
			return root;
		}
		else if(value == StringNBT.TYPE){
			return new JsonPrimitive(value.getAsString());
		}
		else if(value instanceof NumberNBT){
			return new JsonPrimitive(((NumberNBT)value).getAsNumber());
		}
		else if(value instanceof CollectionNBT){
			JsonArray jsonValue = new JsonArray();
			for(INBT n : ((CollectionNBT<INBT>)value)){
				jsonValue.add(getJsonValue(n));
			}
			return jsonValue;
		}
		return null;
	}

	public static CompoundNBT jsonToNbt(String json){
		JsonObject ob = new Gson().fromJson(json, JsonObject.class);
		return toNbt(ob);
	}

	private static CompoundNBT toNbt(JsonObject json){
		CompoundNBT nbt = new CompoundNBT();
		for(Map.Entry<String, JsonElement> entry : json.entrySet()){
			String key = entry.getKey();
			JsonObject ele = (JsonObject)entry.getValue();
			INBTType<? extends INBT> type = stringToType(ele.get("type").getAsString());
			if(type == StringNBT.TYPE){
				nbt.putString(key, ele.get("value").getAsString());
			}
			if(type == IntNBT.TYPE) {
				nbt.putInt(key, ele.get("value").getAsInt());
			}
			if(type == ByteNBT.TYPE) {
				nbt.putByte(key, ele.get("value").getAsByte());
			}
			if(type == LongNBT.TYPE) {
				nbt.putLong(key, ele.get("value").getAsLong());
			}
			if(type == FloatNBT.TYPE) {
				nbt.putFloat(key, ele.get("value").getAsFloat());
			}
			if(type == DoubleNBT.TYPE) {
				nbt.putDouble(key, ele.get("value").getAsDouble());
			}
			if(type == ShortNBT.TYPE) {
				nbt.putShort(key, ele.get("value").getAsShort());
			}
			if(type == CompoundNBT.TYPE) {
				nbt.put(key, toNbt((JsonObject) ele.get("value")));
			}
			if(type == IntArrayNBT.TYPE) {
				JsonArray array = (JsonArray) ele.get("value");
				nbt.put(key, new IntArrayNBT(StreamSupport.stream(array.spliterator(), false).map(JsonElement::getAsInt).collect(Collectors.toList())));
			}
			if(type == ByteArrayNBT.TYPE) {
				JsonArray array = (JsonArray) ele.get("value");
				nbt.put(key, new ByteArrayNBT(StreamSupport.stream(array.spliterator(), false).map(JsonElement::getAsByte).collect(Collectors.toList())));
			}
			if(type == LongArrayNBT.TYPE) {
				JsonArray array = (JsonArray) ele.get("value");
				nbt.put(key, new LongArrayNBT(StreamSupport.stream(array.spliterator(), false).map(JsonElement::getAsLong).collect(Collectors.toList())));
			}
		}
		return nbt;
	}

	private static INBTType<? extends INBT> stringToType(String type){
		if(type.equals(IntNBT.TYPE.getName())){
			return IntNBT.TYPE;
		}
		if(type.equals(ByteNBT.TYPE.getName())){
			return ByteNBT.TYPE;
		}
		if(type.equals(FloatNBT.TYPE.getName())){
			return FloatNBT.TYPE;
		}
		if(type.equals(LongNBT.TYPE.getName())){
			return LongNBT.TYPE;
		}
		if(type.equals(DoubleNBT.TYPE.getName())){
			return DoubleNBT.TYPE;
		}
		if(type.equals(ShortNBT.TYPE.getName())){
			return ShortNBT.TYPE;
		}
		if(type.equals(CompoundNBT.TYPE.getName())){
			return CompoundNBT.TYPE;
		}
		if(type.equals(IntArrayNBT.TYPE.getName())){
			return IntArrayNBT.TYPE;
		}
		if(type.equals(ByteArrayNBT.TYPE.getName())){
			return ByteArrayNBT.TYPE;
		}
		if(type.equals(LongArrayNBT.TYPE.getName())){
			return LongArrayNBT.TYPE;
		}
		return StringNBT.TYPE;
	}

	public static boolean isValidPath(String s) {
		for(int i = 0; i < s.length(); ++i) {
			if (!ResourceLocation.validPathChar(s.charAt(i))) {
				return false;
			}
		}

		return true;
	}
}
