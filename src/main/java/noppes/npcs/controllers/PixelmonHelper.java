package noppes.npcs.controllers;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.fml.ModList;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class PixelmonHelper {
	public static boolean Enabled = ModList.get().isLoaded("pixelmon");
	public static EventBus EVENT_BUS;

	public static Field storageManager;

	private static Object partyStorage;
	private static Method getPartyStorage;

	private static Object pcStorage;
	private static Method getPcStorage;

	private static Method getPokemonData;

	//Client
	private static Method getPixelmonModel = null;
	private static Class modelSetupClass;
	private static Method modelSetupMethod;
	private static Class pixelmonClass;

	public static void load(){
		if(!Enabled)
			return;

		try{
			if(isReforged()) {
				Class c = Class.forName("com.pixelmonmod.pixelmon.Pixelmon");
				EVENT_BUS = (EventBus) c.getDeclaredField("EVENT_BUS").get(null);

				c = Class.forName("com.pixelmonmod.pixelmon.api.storage.StorageProxy");
				storageManager = c.getDeclaredField("storageManager");
				storageManager.setAccessible(true);

				c = Class.forName("com.pixelmonmod.pixelmon.api.storage.StorageManager");
				getPartyStorage = c.getMethod("getParty", UUID.class);
				getPcStorage = c.getMethod("getPCForPlayer", UUID.class);

				pixelmonClass = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.AbstractBaseEntity");
				getPokemonData = pixelmonClass.getMethod("getPokemon");
			}
			else {
				Class c = Class.forName("com.pixelmongenerations.core.storage.PixelmonStorage");
				partyStorage = c.getDeclaredField("pokeBallManager").get(null);
				pcStorage = c.getDeclaredField("computerManager").get(null);

				c = Class.forName("com.pixelmongenerations.core.storage.PokeballManager");
				getPartyStorage = c.getMethod("getPlayerStorage", PlayerEntity.class);

				c = Class.forName("com.pixelmongenerations.core.storage.ComputerManager");
				getPcStorage = c.getMethod("getPlayerStorage", PlayerEntity.class);

				pixelmonClass = Class.forName("com.pixelmongenerations.common.entity.pixelmon.Entity1Base");
			}
		}
		catch(Exception e){
			LogWriter.except(e);
			Enabled = false;
		}
	}

	public static boolean isReforged() {
		if(!Enabled) {
			throw new CustomNPCsException("No pixelmon installed");
		}
		try{
			Class.forName("com.pixelmonmod.pixelmon.Pixelmon");
			return true;
		}
		catch(Exception e){
		}
		return false;
	}

	public static void loadClient(){
		if(!Enabled)
			return;

		try{
			if(isReforged()) {
				Class c = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.AbstractClientEntity");
				getPixelmonModel = c.getMethod("getModel");

				modelSetupClass = Class.forName("com.pixelmonmod.pixelmon.client.models.PixelmonModelSmd");
				modelSetupMethod = modelSetupClass.getMethod("setupForRender", c);
			}
			else {
				Class c = Class.forName("com.pixelmongenerations.common.entity.pixelmon.Entity3HasStats");
				getPixelmonModel = c.getMethod("getModel");

				modelSetupClass = Class.forName("com.pixelmongenerations.client.models.PixelmonModelSmd");
				modelSetupMethod = modelSetupClass.getMethod("setupForRender", c);
			}
		}
		catch(Exception e){
			LogWriter.except(e);
			Enabled = false;
		}
	}

	public static List<String> getPixelmonList(){
		List<String> list = new ArrayList<String>();
		if(!Enabled)
			return list;
		try {
			if(isReforged()) {
				Class c = Class.forName("com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies");
				Field getAll = c.getDeclaredField("ENGLISH_NAMES");
				getAll.setAccessible(true);
				Object2IntOpenHashMap<String> names = (Object2IntOpenHashMap<String>) getAll.get(null);
				list = new ArrayList<String>(names.keySet());
			}
			else {
				Class c = Class.forName("com.pixelmongenerations.core.enums.EnumSpecies");
				Object[] array = c.getEnumConstants();
				for(Object ob : array)
					list.add(ob.toString());
			}

		} catch (Exception e) {
			LogWriter.error("getPixelmonList", e);
		}
		return list;
	}

	public static boolean isPixelmon(Entity entity) {
		if(!Enabled)
			return false;
		String s = entity.getEncodeId();
		if(s == null)
			return false;
		return s.equals("pixelmon:pixelmon");
	}

	public static String getName(LivingEntity entity) {
		if(!Enabled || !isPixelmon(entity))
			return "";
		try {
			if(isReforged()) {
				Object species = pixelmonClass.getMethod("getSpecies").invoke(entity);
				return NoppesStringUtils.stripSpecialCharacters(((String) species.getClass().getMethod("getName").invoke(species)).toLowerCase());
			}
			Method m = entity.getClass().getMethod("getName");
			return m.invoke(entity).toString();
		} catch (Exception e) {
			LogManager.getLogger().error("getName", e);
		}
		return "";
	}

	public static Object getModel(LivingEntity entity){
		try {
			return getPixelmonModel.invoke(entity);
		} catch (Exception e) {
			LogManager.getLogger().error("getModel", e);
		}
		return null;
	}

	public static void setupModel(LivingEntity entity, Object model){
		try {
			if(modelSetupClass.isAssignableFrom(model.getClass())) {
				modelSetupMethod.invoke(model, entity);
			}
		} catch (Exception e) {
			LogManager.getLogger().error("setupModel", e);
		}
	}

	public static Object getPokemonData(Entity entity) {
		try {
			return getPokemonData.invoke(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getParty(PlayerEntity player) {
		try {
			if(isReforged()) {
				return getPartyStorage.invoke(storageManager.get(null), player.getUUID());
			}
			return ((Optional)getPartyStorage.invoke(partyStorage, player)).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getPc(PlayerEntity player) {
		try {
			if(isReforged()) {
				return getPcStorage.invoke(storageManager.get(null), player.getUUID());
			}
			return getPcStorage.invoke(pcStorage, player);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class getPixelmonClass() {
		return pixelmonClass;
	}

	public static void initEntity(LivingEntity entity, String name) {
		try {
			if(isReforged()) {
				Class c = Class.forName("com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies");
				Field f = c.getDeclaredField("ENGLISH_NAMES");
				f.setAccessible(true);
				Object2IntOpenHashMap<String> names = (Object2IntOpenHashMap<String>) f.get(null);
				f = c.getDeclaredField("REGISTERED_SPECIES");
				f.setAccessible(true);
				Int2ObjectOpenHashMap<Object> species = (Int2ObjectOpenHashMap)f.get(null);
				Object specie = species.get(names.getInt(name));

				c = Class.forName("com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory");
				Object pokemon = c.getMethod("create", specie.getClass()).invoke(null, specie);
				pixelmonClass.getMethod("setPokemon", pokemon.getClass()).invoke(entity, pokemon);
			}
			else{

			}
		} catch (Exception e) {
			LogManager.getLogger().error("initEntity", e);
		}
	}
}
