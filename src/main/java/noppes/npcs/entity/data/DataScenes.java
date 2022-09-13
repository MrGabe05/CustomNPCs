package noppes.npcs.entity.data;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.util.ValueUtil;

import java.util.*;

public class DataScenes {	
	private final EntityNPCInterface npc;
	
	public List<SceneContainer> scenes = new ArrayList<SceneContainer>();
	
	public static Map<String, SceneState> StartedScenes = new HashMap<String, SceneState>();	
	public static List<SceneContainer> ScenesToRun = new ArrayList<SceneContainer>();
	
	private LivingEntity owner = null;
	private String ownerScene = null;
	
	public DataScenes(EntityNPCInterface npc){
		this.npc = npc;
	}

	public CompoundNBT save(CompoundNBT compound) {
		ListNBT list = new ListNBT();
		for(SceneContainer scene : scenes){
			list.add(scene.save(new CompoundNBT()));
		}
		compound.put("Scenes", list);
		return compound;
	}

	public void load(CompoundNBT compound) {
		ListNBT list = compound.getList("Scenes", 10);
		List<SceneContainer> scenes = new ArrayList<SceneContainer>();
		for(int i = 0; i < list.size(); i++){
			SceneContainer scene = new SceneContainer();
			scene.load(list.getCompound(i));
			scenes.add(scene);
		}
		this.scenes = scenes;
	}

	public LivingEntity getOwner(){
		return owner;
	}

	public static void Toggle(CommandSource sender, String id) {
		SceneState state = StartedScenes.get(id.toLowerCase());
		if(state == null || state.paused){
			Start(sender, id);
		}
		else{
			state.paused = true;
			NoppesUtilServer.NotifyOPs("Paused scene %s at %s", id, state.ticks);			
		}
	}
	
	public static void Start(CommandSource sender, String id) {
		SceneState state = StartedScenes.get(id.toLowerCase());
		if(state == null){
			NoppesUtilServer.NotifyOPs("Started scene %s", id);
			StartedScenes.put(id.toLowerCase(), new SceneState());
		}
		else if(state.paused){
			state.paused = false;
			NoppesUtilServer.NotifyOPs("Started scene %s from %s", id, state.ticks);
		}
		
	}

	public static void Pause(CommandSource sender, String id) {
		if(id == null){
			for(SceneState state : StartedScenes.values()){
				state.paused = true;
			}
			NoppesUtilServer.NotifyOPs("Paused all scenes");
		}
		else{
			SceneState state = StartedScenes.get(id.toLowerCase());
			if(state == null)
				sender.sendSuccess(new TranslationTextComponent("Unknown scene %s ", id), false);
			else{
				state.paused = true;
				NoppesUtilServer.NotifyOPs("Paused scene %s at %s", id, state.ticks);
			}
		}
	}

	public static void Reset(CommandSource sender, String id) {
		if(id == null){
			if(StartedScenes.isEmpty())
				return;
			StartedScenes = new HashMap<String, SceneState>();
			NoppesUtilServer.NotifyOPs("Reset all scene");
		}
		else if(StartedScenes.remove(id.toLowerCase()) == null)
			sender.sendSuccess(new TranslationTextComponent("Unknown scene %s ", id), false);
		else
			NoppesUtilServer.NotifyOPs("Reset scene %s", id);
	}

	public void update() {
		for(SceneContainer scene : scenes){
			if(scene.validState())
				ScenesToRun.add(scene);
		}
		if(owner != null && !StartedScenes.containsKey(ownerScene.toLowerCase())){
			owner = null;
			ownerScene = null;
		}
	}
	
	public static class SceneState{
		public boolean paused = false;
		public int ticks = -1;
	}
	
	public class SceneContainer{
		public int btn = 0;
		public String name = "";
		public String lines = "";	
		public boolean enabled = false;

		public int ticks = -1;
		private SceneState state = null;
		
		private List<SceneEvent> events = new ArrayList<SceneEvent>();

		public CompoundNBT save(CompoundNBT compound) {
			compound.putBoolean("Enabled", enabled);
			compound.putString("Name", name);	
			compound.putString("Lines", lines);	
			compound.putInt("Button", btn);
			compound.putInt("Ticks", ticks);
			return compound;
		}

		public boolean validState() {
			if(!enabled)
				return false;
			if(state != null){
				if(StartedScenes.containsValue(state))
					return !state.paused;
				state = null;
			}
			state = StartedScenes.get(name.toLowerCase());
			if(state == null)
				state = StartedScenes.get(btn + "btn");
			if(state != null)
				return !state.paused;			
			return false;
		}

		public void load(CompoundNBT compound) {
			enabled = compound.getBoolean("Enabled");						
			name = compound.getString("Name");				
			lines = compound.getString("Lines");
			btn = compound.getInt("Button");
			ticks = compound.getInt("Ticks");
			
			ArrayList<SceneEvent> events = new ArrayList<SceneEvent>();
			for(String line : lines.split("\r\n|\r|\n")){
				SceneEvent event = SceneEvent.parse(line);
				if(event != null)
					events.add(event);
			}
			Collections.sort(events);
			this.events = events;
		}
		
		public void update(){
			if(!enabled || events.isEmpty() || state == null)
				return;
			for(SceneEvent event : events){
				if(event.ticks > state.ticks)
					break;
				if(event.ticks == state.ticks){
					try {
						handle(event);
					} catch (Exception e) {
						
					}
				}
			}
			ticks = state.ticks;
		}

		private LivingEntity getEntity(String name){
			UUID uuid = null;
			try{
				uuid = UUID.fromString(name);
			}
			catch(Exception e){}
			for(Entity entity : ((ServerWorld)npc.getCommandSenderWorld()).entitiesById.values()){
				if(!(entity instanceof LivingEntity))
					continue;

				if(uuid != null && entity.getUUID() == uuid){
					return (LivingEntity) entity;
				}
				if(name.equalsIgnoreCase(entity.getName().getString())){
					return (LivingEntity) entity;
				}
			}
			return null;
		}

		private BlockPos parseBlockPos(BlockPos blockpos, String[] args, int startIndex, boolean centerBlock) throws Exception {
			return new BlockPos(parseDouble(blockpos.getX(), args[startIndex], -30000000, 30000000, centerBlock), parseDouble(blockpos.getY(), args[startIndex + 1], 0, 256, false), parseDouble(blockpos.getZ(), args[startIndex + 2], -30000000, 30000000, centerBlock));
		}

		private double parseDouble(double base, String input, int min, int max, boolean centerBlock) throws Exception {
			boolean flag = input.startsWith("~");

			if (flag && Double.isNaN(base))
			{
				throw new Exception("invalid number");
			}
			else
			{
				double d0 = flag ? base : 0.0D;

				if (!flag || input.length() > 1)
				{
					boolean flag1 = input.contains(".");

					if (flag)
					{
						input = input.substring(1);
					}
					d0 += Double.parseDouble(input);

					if (!flag1 && !flag && centerBlock)
					{
						d0 += 0.5D;
					}
				}

				if (min != 0 || max != 0)
				{
					if (d0 < (double)min)
					{
						throw new Exception("number too small");
					}

					if (d0 > (double)max)
					{
						throw new Exception("number too big");
					}
				}

				return d0;
			}
		}
		
		private void handle(SceneEvent event) throws Exception{
			if(event.type == SceneType.MOVE){
				String[] param = event.param.split(" ");
				while(param.length > 1){
					boolean move = false;
					if(param[0].startsWith("to")){
						move = true;
					}
					else if(!param[0].startsWith("tp")){
						break;
					}
					
					BlockPos pos = null;
					if(param[0].startsWith("@")){
			            LivingEntity entitylivingbase = getEntity(param[0]);
			            if(entitylivingbase != null)
			            	pos = entitylivingbase.blockPosition();
			            param = Arrays.copyOfRange(param, 2, param.length);
					}
					else if(param.length < 4){
						return;
					}
					else{
						pos = parseBlockPos(npc.blockPosition(), param, 1, false);
			            param = Arrays.copyOfRange(param, 4, param.length);
					}
					if(pos == null)
						continue;
					npc.ais.setStartPos(pos);
					npc.getNavigation().stop();
					if(move){
						Path pathentity = npc.getNavigation().createPath(pos, 0);
						npc.getNavigation().moveTo(pathentity, 1);
					}
					else if(!npc.isInRange(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 2))
						npc.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				}				
			}
			else if(event.type == SceneType.SAY){
				npc.saySurrounding(new Line(event.param));
			}
			else if(event.type == SceneType.ROTATE){
				npc.lookAi.stop();
				if(event.param.startsWith("@")){
		            LivingEntity entitylivingbase = getEntity(event.param);
		            npc.lookAi.rotate(npc.level.getNearestPlayer(entitylivingbase, 30));
				}
				else{
					npc.lookAi.rotate(Integer.parseInt(event.param));
				}
			}
			else if(event.type == SceneType.EQUIP){
				String[] args = event.param.split(" ");
				if(args.length < 2)
					return;
				IItemStack itemstack = null;
				if(!args[1].equalsIgnoreCase("none")){
					ResourceLocation resourcelocation = new ResourceLocation(args[1]);
					Item item = ForgeRegistries.ITEMS.getValue(resourcelocation);
		            int i = args.length >= 3 ? ValueUtil.CorrectInt(Integer.parseInt(args[2]), 1, 64) : 1;
		            itemstack = NpcAPI.Instance().getIItemStack(new ItemStack(item, i));
				}
				
	            if(args[0].equalsIgnoreCase("main"))
	            	npc.inventory.weapons.put(0, itemstack);
	            else if(args[0].equalsIgnoreCase("off"))
	            	npc.inventory.weapons.put(2, itemstack);
	            else if(args[0].equalsIgnoreCase("proj"))
	            	npc.inventory.weapons.put(1, itemstack);
	            else if(args[0].equalsIgnoreCase("head"))
	            	npc.inventory.armor.put(0, itemstack);
	            else if(args[0].equalsIgnoreCase("body"))
	            	npc.inventory.armor.put(1, itemstack);
	            else if(args[0].equalsIgnoreCase("legs"))
	            	npc.inventory.armor.put(2, itemstack);
	            else if(args[0].equalsIgnoreCase("boots"))
	            	npc.inventory.armor.put(3, itemstack);
			}
			else if(event.type == SceneType.ATTACK){
				if(event.param.equals("none"))
					npc.setTarget(null);
				else{
					LivingEntity entity = getEntity(event.param);
					if(entity != null)
						npc.setTarget(entity);
				}
			}
			else if(event.type == SceneType.THROW){
				String[] args = event.param.split(" ");
				LivingEntity entity = getEntity(args[0]);
				if(entity == null)
					return;
				float damage = Float.parseFloat(args[1]);
				if(damage <= 0)
					damage = 0.01f;
				ItemStack stack = ItemStackWrapper.MCItem(npc.inventory.getProjectile());
				if(args.length > 2){
					ResourceLocation resourcelocation = new ResourceLocation(args[2]);
					Item item = ForgeRegistries.ITEMS.getValue(resourcelocation);
		            stack = new ItemStack(item, 1);
				}
				EntityProjectile projectile = npc.shoot(entity, 100, stack, false);
				projectile.damage = damage;
			}
			else if(event.type == SceneType.ANIMATE){
				npc.animateAi.temp = AnimationType.NORMAL;
				if(event.param.equalsIgnoreCase("sleep"))
					npc.animateAi.temp = AnimationType.SLEEP;
				else if(event.param.equalsIgnoreCase("sneak"))
					npc.ais.animationType = AnimationType.SNEAK;
				else if(event.param.equalsIgnoreCase("normal"))
					npc.ais.animationType = AnimationType.NORMAL;
				else if(event.param.equalsIgnoreCase("sit"))
					npc.animateAi.temp = AnimationType.SIT;
				else if(event.param.equalsIgnoreCase("crawl"))
					npc.ais.animationType = AnimationType.CRAWL;
				else if(event.param.equalsIgnoreCase("bow"))
					npc.animateAi.temp = AnimationType.BOW;
				else if(event.param.equalsIgnoreCase("yes"))
					npc.animateAi.temp = AnimationType.YES;
				else if(event.param.equalsIgnoreCase("no"))
					npc.animateAi.temp = AnimationType.NO;
			}
			else if(event.type == SceneType.COMMAND){
				NoppesUtilServer.runCommand(npc, npc.getName().getString(), event.param, null);
			}
			else if(event.type == SceneType.STATS){
				int i = event.param.indexOf(" ");
				if(i <= 0)
					return;
				String type = event.param.substring(0, i).toLowerCase();
				String value = event.param.substring(i).trim();
				try{
					if(type.equals("walking_speed")){
						npc.ais.setWalkingSpeed(ValueUtil.CorrectInt(Integer.parseInt(value), 0, 10));
					}
					else if(type.equals("size")){
						npc.display.setSize(ValueUtil.CorrectInt(Integer.parseInt(value), 1, 30));
					}
					else{
						NoppesUtilServer.NotifyOPs("Unknown scene stat: " + type);
					}
				}
				catch(NumberFormatException e){
					NoppesUtilServer.NotifyOPs("Unknown scene stat " + type + " value: " + value);
				}
				
			}
			else if(event.type == SceneType.FACTION){
				npc.setFaction(Integer.parseInt(event.param));
			}
			else if(event.type == SceneType.FOLLOW){
				if(event.param.equalsIgnoreCase("none")){
					owner = null;
					ownerScene = null;
				}
				else{
					LivingEntity entity = getEntity(event.param);
					if(entity == null)
						return;
					owner = entity;
					ownerScene = name;
				}
			}
		}
	}
	
	public static class SceneEvent implements Comparable<SceneEvent>{
		public int ticks = 0;
		public SceneType type;
		public String param = "";
		
		
		
		public String toString(){
			return ticks + " " + type.name() + " " + param;
		}
		
		public static SceneEvent parse(String str){
			SceneEvent event = new SceneEvent();
			int i = str.indexOf(" ");
			if(i <= 0)
				return null;
			try{
				event.ticks = Integer.parseInt(str.substring(0, i));
				str = str.substring(i + 1);
			}
			catch(NumberFormatException ex){
				return null;
			}
			i = str.indexOf(" ");
			if(i <= 0)
				return null;
			String name = str.substring(0, i);
			for(SceneType type : SceneType.values()){
				if(name.equalsIgnoreCase(type.name()))
					event.type = type;
			}
			if(event.type == null)
				return null;
			event.param = str.substring(i + 1);
			
			return event;
			
		}

		@Override
		public int compareTo(SceneEvent o) {
			return ticks - o.ticks;
		}
	}
	
	public enum SceneType{
		ANIMATE, MOVE, FACTION, COMMAND, EQUIP, THROW, ATTACK, FOLLOW, SAY, ROTATE, STATS
	}

	public void addScene(String name) {
		if(name.isEmpty())
			return;
		SceneContainer scene = new SceneContainer();
		scene.name = name;
		scenes.add(scene);
	}
}
