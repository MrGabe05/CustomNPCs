package noppes.npcs.controllers.data;

import java.util.HashSet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.CustomNpcs;
import noppes.npcs.ICompatibilty;
import noppes.npcs.VersionCompatibility;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IAvailability;
import noppes.npcs.constants.EnumAvailabilityDialog;
import noppes.npcs.constants.EnumAvailabilityFaction;
import noppes.npcs.constants.EnumAvailabilityFactionType;
import noppes.npcs.constants.EnumAvailabilityQuest;
import noppes.npcs.constants.EnumAvailabilityScoreboard;
import noppes.npcs.constants.EnumDayTime;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.PlayerQuestController;

public class Availability implements ICompatibilty, IAvailability {
	public static HashSet<String> scores = new HashSet<String>();
	
	public int version = VersionCompatibility.ModRev;

	public EnumAvailabilityDialog dialogAvailable = EnumAvailabilityDialog.Always;
	public EnumAvailabilityDialog dialog2Available = EnumAvailabilityDialog.Always;
	public EnumAvailabilityDialog dialog3Available = EnumAvailabilityDialog.Always;
	public EnumAvailabilityDialog dialog4Available = EnumAvailabilityDialog.Always;
	public int dialogId = -1;
	public int dialog2Id = -1;
	public int dialog3Id = -1;
	public int dialog4Id = -1;
	
	public EnumAvailabilityQuest questAvailable = EnumAvailabilityQuest.Always;
	public EnumAvailabilityQuest quest2Available = EnumAvailabilityQuest.Always;
	public EnumAvailabilityQuest quest3Available = EnumAvailabilityQuest.Always;
	public EnumAvailabilityQuest quest4Available = EnumAvailabilityQuest.Always;
	public int questId = -1;
	public int quest2Id = -1;
	public int quest3Id = -1;
	public int quest4Id = -1;

	public EnumDayTime daytime = EnumDayTime.Always;
	
	public int factionId = -1;
	public int faction2Id = -1;
	
	public EnumAvailabilityFactionType factionAvailable = EnumAvailabilityFactionType.Always;
	public EnumAvailabilityFactionType faction2Available = EnumAvailabilityFactionType.Always;

	public EnumAvailabilityFaction factionStance = EnumAvailabilityFaction.Friendly;
	public EnumAvailabilityFaction faction2Stance = EnumAvailabilityFaction.Friendly;

	public EnumAvailabilityScoreboard scoreboardType = EnumAvailabilityScoreboard.EQUAL;
	public EnumAvailabilityScoreboard scoreboard2Type = EnumAvailabilityScoreboard.EQUAL;

	public String scoreboardObjective= "";
	public String scoreboard2Objective= "";
	
	public int scoreboardValue = 1;
	public int scoreboard2Value = 1;

	public int minPlayerLevel = 0;

	private boolean hasOptions = false;
	
    public void load(CompoundNBT compound){
		version = compound.getInt("ModRev");
		VersionCompatibility.CheckAvailabilityCompatibility(this, compound);

    	dialogAvailable = EnumAvailabilityDialog.values()[compound.getInt("AvailabilityDialog")];
    	dialog2Available = EnumAvailabilityDialog.values()[compound.getInt("AvailabilityDialog2")];
    	dialog3Available = EnumAvailabilityDialog.values()[compound.getInt("AvailabilityDialog3")];
    	dialog4Available = EnumAvailabilityDialog.values()[compound.getInt("AvailabilityDialog4")];
    	
    	dialogId = compound.getInt("AvailabilityDialogId");
    	dialog2Id = compound.getInt("AvailabilityDialog2Id");
    	dialog3Id = compound.getInt("AvailabilityDialog3Id");
    	dialog4Id = compound.getInt("AvailabilityDialog4Id");
    	
    	questAvailable = EnumAvailabilityQuest.values()[compound.getInt("AvailabilityQuest")];
    	quest2Available = EnumAvailabilityQuest.values()[compound.getInt("AvailabilityQuest2")];
    	quest3Available = EnumAvailabilityQuest.values()[compound.getInt("AvailabilityQuest3")];
    	quest4Available = EnumAvailabilityQuest.values()[compound.getInt("AvailabilityQuest4")];
    	
    	questId = compound.getInt("AvailabilityQuestId");
    	quest2Id = compound.getInt("AvailabilityQuest2Id");
    	quest3Id = compound.getInt("AvailabilityQuest3Id");
    	quest4Id = compound.getInt("AvailabilityQuest4Id");
    	
    	setFactionAvailability(compound.getInt("AvailabilityFaction"));
    	setFactionAvailabilityStance(compound.getInt("AvailabilityFactionStance"));

    	setFaction2Availability(compound.getInt("AvailabilityFaction2"));
    	setFaction2AvailabilityStance(compound.getInt("AvailabilityFaction2Stance"));

    	factionId = compound.getInt("AvailabilityFactionId");
    	faction2Id = compound.getInt("AvailabilityFaction2Id");

    	scoreboardObjective = compound.getString("AvailabilityScoreboardObjective");
    	scoreboard2Objective = compound.getString("AvailabilityScoreboard2Objective");

    	initScore(scoreboardObjective);
    	initScore(scoreboard2Objective);

    	scoreboardType = EnumAvailabilityScoreboard.values()[compound.getInt("AvailabilityScoreboardType")];
    	scoreboard2Type = EnumAvailabilityScoreboard.values()[compound.getInt("AvailabilityScoreboard2Type")];

    	scoreboardValue = compound.getInt("AvailabilityScoreboardValue");
    	scoreboard2Value = compound.getInt("AvailabilityScoreboard2Value");
    	
    	daytime = EnumDayTime.values()[compound.getInt("AvailabilityDayTime")];

    	minPlayerLevel = compound.getInt("AvailabilityMinPlayerLevel");

		hasOptions = checkHasOptions();
    }
    
    private void initScore(String objective) {
    	if(objective.isEmpty() || scores.contains(objective))
    		return;
		scores.add(objective);
    	
    	if(CustomNpcs.Server == null)
    		return;
    	for(ServerWorld level : CustomNpcs.Server.getAllLevels()) {
    		ServerScoreboard board = level.getScoreboard();
        	ScoreObjective so = board.getObjective(objective);
        	if(so != null) {
        		board.startTrackingObjective(so);
        	}
    	}
    }
    
	public CompoundNBT save(CompoundNBT compound){
		compound.putInt("ModRev", version);
		
		compound.putInt("AvailabilityDialog", dialogAvailable.ordinal());
		compound.putInt("AvailabilityDialog2", dialog2Available.ordinal());
		compound.putInt("AvailabilityDialog3", dialog3Available.ordinal());
		compound.putInt("AvailabilityDialog4", dialog4Available.ordinal());
		
		compound.putInt("AvailabilityDialogId", dialogId);
		compound.putInt("AvailabilityDialog2Id", dialog2Id);
		compound.putInt("AvailabilityDialog3Id", dialog3Id);
		compound.putInt("AvailabilityDialog4Id", dialog4Id);
		
		compound.putInt("AvailabilityQuest", questAvailable.ordinal());
		compound.putInt("AvailabilityQuest2", quest2Available.ordinal());
		compound.putInt("AvailabilityQuest3", quest3Available.ordinal());
		compound.putInt("AvailabilityQuest4", quest4Available.ordinal());
		
		compound.putInt("AvailabilityQuestId", questId);
		compound.putInt("AvailabilityQuest2Id", quest2Id);
		compound.putInt("AvailabilityQuest3Id", quest3Id);
		compound.putInt("AvailabilityQuest4Id", quest4Id);
		
		compound.putInt("AvailabilityFaction", factionAvailable.ordinal());
		compound.putInt("AvailabilityFaction2", faction2Available.ordinal());
		
		compound.putInt("AvailabilityFactionStance", factionStance.ordinal());
		compound.putInt("AvailabilityFaction2Stance", faction2Stance.ordinal());
		
		compound.putInt("AvailabilityFactionId", factionId);
		compound.putInt("AvailabilityFaction2Id", faction2Id);

    	compound.putString("AvailabilityScoreboardObjective", scoreboardObjective);
    	compound.putString("AvailabilityScoreboard2Objective", scoreboard2Objective);

    	compound.putInt("AvailabilityScoreboardType", scoreboardType.ordinal());
    	compound.putInt("AvailabilityScoreboard2Type", scoreboard2Type.ordinal());

    	compound.putInt("AvailabilityScoreboardValue", scoreboardValue);
    	compound.putInt("AvailabilityScoreboard2Value", scoreboard2Value);

		compound.putInt("AvailabilityDayTime", daytime.ordinal());
		compound.putInt("AvailabilityMinPlayerLevel", minPlayerLevel);
		return compound;
    }
	
	public void setFactionAvailability(int value) {
    	factionAvailable =  EnumAvailabilityFactionType.values()[value];
		hasOptions = checkHasOptions();
	}
	
	public void setFaction2Availability(int value) {
    	faction2Available =  EnumAvailabilityFactionType.values()[value];
		hasOptions = checkHasOptions();
	}
	
	public void setFactionAvailabilityStance(int integer) {
		factionStance = EnumAvailabilityFaction.values()[integer];
	}
	
	public void setFaction2AvailabilityStance(int integer) {
		faction2Stance = EnumAvailabilityFaction.values()[integer];
	}
	
	public boolean isAvailable(PlayerEntity player){
		if(daytime == EnumDayTime.Day){
			long time = player.level.getDayTime() % 24000;
			if(time > 12000)
				return false;
		}
		if(daytime == EnumDayTime.Night){
			long time = player.level.getDayTime() % 24000;
			if(time < 12000)
				return false;
		}
		
		if(!dialogAvailable(dialogId, dialogAvailable, player))
			return false;
		if(!dialogAvailable(dialog2Id, dialog2Available, player))
			return false;
		if(!dialogAvailable(dialog3Id, dialog3Available, player))
			return false;
		if(!dialogAvailable(dialog4Id, dialog4Available, player))
			return false;

		if(!questAvailable(questId, questAvailable, player))
			return false;
		if(!questAvailable(quest2Id, quest2Available, player))
			return false;
		if(!questAvailable(quest3Id, quest3Available, player))
			return false;
		if(!questAvailable(quest4Id, quest4Available, player))
			return false;

		if(!factionAvailable(factionId, factionStance, factionAvailable, player))
			return false;
		if(!factionAvailable(faction2Id, faction2Stance, faction2Available, player))
			return false;

		if(!scoreboardAvailable(player, scoreboardObjective, scoreboardType, scoreboardValue))
			return false;
		if(!scoreboardAvailable(player, scoreboard2Objective, scoreboard2Type, scoreboard2Value))
			return false;
		
		if(player.experienceLevel < minPlayerLevel)
			return false;
		
		return true;
	}
	
	private boolean scoreboardAvailable(PlayerEntity player, String objective, EnumAvailabilityScoreboard type, int value) {
		if(objective.isEmpty())
			return true;
		
		ScoreObjective sbObjective = player.getScoreboard().getObjective(objective);
		if(sbObjective == null)
			return false;
		
		if(!player.getScoreboard().hasPlayerScore(player.getName().getString(), sbObjective))
			return false;
		
		int i = player.getScoreboard().getOrCreatePlayerScore(player.getName().getString(), sbObjective).getScore();
		if(type == EnumAvailabilityScoreboard.EQUAL)
			return i == value;
		if(type == EnumAvailabilityScoreboard.BIGGER)
			return i > value;
		return i < value;
	}
	
	private boolean factionAvailable(int id, EnumAvailabilityFaction stance, EnumAvailabilityFactionType available, PlayerEntity player) {
		if(available == EnumAvailabilityFactionType.Always)
			return true;
		
		Faction faction = FactionController.instance.getFaction(id);
		if(faction == null)
			return true;
		
		PlayerFactionData data = PlayerData.get(player).factionData;
		int points = data.getFactionPoints(player, id);
		
		EnumAvailabilityFaction current = EnumAvailabilityFaction.Neutral;
		if(points < faction.neutralPoints)
			current = EnumAvailabilityFaction.Hostile;
		if(points >= faction.friendlyPoints)
			current = EnumAvailabilityFaction.Friendly;

		if(available == EnumAvailabilityFactionType.Is && stance == current){
			return true;
		}
		if(available == EnumAvailabilityFactionType.IsNot && stance != current){
			return true;
		}
		
		return false;
	}
	
	public boolean dialogAvailable(int id, EnumAvailabilityDialog en, PlayerEntity player){
		if(en == EnumAvailabilityDialog.Always)
			return true;
		boolean hasRead = PlayerData.get(player).dialogData.dialogsRead.contains(id);
		if(hasRead && en == EnumAvailabilityDialog.After)
			return true;
		else if(!hasRead && en == EnumAvailabilityDialog.Before)
			return true;
		return false;
	}
	
	public boolean questAvailable(int id, EnumAvailabilityQuest en, PlayerEntity player){
		if(en == EnumAvailabilityQuest.Always)
			return true;
		else if(en == EnumAvailabilityQuest.After && PlayerQuestController.isQuestFinished(player, id))
			return true;
		else if(en == EnumAvailabilityQuest.Before && !PlayerQuestController.isQuestFinished(player, id))
			return true;
		else if(en == EnumAvailabilityQuest.Active && PlayerQuestController.isQuestActive(player, id))
			return true;
		else if(en == EnumAvailabilityQuest.NotActive && !PlayerQuestController.isQuestActive(player, id))
			return true;
		else if(en == EnumAvailabilityQuest.Completed && PlayerQuestController.isQuestCompleted(player, id)) {
			return true;
		}
		else if(en == EnumAvailabilityQuest.CanStart && PlayerQuestController.canQuestBeAccepted(player, id)) {
			return true;
		}
		return false;
	}
	
	@Override
	public int getVersion() {
		return version;
	}
	
	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public boolean isAvailable(IPlayer player) {
		return isAvailable(player.getMCEntity());
	}

	@Override
	public int getDaytime() {
		return daytime.ordinal();
	}

	@Override
	public void setDaytime(int type) {
		daytime = EnumDayTime.values()[MathHelper.clamp(type, 0, 2)];
		hasOptions = checkHasOptions();
	}

	@Override
	public int getMinPlayerLevel() {
		return minPlayerLevel;
	}

	@Override
	public void setMinPlayerLevel(int level) {
		this.minPlayerLevel = level;
		hasOptions = checkHasOptions();
	}

	@Override
	public int getDialog(int i) {
		if(i < 0 && i > 3)
			throw new CustomNPCsException(i + " isnt between 0 and 3");
		if(i == 0) {
			return dialogId;
		}
		else if(i == 1) {
			return dialog2Id;
		}
		else if(i == 2) {
			return dialog3Id;
		}
		return dialog4Id;
	}

	@Override
	public void setDialog(int i, int id, int type) {
		if(i < 0 && i > 3)
			throw new CustomNPCsException(i + " isnt between 0 and 3");
		EnumAvailabilityDialog e = EnumAvailabilityDialog.values()[MathHelper.clamp(type, 0, 2)];
		if(i == 0) {
			dialogId = id;
			dialogAvailable = e;
		}
		else if(i == 1) {
			dialog2Id = id;
			dialog2Available = e;
		}
		else if(i == 2) {
			dialog3Id = id;
			dialog3Available = e;
		}
		else if(i == 3) {
			dialog4Id = id;
			dialog4Available = e;
		}
		hasOptions = checkHasOptions();
	}

	@Override
	public void removeDialog(int i) {
		if(i < 0 && i > 3)
			throw new CustomNPCsException(i + " isnt between 0 and 3");
		if(i == 0) {
			dialogId = -1;
			dialogAvailable = EnumAvailabilityDialog.Always;
		}
		else if(i == 1) {
			dialog2Id = -1;
			dialog2Available = EnumAvailabilityDialog.Always;
		}
		else if(i == 2) {
			dialog3Id = -1;
			dialog3Available = EnumAvailabilityDialog.Always;
		}
		else if(i == 3) {
			dialog4Id = -1;
			dialog4Available = EnumAvailabilityDialog.Always;
		}
		hasOptions = checkHasOptions();
	}

	@Override
	public int getQuest(int i) {
		if(i < 0 && i > 3)
			throw new CustomNPCsException(i + " isnt between 0 and 3");
		if(i == 0) {
			return questId;
		}
		else if(i == 1) {
			return quest2Id;
		}
		else if(i == 2) {
			return quest3Id;
		}
		return quest4Id;
	}

	@Override
	public void setQuest(int i, int id, int type) {
		if(i < 0 && i > 3)
			throw new CustomNPCsException(i + " isnt between 0 and 3");
		EnumAvailabilityQuest e = EnumAvailabilityQuest.values()[MathHelper.clamp(type, 0, 5)];
		if(i == 0) {
			questId = id;
			questAvailable = e;
		}
		else if(i == 1) {
			quest2Id = id;
			quest2Available = e;
		}
		else if(i == 2) {
			quest3Id = id;
			quest3Available = e;
		}
		else if(i == 3) {
			quest4Id = id;
			quest4Available = e;
		}
		hasOptions = checkHasOptions();
	}

	@Override
	public void removeQuest(int i) {
		if(i < 0 && i > 3)
			throw new CustomNPCsException(i + " isnt between 0 and 3");
		if(i == 0) {
			questId = -1;
			questAvailable = EnumAvailabilityQuest.Always;
		}
		else if(i == 1) {
			quest2Id = -1;
			quest2Available = EnumAvailabilityQuest.Always;
		}
		else if(i == 2) {
			quest3Id = -1;
			quest3Available = EnumAvailabilityQuest.Always;
		}
		else if(i == 3) {
			quest4Id = -1;
			quest4Available = EnumAvailabilityQuest.Always;
		}
		hasOptions = checkHasOptions();
	}

	@Override
	public void setFaction(int i, int id, int type, int stance) {
		if(i < 0 && i > 1)
			throw new CustomNPCsException(i + " isnt between 0 and 1");

		EnumAvailabilityFactionType e = EnumAvailabilityFactionType.values()[MathHelper.clamp(type, 0, 2)];
		EnumAvailabilityFaction ee = EnumAvailabilityFaction.values()[MathHelper.clamp(stance, 0, 2)];
		if(i == 0) {
			factionId = id;
			factionAvailable = e;
			factionStance = ee;
		}
		else if(i == 1) {
			faction2Id = id;
			faction2Available = e;
			faction2Stance = ee;
		}
		hasOptions = checkHasOptions();
	}

	@Override
	public void setScoreboard(int i, String objective, int type, int value) {
		if(i < 0 && i > 1)
			throw new CustomNPCsException(i + " isnt between 0 and 1");
		if(objective == null)
			objective = "";
		
		EnumAvailabilityScoreboard e = EnumAvailabilityScoreboard.values()[MathHelper.clamp(type, 0, 2)];
		if(i == 0) {
			scoreboardObjective = objective;
			scoreboardType = e;
			scoreboardValue = value;
		}
		else if(i == 1) {
			scoreboard2Objective = objective;
			scoreboard2Type = e;
			scoreboard2Value = value;
		}
		hasOptions = checkHasOptions();
	}

	@Override
	public void removeFaction(int i) {
		if(i < 0 && i > 1)
			throw new CustomNPCsException(i + " isnt between 0 and 1");
		
		if(i == 0) {
			factionId = -1;
			factionAvailable = EnumAvailabilityFactionType.Always;
			factionStance = EnumAvailabilityFaction.Friendly;
		}
		else if(i == 1) {
			faction2Id = -1;
			faction2Available = EnumAvailabilityFactionType.Always;
			faction2Stance = EnumAvailabilityFaction.Friendly;
		}
		hasOptions = checkHasOptions();
	}

	private boolean checkHasOptions() {
		if(dialogAvailable != EnumAvailabilityDialog.Always || dialog2Available != EnumAvailabilityDialog.Always ||
				dialog3Available != EnumAvailabilityDialog.Always || dialog4Available != EnumAvailabilityDialog.Always) {
			return true;
		}

		if(questAvailable != EnumAvailabilityQuest.Always || quest2Available != EnumAvailabilityQuest.Always ||
				quest3Available != EnumAvailabilityQuest.Always || quest4Available != EnumAvailabilityQuest.Always) {
			return true;
		}

		if(daytime != EnumDayTime.Always || minPlayerLevel > 0) {
			return true;
		}

		if(factionAvailable != EnumAvailabilityFactionType.Always || faction2Available != EnumAvailabilityFactionType.Always) {
			return true;
		}

		if(!scoreboardObjective.isEmpty() || !scoreboard2Objective.isEmpty()) {
			return true;
		}

		return false;
	}

	public boolean hasOptions() {
		return hasOptions;
	}
}
