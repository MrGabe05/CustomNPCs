package noppes.npcs.entity.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.data.INPCAdvanced;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.FactionOptions;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.Lines;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketPlaySound;
import noppes.npcs.roles.*;
import noppes.npcs.util.ValueUtil;

import java.util.HashMap;

public class DataAdvanced implements INPCAdvanced{

    public Lines interactLines = new Lines();
    public Lines worldLines = new Lines();
    public Lines attackLines = new Lines();
    public Lines killedLines = new Lines();
    public Lines killLines = new Lines();
    public Lines npcInteractLines = new Lines();
    
    public boolean orderedLines = false;

    private String idleSound = "";
    private String angrySound = "";
    private String hurtSound = "minecraft:entity.player.hurt";
    private String deathSound = "minecraft:entity.player.hurt";
    private String stepSound = "";

    private final EntityNPCInterface npc;
    public FactionOptions factions = new FactionOptions();

    public boolean attackOtherFactions = false;
    public boolean defendFaction = false;
	public boolean disablePitch = false;
	public DataScenes scenes;

    public DataAdvanced(EntityNPCInterface npc) {
        this.npc = npc;
        scenes = new DataScenes(npc);
    }

    public CompoundNBT save(CompoundNBT compound) {
        compound.put("NpcLines", worldLines.save());
        compound.put("NpcKilledLines", killedLines.save());
        compound.put("NpcInteractLines", interactLines.save());
        compound.put("NpcAttackLines", attackLines.save());
        compound.put("NpcKillLines", killLines.save());
        compound.put("NpcInteractNPCLines", npcInteractLines.save());

        compound.putBoolean("OrderedLines", orderedLines);

        compound.putString("NpcIdleSound", idleSound);
        compound.putString("NpcAngrySound", angrySound);
        compound.putString("NpcHurtSound", hurtSound);
        compound.putString("NpcDeathSound", deathSound);
        compound.putString("NpcStepSound", stepSound);

        compound.putInt("FactionID", npc.getFaction().id);
        compound.putBoolean("AttackOtherFactions", attackOtherFactions);
        compound.putBoolean("DefendFaction", defendFaction);
        compound.putBoolean("DisablePitch", disablePitch);

        compound.putInt("Role", this.npc.role.getType());
        compound.putInt("NpcJob", this.npc.job.getType());
        compound.put("FactionPoints", factions.save(new CompoundNBT()));

		compound.put("NPCDialogOptions", nbtDialogs(npc.dialogs));
		
		compound.put("NpcScenes", scenes.save(new CompoundNBT()));
		
        return compound;
    }

    public void readToNBT(CompoundNBT compound) {
        interactLines.readNBT(compound.getCompound("NpcInteractLines"));
        worldLines.readNBT(compound.getCompound("NpcLines"));
        attackLines.readNBT(compound.getCompound("NpcAttackLines"));
        killedLines.readNBT(compound.getCompound("NpcKilledLines"));
        killLines.readNBT(compound.getCompound("NpcKillLines"));
        npcInteractLines.readNBT(compound.getCompound("NpcInteractNPCLines"));

        orderedLines = compound.getBoolean("OrderedLines");

        idleSound = compound.getString("NpcIdleSound");
        angrySound = compound.getString("NpcAngrySound");
        hurtSound = compound.getString("NpcHurtSound");
        deathSound = compound.getString("NpcDeathSound");
        stepSound = compound.getString("NpcStepSound");

        npc.setFaction(compound.getInt("FactionID"));
        npc.faction = npc.getFaction();
        attackOtherFactions = compound.getBoolean("AttackOtherFactions");
        defendFaction = compound.getBoolean("DefendFaction");
        disablePitch = compound.getBoolean("DisablePitch");

        setRole(compound.getInt("Role"));
        setJob(compound.getInt("NpcJob"));

        factions.load(compound.getCompound("FactionPoints"));

		npc.dialogs = getDialogs(compound.getList("NPCDialogOptions", 10));
		
		scenes.load(compound.getCompound("NpcScenes"));
    }

	private HashMap<Integer, DialogOption> getDialogs(ListNBT tagList) {
		HashMap<Integer, DialogOption> map = new HashMap<Integer, DialogOption>();
		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT nbttagcompound = tagList.getCompound(i);
			int slot = nbttagcompound.getInt("DialogSlot");
			DialogOption option = new DialogOption();
			option.readNBT(nbttagcompound.getCompound("NPCDialog"));
			option.optionType = OptionType.DIALOG_OPTION;
			map.put(slot, option);

		}
		return map;
	}


	private ListNBT nbtDialogs(HashMap<Integer, DialogOption> dialogs2) {
		ListNBT nbttaglist = new ListNBT();
		for (int slot : dialogs2.keySet()) {
			CompoundNBT nbttagcompound = new CompoundNBT();
			nbttagcompound.putInt("DialogSlot", slot);
			nbttagcompound.put("NPCDialog", dialogs2.get(slot)
					.writeNBT());
			nbttaglist.add(nbttagcompound);
		}
		return nbttaglist;
	}
	
	private Lines getLines(int type){
		if(type == 0)
			return interactLines;
		if(type == 1)
			return attackLines;
		if(type == 2)
			return worldLines;
		if(type == 3)
			return killedLines;
		if(type == 4)
			return killLines;
		if(type == 5)
			return npcInteractLines;
		
		return null;
	}
	
	@Override
	public void setLine(int type, int slot, String text, String sound){
		slot = ValueUtil.CorrectInt(slot, 0, 7);
		Lines lines = getLines(type);
		if(text == null || text.isEmpty())
			lines.lines.remove(slot);
		else{
			Line line = lines.lines.get(slot);
            if(line == null) {
                lines.lines.put(slot, line = new Line());
            }
			line.setText(text);
			line.setSound(sound);
		}
	}

	@Override
	public String getLine(int type, int slot){
		Line line = getLines(type).lines.get(slot);
		if(line == null)
			return "";
		return line.getText();
	}

	@Override
	public int getLineCount(int type){
		return getLines(type).lines.size();
	}

	@Override
	public String getSound(int type){
		String sound = null;
		if(type == 0)
			sound = idleSound;
		else if(type == 1)
			sound = angrySound;
		else if(type == 2)
			sound = hurtSound;
		else if(type == 3)
			sound = deathSound;
		else if(type == 4)
			sound = stepSound;

        if(sound != null && sound.isEmpty())
            return null;
		return NoppesStringUtils.cleanResource(sound);
	}

	public void playSound(int type, float volume, float pitch) {
        String sound = getSound(type);
        if(sound == null)
            return;
        BlockPos pos = npc.blockPosition();
        if(!npc.level.isClientSide) {
            Packets.sendNearby(npc.level, pos, 16, new PacketPlaySound(sound, pos, volume, pitch));
        }
        else{
            MusicController.Instance.playSound(SoundCategory.VOICE, sound, pos, volume, pitch);
        }
	}
	
	@Override
	public void setSound(int type, String sound){
		if(sound == null)
			sound = "";
		sound = NoppesStringUtils.cleanResource(sound);
		if(type == 0)
			idleSound = sound;
		else if(type == 1)
			angrySound = sound;
		else if(type == 2)
			hurtSound = sound;
		else if(type == 3)
			deathSound = sound;
		else if(type == 4)
			stepSound = sound;
	}
	

    public Line getInteractLine() {
        return interactLines.getLine(!orderedLines);
    }

    public Line getAttackLine() {
        return attackLines.getLine(!orderedLines);
    }

    public Line getKilledLine() {
        return killedLines.getLine(!orderedLines);
    }

    public Line getKillLine() {
        return killLines.getLine(!orderedLines);
    }

    public Line getWorldLine() {
        return worldLines.getLine(!orderedLines);
    }

    public Line getNPCInteractLine() {
        return npcInteractLines.getLine(!orderedLines);
    }

    public void setRole(int role) {
        if (RoleType.MAXSIZE <= role) {
            role -= 2;
        }
        role = role % RoleType.MAXSIZE;
        if(role == RoleType.NONE)
            npc.role = RoleInterface.NONE;
        else if(role == RoleType.BANK && !(npc.role instanceof RoleBank))
            npc.role = new RoleBank(npc);
        else if(role == RoleType.FOLLOWER && !(npc.role instanceof RoleFollower))
            npc.role = new RoleFollower(npc);
        else if(role == RoleType.MAILMAN && !(npc.role instanceof RolePostman))
            npc.role = new RolePostman(npc);
        else if(role == RoleType.TRADER && !(npc.role instanceof RoleTrader))
            npc.role = new RoleTrader(npc);
        else if(role == RoleType.TRANSPORTER && !(npc.role instanceof RoleTransporter))
            npc.role = new RoleTransporter(npc);
        else if(role == RoleType.COMPANION && !(npc.role instanceof RoleCompanion))
            npc.role = new RoleCompanion(npc);
        else if(role == RoleType.DIALOG && !(npc.role instanceof RoleDialog))
            npc.role = new RoleDialog(npc);
    }

    public void setJob(int job) {
        if(!npc.level.isClientSide)
        	npc.job.reset();

        job = job % JobType.MAXSIZE;
        if (job == JobType.NONE)
            npc.job = JobInterface.NONE;
        else if (job == JobType.BARD && !(npc.job instanceof JobBard)) 
            npc.job = new JobBard(npc);
        else if (job == JobType.HEALER && !(npc.job instanceof JobHealer)) 
            npc.job = new JobHealer(npc);
        else if (job == JobType.GUARD && !(npc.job instanceof JobGuard)) 
            npc.job = new JobGuard(npc);
        else if (job == JobType.ITEMGIVER && !(npc.job instanceof JobItemGiver)) 
            npc.job = new JobItemGiver(npc);
        else if (job == JobType.FOLLOWER && !(npc.job instanceof JobFollower)) 
            npc.job = new JobFollower(npc);
        else if (job == JobType.SPAWNER && !(npc.job instanceof JobSpawner)) 
            npc.job = new JobSpawner(npc);
        else if (job == JobType.CONVERSATION && !(npc.job instanceof JobConversation)) 
            npc.job = new JobConversation(npc);
        else if (job == JobType.CHUNKLOADER && !(npc.job instanceof JobChunkLoader))
            npc.job = new JobChunkLoader(npc);
        else if (job == JobType.PUPPET && !(npc.job instanceof JobPuppet))
            npc.job = new JobPuppet(npc);
        else if (job == JobType.BUILDER && !(npc.job instanceof JobBuilder))
            npc.job = new JobBuilder(npc);
        else if (job == JobType.FARMER && !(npc.job instanceof JobFarmer))
            npc.job = new JobFarmer(npc);
    }

    public boolean hasWorldLines() {
        return !worldLines.isEmpty();
    }
}
