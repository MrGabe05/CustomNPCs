package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;

public class JobConversation extends JobInterface {
	public Availability availability = new Availability();

	private final ArrayList<String> names = new ArrayList<String>();
	private final HashMap<String, EntityNPCInterface> npcs = new HashMap<String, EntityNPCInterface>();
	
	public HashMap<Integer,ConversationLine> lines = new HashMap<Integer,ConversationLine>();

	public int quest = -1;
	public String questTitle = "";
	public int generalDelay = 400;
	public int ticks = 100;
	public int range = 20;
	
	private ConversationLine nextLine;
	
	private boolean hasStarted = false;
	private int startedTicks = 20;
	public int mode = 0; //0:Always, 1:Player near

	public JobConversation(EntityNPCInterface npc) {
		super(npc);
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.put("ConversationAvailability", availability.save(new CompoundNBT()));
		compound.putInt("ConversationQuest", quest);
		compound.putInt("ConversationDelay", generalDelay);
		compound.putInt("ConversationRange", range);
		compound.putInt("ConversationMode", mode);

        ListNBT nbttaglist = new ListNBT();
        for(int slot : lines.keySet()){
        	ConversationLine line = lines.get(slot);
            CompoundNBT nbttagcompound = new CompoundNBT();
            nbttagcompound.putInt("Slot", slot);
            line.addAdditionalSaveData(nbttagcompound);
            
            nbttaglist.add(nbttagcompound);
        }
        
		compound.put("ConversationLines", nbttaglist);
		if(hasQuest())
			compound.putString("ConversationQuestTitle", getQuest().title);
		
		return compound;
	}

	@Override
	public void load(CompoundNBT compound) {
		names.clear();
		availability.load(compound.getCompound("ConversationAvailability"));
		quest = compound.getInt("ConversationQuest");
		generalDelay = compound.getInt("ConversationDelay");
		questTitle = compound.getString("ConversationQuestTitle");
		range = compound.getInt("ConversationRange");
		mode = compound.getInt("ConversationMode");

		ListNBT nbttaglist = compound.getList("ConversationLines", 10);
		HashMap<Integer, ConversationLine> map = new HashMap<Integer, ConversationLine>();
        for(int i = 0; i < nbttaglist.size(); i++){
            CompoundNBT nbttagcompound = nbttaglist.getCompound(i);
            ConversationLine line = new ConversationLine();
            line.readAdditionalSaveData(nbttagcompound);
            if(!line.npc.isEmpty() && !names.contains(line.npc.toLowerCase()))
            	names.add(line.npc.toLowerCase());
            
            map.put(nbttagcompound.getInt("Slot"), line);
        }
        lines = map;
        ticks = generalDelay;
	}

	public boolean hasQuest() {
		return getQuest() != null;
	}
	public Quest getQuest() {
		if(npc.isClientSide())
			return null;
		return QuestController.instance.quests.get(quest);
	}
	@Override
	public void aiUpdateTask() {
		ticks--;
		if(ticks > 0 || nextLine == null)
			return;
		say(nextLine);
		boolean seenNext = false;
		ConversationLine compare = nextLine;
		nextLine = null;
		for(ConversationLine line : lines.values()){
			if(line.isEmpty())
				continue;
			if(seenNext){
				nextLine = line;
				break;
			}
			if(line == compare){
				seenNext = true;
			}
		}
		if(nextLine != null)
			ticks = nextLine.delay;
		else if(hasQuest()){
			List<PlayerEntity> inRange = npc.level.getEntitiesOfClass(PlayerEntity.class, npc.getBoundingBox().inflate(range, range, range));

			for (PlayerEntity player : inRange){
				if(availability.isAvailable(player))
					PlayerQuestController.addActiveQuest(getQuest(), player);
			}
		}
	}
	

	@Override
	public boolean aiShouldExecute() {
		if(lines.isEmpty() || npc.isKilled() || npc.isAttacking() || !shouldRun())
			return false;
		if(!hasStarted && mode == 1){
			if(startedTicks-- > 0)
				return false;
			startedTicks = 10;
			if(npc.level.getEntitiesOfClass(PlayerEntity.class, npc.getBoundingBox().inflate(range, range, range)).isEmpty()){
				return false;
			}
		}
		
		for(ConversationLine line : lines.values()){
			if(line == null || line.isEmpty())
				continue;
			nextLine = line;
			break;
		}
		return nextLine != null;
	}

	private boolean shouldRun() {
		ticks--;
		if(ticks > 0)
			return false;
		npcs.clear();
		List<EntityNPCInterface> list = npc.level.getEntitiesOfClass(EntityNPCInterface.class, npc.getBoundingBox().inflate(10, 10, 10));
		for(EntityNPCInterface npc : list){
			String name = npc.getName().getString().toLowerCase();
			if(!npc.isKilled() && !npc.isAttacking() && names.contains(name))
				npcs.put(name, npc);
		}
		boolean bo = names.size() == npcs.size();
		if(!bo)
			ticks = 20;
		return bo;
	}

	@Override
	public boolean aiContinueExecute() {
		for(EntityNPCInterface npc : npcs.values()){
			if(npc.isKilled() || npc.isAttacking())
				return false;
		}
		return nextLine != null;
	}

	@Override
	public void stop() {
		nextLine = null;
		ticks = generalDelay;
		hasStarted = false;
	}

	@Override
	public void aiStartExecuting() {
		startedTicks = 20;
		hasStarted = true;
	}
	
	private void say(ConversationLine line) {
		List<PlayerEntity> inRange = npc.level.getEntitiesOfClass(PlayerEntity.class, npc.getBoundingBox().inflate(range, range, range));

		EntityNPCInterface npc = npcs.get(line.npc.toLowerCase());
		if(npc == null)
			return;
		for (PlayerEntity player : inRange){
			if(availability.isAvailable(player))
				npc.say(player, line);
		}
	}

	@Override
	public void reset() {
		hasStarted = false;
		stop();
		ticks = 60;
	}
	@Override
	public void killed() {
		reset();
	}
	public class ConversationLine extends Line{
		public String npc = "";
		public int delay = 40;
		
		public void addAdditionalSaveData(CompoundNBT compound) {
			compound.putString("Line", text);
			compound.putString("Npc", npc);
			compound.putString("Sound", sound);
			compound.putInt("Delay", delay);
		}

		public void readAdditionalSaveData(CompoundNBT compound) {
			text = compound.getString("Line");
			npc = compound.getString("Npc");
			sound = compound.getString("Sound");
			delay = compound.getInt("Delay");
		}
		
		public boolean isEmpty(){
			return npc.isEmpty() || text.isEmpty();
		}
	}
	public ConversationLine getLine(int slot) {
		if(lines.containsKey(slot))
			return lines.get(slot);
		ConversationLine line = new ConversationLine();
		lines.put(slot, line);
		return line;
	}

	@Override
	public int getType() {
		return JobType.CONVERSATION;
	}
}
