package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.ICompatibilty;
import noppes.npcs.VersionCompatibility;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.handler.data.IAvailability;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IDialogCategory;
import noppes.npcs.api.handler.data.IDialogOption;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.db.DatabaseColumn;


public class Dialog implements ICompatibilty, IDialog {
	public int version = VersionCompatibility.ModRev;

	@DatabaseColumn(name = "id", type = DatabaseColumn.Type.INT)
	public int id = -1;

	@DatabaseColumn(name = "title", type = DatabaseColumn.Type.VARCHAR)
	public String title = "";
	@DatabaseColumn(name = "text", type = DatabaseColumn.Type.TEXT)
	public String text = "";
	@DatabaseColumn(name = "quest", type = DatabaseColumn.Type.INT)
	public int quest = -1;

	@DatabaseColumn(name = "category", type = DatabaseColumn.Type.VARCHAR)
	public String categoryName;
	public final DialogCategory category;

	public HashMap<Integer,DialogOption> options = new HashMap<Integer,DialogOption>();

	public Availability availability = new Availability();
	public FactionOptions factionOptions = new FactionOptions();
	public String sound;
	public String command = "";
	public PlayerMail mail = new PlayerMail();
	
	public boolean hideNPC = false;
	public boolean showWheel = false;
	public boolean disableEsc = false;
	
	public Dialog(DialogCategory category){
		this.category = category;
	}
	
	public boolean hasDialogs(PlayerEntity player) {
		for(DialogOption option: options.values())
			if(option != null && option.optionType == OptionType.DIALOG_OPTION && option.hasDialog() && option.isAvailable(player))
				return true;
		return false;
	}

	public void readNBT(CompoundNBT compound) {
		id = compound.getInt("DialogId");
		readNBTPartial(compound);
	}
	public void readNBTPartial(CompoundNBT compound) {
    	version = compound.getInt("ModRev");
		VersionCompatibility.CheckAvailabilityCompatibility(this, compound);
		
    	title = compound.getString("DialogTitle");
    	text = compound.getString("DialogText");
    	quest = compound.getInt("DialogQuest");
    	sound = compound.getString("DialogSound");
		command = compound.getString("DialogCommand");
		mail.readNBT(compound.getCompound("DialogMail"));

		hideNPC = compound.getBoolean("DialogHideNPC");
		showWheel = compound.getBoolean("DialogShowWheel");
		disableEsc = compound.getBoolean("DialogDisableEsc");
    	
		ListNBT options = compound.getList("Options", 10);
		HashMap<Integer,DialogOption> newoptions = new HashMap<Integer,DialogOption>();
		for(int iii = 0; iii < options.size();iii++){
            CompoundNBT option = options.getCompound(iii);
            int opslot = option.getInt("OptionSlot");
            DialogOption dia = new DialogOption();
            dia.readNBT(option.getCompound("Option"));
            if(dia.hasDialog()){

			}
            newoptions.put(opslot, dia);
            dia.slot = opslot;
		}
		this.options = newoptions;

    	availability.load(compound);
    	factionOptions.load(compound);
	}


	public CompoundNBT save(CompoundNBT compound) {
    	compound.putInt("DialogId", id);
		return writeToNBTPartial(compound);
	}
	
	public CompoundNBT writeToNBTPartial(CompoundNBT compound) {
		compound.putString("DialogTitle", title);
		compound.putString("DialogText", text);
		compound.putInt("DialogQuest", quest);
		compound.putString("DialogCommand", command);
		compound.put("DialogMail", mail.writeNBT());
		compound.putBoolean("DialogHideNPC", hideNPC);
		compound.putBoolean("DialogShowWheel", showWheel);
		compound.putBoolean("DialogDisableEsc", disableEsc);
		
		if(sound != null && !sound.isEmpty())
			compound.putString("DialogSound", sound);

		ListNBT options = new ListNBT();
		for(int opslot : this.options.keySet()){
			CompoundNBT listcompound = new CompoundNBT();
			listcompound.putInt("OptionSlot", opslot);
			listcompound.put("Option", this.options.get(opslot).writeNBT());
			options.add(listcompound);
		}
		compound.put("Options", options);
		
    	availability.save(compound);
    	factionOptions.save(compound);
		compound.putInt("ModRev", version);
		return compound;
	}

	public boolean hasQuest() {
		return getQuest() != null;
	}
	public Quest getQuest() {
		if(QuestController.instance == null)
			return null;
		return QuestController.instance.quests.get(quest);
	}
	public boolean hasOtherOptions() {
		for(DialogOption option: options.values())
			if(option != null && option.optionType != OptionType.DISABLED)
				return true;
		return false;
	}
	
	public Dialog copy(PlayerEntity player) {
		Dialog dialog = new Dialog(category);
		dialog.id = id;
		dialog.text = text;
		dialog.title = title;
		dialog.quest = quest;
		dialog.sound = sound;
		dialog.mail = mail;
		dialog.command = command;
		dialog.hideNPC = hideNPC;
		dialog.showWheel = showWheel;
		dialog.disableEsc = disableEsc;
		
		for(int slot : options.keySet()){
			DialogOption option = options.get(slot);
			if(option.optionType == OptionType.DIALOG_OPTION && (!option.hasDialog() || !option.isAvailable(player)))
				continue;
			dialog.options.put(slot, option);
		}
		return dialog;
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
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return title;
	}

	@Override
	public List<IDialogOption> getOptions() {
		return new ArrayList<IDialogOption>(options.values());
	}

	@Override
	public IDialogOption getOption(int slot) {
		IDialogOption option = options.get(slot);
		if(option == null)
			throw new CustomNPCsException("There is no DialogOption for slot: " + slot);
		return option;
	}

	@Override
	public IAvailability getAvailability() {
		return availability;
	}

	@Override
	public IDialogCategory getCategory() {
		return category;
	}

	@Override
	public void save() {
		DialogController.instance.saveDialog(category, this);
	}

	@Override
	public void setName(String name) {
		this.title = name;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void setQuest(IQuest quest) {
		if(quest == null)
			this.quest = -1;
		else {
			if(quest.getId() < 0)
				throw new CustomNPCsException("Quest id is lower than 0");
			this.quest = quest.getId();
		}
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public void setCommand(String command) {
		this.command = command;
	}
	
}
