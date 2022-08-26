package noppes.npcs.controllers;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Event;
import noppes.npcs.*;
import noppes.npcs.api.constants.*;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.shared.common.util.LogWriter;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public class ScriptContainer {	
	private static final String lock = "lock";
	public static ScriptContainer Current;
	private static String CurrentType;
	
	public final static HashMap<String, Object> Data = new HashMap<String, Object >();
	static {
		FillMap(AnimationType.class);
		FillMap(EntitiesType.class);
		FillMap(RoleType.class);
		FillMap(JobType.class);
		FillMap(SideType.class);
		FillMap(PotionEffectType.class);
		FillMap(ParticleType.class);
		Data.put("PosZero", new BlockPosWrapper(BlockPos.ZERO));
	}
	
	private static void FillMap(Class c) {
		try {
			Data.put(c.getSimpleName(), c.newInstance());
		}
		catch(Exception e) {}
		Field[] declaredFields = c.getDeclaredFields();
		for (Field field : declaredFields) {
			try {
				if (Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
			    	Data.put(c.getSimpleName() + "_" + field.getName(), field.getInt(null));
			    }
			}
			catch(Exception e) {}
		}
	}
	
	public String fullscript = "";
	public String script = "";
	public TreeMap<Long, String> console = new TreeMap<Long, String>();
	public boolean errored = false;
	public List<String> scripts = new ArrayList<String>();
	
	private HashSet<String> unknownFunctions = new HashSet<String>();
	
	public long lastCreated = 0;

	private String currentScriptLanguage = null;
	private ScriptEngine engine = null;
	private IScriptHandler handler = null;
	
	private boolean init = false;

	private static Method luaCoerce;
	private static Method luaCall;

	public ScriptContainer(IScriptHandler handler){
		this.handler = handler;
	}
	
	public void load(CompoundNBT compound) {
		script = compound.getString("Script");
		console = NBTTags.GetLongStringMap(compound.getList("Console", 10));
		scripts = NBTTags.getStringList(compound.getList("ScriptList", 10));
		lastCreated = 0;
	}

	public CompoundNBT save(CompoundNBT compound) {
		compound.putString("Script", script);
		compound.put("Console", NBTTags.NBTLongStringMap(console));
		compound.put("ScriptList", NBTTags.nbtStringList(scripts));
		return compound;
	}

	private String getFullCode() {
		if(!init){
			fullscript = script;
			if(!fullscript.isEmpty())
				fullscript += "\n";
			for(String loc : scripts){
				String code = ScriptController.Instance.scripts.get(loc);
				if(code != null && !code.isEmpty())
					fullscript += code + "\n";
			}
			unknownFunctions = new HashSet<String>();
		}
		return fullscript;
	}
	
	public void run(EnumScriptType type, Event event){		
		run(type.function, event);
	}
	
	public void run(String type, Object event){		
		if(errored || !hasCode() || unknownFunctions.contains(type) || !CustomNpcs.EnableScripting)
			return;
		
		setEngine(handler.getLanguage());
		if(engine == null)
			return;
		if(ScriptController.Instance.lastLoaded > this.lastCreated){
			this.lastCreated = ScriptController.Instance.lastLoaded;
			init = false;
		}
		synchronized(lock) {
	    	Current = this;
	    	CurrentType = type;
			
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter(sw);
		    
		    engine.getContext().setWriter(pw);
		    engine.getContext().setErrorWriter(pw);
		    
		    try {
			    if(!init){
					engine.eval(getFullCode());
					init = true;
			    }
			    if(engine.getFactory().getLanguageName().equals("lua")){
			    	Object ob = engine.get(type);
			    	if(ob != null){
			    		if(luaCoerce == null){
			    			luaCoerce = Class.forName("org.luaj.vm2.lib.jse.CoerceJavaToLua").getMethod("coerce", Object.class);
					    	luaCall = ob.getClass().getMethod("call", Class.forName("org.luaj.vm2.LuaValue"));
			    		}
				    	luaCall.invoke(ob, luaCoerce.invoke(null, event));
			    	}
			    	else{
						unknownFunctions.add(type);
			    	}
			    }
			    else{
			    	((Invocable)engine).invokeFunction(type, event);
			    }
			} catch (NoSuchMethodException e) {
				unknownFunctions.add(type);
			} catch (Throwable e) {
				errored = true;
				e.printStackTrace(pw);
				NoppesUtilServer.NotifyOPs(handler.noticeString() + " script errored");
			}
		    finally {
				appandConsole(sw.getBuffer().toString().trim());
				
				pw.close();
		    	Current = null;
		    }
		}
	}

	public void appandConsole(String message) {
		if(message == null || message.isEmpty())
			return;
		long time = System.currentTimeMillis();
		if(console.containsKey(time)){
			message = console.get(time) + "\n" + message;
		}
		console.put(time, message);
		
		while(console.size() > 40) {
			console.remove(console.firstKey());
		}
	}

	public boolean hasCode() {
		return !getFullCode().isEmpty();
	}

	public void setEngine(String scriptLanguage) {
		if(currentScriptLanguage != null && currentScriptLanguage.equals(scriptLanguage))
			return;
		engine = ScriptController.Instance.getEngineByName(scriptLanguage);
		if(engine == null){
			errored = true;
			return;
		}
		for(Entry<String, Object> entry : Data.entrySet()) {
			engine.put(entry.getKey(), entry.getValue());
		}
		engine.put("dump", new Dump());
		engine.put("log", new Log());
		currentScriptLanguage = scriptLanguage;
		init = false;
	}
	
	public boolean isValid() {
		return init && !errored;
	}

	private class Dump implements Function<Object, String> {
		@Override
		public String apply(Object o) {
			if(o == null) {
				return "null";
			}
			StringBuilder builder = new StringBuilder();
			builder.append(o + ":" + NoppesStringUtils.newLine());
			for (Field field : o.getClass().getFields()) {
				try {
					builder.append(field.getName() + " - " + field.getType().getSimpleName() + ", ");
				} catch (IllegalArgumentException e) {}
			}
			
			for (Method method : o.getClass().getMethods()) {
				try {
					String s = method.getName() + "(";
					for (Class c : method.getParameterTypes()) {
						s += c.getSimpleName() + ", ";
					}
					if(s.endsWith(", "))
						s = s.substring(0, s.length() - 2);
					builder.append(s + "), ");
				} catch (IllegalArgumentException e) {}
			}
			return builder.toString();
		}
	}

	private class Log implements Function<Object, Void> {
		@Override
		public Void apply(Object o) {
			appandConsole(o + "");
			LogWriter.info(o + "");
			return null;
		}
	}
}
