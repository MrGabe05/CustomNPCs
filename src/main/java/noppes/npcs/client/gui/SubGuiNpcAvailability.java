package noppes.npcs.client.gui;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumAvailabilityFactionType;
import noppes.npcs.constants.EnumDayTime;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketFactionGet;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcAvailability extends GuiNPCInterface implements ITextfieldListener, GuiSelectionListener, IGuiData
{
	private Availability availabitily;
	private int slot = 0;
	
    public SubGuiNpcAvailability(Availability availabitily) {
    	this.availabitily = availabitily;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

    @Override
    public void init() {
        super.init();
        addLabel(new GuiLabel(1,"availability.available", guiLeft, guiTop + 4, imageWidth, 0));

        this.addButton(new GuiButtonNop(this, 0, guiLeft + 34, guiTop + 12, 180, 20, "availability.selectdialog"));
        this.addButton(new GuiButtonNop(this, 1, guiLeft + 34, guiTop + 35, 180, 20, "availability.selectquest"));
        this.addButton(new GuiButtonNop(this, 2, guiLeft + 34, guiTop + 58, 180, 20, "availability.selectscoreboard"));

        
        this.addButton(new GuiButtonNop(this, 20, guiLeft + 4, guiTop + 104, 50, 20, new String[]{"availability.always","availability.is","availability.isnot"},availabitily.factionAvailable.ordinal()));
        this.addButton(new GuiButtonNop(this, 22, guiLeft + 56, guiTop + 104, 60, 20, new String[]{"faction.friendly","faction.neutral","faction.unfriendly"},availabitily.factionStance.ordinal()));
    	this.addButton(new GuiButtonNop(this, 21, guiLeft + 118, guiTop + 104, 110, 20, "availability.selectfaction"));
    	getButton(21).setEnabled(availabitily.factionAvailable != EnumAvailabilityFactionType.Always);
    	getButton(22).setEnabled(availabitily.factionAvailable != EnumAvailabilityFactionType.Always);
    	this.addButton(new GuiButtonNop(this, 23, guiLeft + 230, guiTop + 104,20, 20, "X"));
    	
        this.addButton(new GuiButtonNop(this, 24, guiLeft + 4, guiTop + 126, 50, 20, new String[]{"availability.always","availability.is","availability.isnot"},availabitily.faction2Available.ordinal()));
        this.addButton(new GuiButtonNop(this, 27, guiLeft + 56, guiTop + 126, 60, 20, new String[]{"faction.friendly","faction.neutral","faction.unfriendly"},availabitily.faction2Stance.ordinal()));
    	this.addButton(new GuiButtonNop(this, 25, guiLeft + 118, guiTop + 126, 110, 20, "availability.selectfaction"));
    	getButton(25).setEnabled(availabitily.faction2Available != EnumAvailabilityFactionType.Always);
    	getButton(27).setEnabled(availabitily.faction2Available != EnumAvailabilityFactionType.Always);
    	this.addButton(new GuiButtonNop(this, 26, guiLeft + 230, guiTop + 126,20, 20, "X"));

        addLabel(new GuiLabel(50,"availability.daytime", guiLeft + 4 , guiTop + 153));
    	this.addButton(new GuiButtonNop(this, 50, guiLeft + 50, guiTop + 148,150, 20, new String[]{"availability.wholeday","availability.night","availability.day"},availabitily.daytime.ordinal()));

        addLabel(new GuiLabel(51,"availability.minlevel", guiLeft + 4 , guiTop + 175));
    	this.addTextField(new GuiTextFieldNop(51, this,  guiLeft + 50, guiTop + 170,90, 20, availabitily.minPlayerLevel + ""));
    	this.getTextField(51).numbersOnly = true;
    	this.getTextField(51).setMinMaxDefault(0, Integer.MAX_VALUE, 0);
    	
    	this.addButton(new GuiButtonNop(this, 66, guiLeft + 82, guiTop + 192,98, 20, "gui.done"));
    	
    	updateGuiButtons();
    }


    private void updateGuiButtons() {
		if(availabitily.factionId >= 0){
			Packets.sendServer(new SPacketFactionGet(availabitily.factionId));
		}
		if(availabitily.faction2Id >= 0){
			Packets.sendServer(new SPacketFactionGet(availabitily.faction2Id));
		}
	}

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
    	GuiButtonNop button = (GuiButtonNop) guibutton;

        if(button.id == 0){
        	setSubGui(new SubGuiNpcAvailabilityDialog(availabitily));
        }
        if(button.id == 1){
        	setSubGui(new SubGuiNpcAvailabilityQuest(availabitily));
        }
        if(button.id == 2){
        	setSubGui(new SubGuiNpcAvailabilityScoreboard(availabitily));
        }

        if(button.id == 20){
        	availabitily.setFactionAvailability(button.getValue());
        	if(availabitily.factionAvailable == EnumAvailabilityFactionType.Always)
        		availabitily.factionId = -1;
        	init();
        }
        if(button.id == 24){
        	availabitily.setFaction2Availability(button.getValue());
        	if(availabitily.faction2Available == EnumAvailabilityFactionType.Always)
        		availabitily.faction2Id = -1;
        	init();
        }
        if(button.id == 21){
        	slot = 1;
        	GuiNPCFactionSelection gui = new GuiNPCFactionSelection(npc, getParent(), availabitily.factionId);
        	gui.listener = this;
        	NoppesUtil.openGUI(player, gui);
        }
        if(button.id == 25){
        	slot = 2;
        	GuiNPCFactionSelection gui = new GuiNPCFactionSelection(npc, getParent(), availabitily.faction2Id);
        	gui.listener = this;
        	NoppesUtil.openGUI(player, gui);
        }
        if(button.id == 22){
        	availabitily.setFactionAvailabilityStance(button.getValue());
        }

        if(button.id == 27){
        	availabitily.setFaction2AvailabilityStance(button.getValue());
        }
        if(button.id == 23){
        	availabitily.factionId = -1;
    		getButton(21).setDisplayText("availability.selectfaction");
        }
        if(button.id == 26){
        	availabitily.faction2Id = -1;
    		getButton(25).setDisplayText("availability.selectfaction");
        }
        if(button.id == 50){
        	availabitily.daytime = EnumDayTime.values()[button.getValue()];
        }
        if(button.id == 66){
    		close();
        }
    }

	@Override
	public void selected(int id, String name) {
		if(slot == 1)
			availabitily.factionId = id;
		if(slot == 2)
			availabitily.faction2Id = id;
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		if(compound.contains("Slot")){
			Faction faction = new Faction();
			faction.readNBT(compound);
			if(availabitily.factionId == faction.id)
				getButton(21).setDisplayText(faction.name);
			if(availabitily.faction2Id == faction.id)
				getButton(25).setDisplayText(faction.name);
		}
	}

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 51)
			availabitily.minPlayerLevel = textfield.getInteger();
	}

}
