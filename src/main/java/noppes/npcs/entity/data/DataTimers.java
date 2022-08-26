package noppes.npcs.entity.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.EventHooks;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.ITimers;
import noppes.npcs.controllers.IScriptBlockHandler;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataTimers implements ITimers{
	private Object parent;
	private Map<Integer, Timer> timers = new HashMap<Integer, Timer>();
	
	public DataTimers(Object parent){
		this.parent = parent;
	}

	@Override
	public void start(int id, int ticks, boolean repeat) {
		if(timers.containsKey(id))
			throw new CustomNPCsException("There is already a timer with id: " + id);
		timers.put(id, new Timer(id, ticks, repeat));
	}

	@Override
	public void forceStart(int id, int ticks, boolean repeat) {
		timers.put(id, new Timer(id, ticks, repeat));
	}

	@Override
	public boolean has(int id) {
		return timers.containsKey(id);
	}

	@Override
	public boolean stop(int id) {
		return timers.remove(id) != null;
	}

	@Override
	public void reset(int id) {
		Timer timer = timers.get(id);
		if(timer == null)
			throw new CustomNPCsException("There is no timer with id: " + id);
		timer.ticks = 0;
	}


	public void save(CompoundNBT compound) {
		ListNBT list = new ListNBT();
		for(Timer timer : timers.values()){
			CompoundNBT c = new CompoundNBT();
			c.putInt("ID", timer.id);
			c.putInt("TimerTicks", timer.id);
			c.putBoolean("Repeat", timer.repeat);
			c.putInt("Ticks", timer.ticks);
			list.add(c);
		}
		compound.put("NpcsTimers", list);
	}

	public void load(CompoundNBT compound) {
		Map<Integer, Timer> timers = new HashMap<Integer, Timer>();
		ListNBT list = compound.getList("NpcsTimers", 10);
		for(int i = 0; i < list.size(); i++){
			CompoundNBT c = list.getCompound(i);
			Timer t = new Timer(c.getInt("ID"), c.getInt("TimerTicks"), c.getBoolean("Repeat"));
			t.ticks = c.getInt("Ticks");
			
			timers.put(t.id, t);
		}
		this.timers = timers;
	}
	
	public void update(){
		for(Timer timer : new ArrayList<Timer>(timers.values())){
			timer.update();
		}
	}
	
	class Timer{
		public int id;
		private boolean repeat;
		private int timerTicks;
		
		private int ticks = 0;
		
		public Timer(int id, int ticks, boolean repeat){
			this.id = id;
			this.repeat = repeat;
			this.timerTicks = ticks;
			this.ticks = ticks;
		}
		
		public void update(){
			if(ticks-- > 0)
				return;
			
			if(repeat)
				ticks = timerTicks;
			else
				DataTimers.this.stop(id);
			
			Object ob = DataTimers.this.parent;
			if(ob instanceof EntityNPCInterface)
				EventHooks.onNPCTimer((EntityNPCInterface)ob, id);
			else if(ob instanceof PlayerData)
				EventHooks.onPlayerTimer((PlayerData)ob, id);
			else
				EventHooks.onScriptBlockTimer((IScriptBlockHandler) ob, id);
			
		}
	}

	@Override
	public void clear() {
		timers = new HashMap<Integer, Timer>();
	}
}
