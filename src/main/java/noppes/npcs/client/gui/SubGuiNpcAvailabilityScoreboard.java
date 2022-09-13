package noppes.npcs.client.gui;

import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.constants.EnumAvailabilityScoreboard;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcAvailabilityScoreboard extends GuiBasic implements ITextfieldListener {
	private final Availability availabitily;
	private final boolean selectFaction = false;
	private final int slot = 0;
	
    public SubGuiNpcAvailabilityScoreboard(Availability availabitily){
    	this.availabitily = availabitily;
		setBackground("menubg.png");
		imageWidth = 316;
		imageHeight = 216;
    }

    @Override
    public void init(){
        super.init();
        addLabel(new GuiLabel(1,"availability.available", guiLeft, guiTop + 4, imageWidth, 0));

        int y = guiTop + 12;        
    	this.addTextField(new GuiTextFieldNop(10, this, guiLeft + 4, y, 140, 20, availabitily.scoreboardObjective));
    	this.addButton(new GuiButtonNop(this, 0, guiLeft + 148, y, 90, 20, new String[]{"availability.smaller", "availability.equals", "availability.bigger"},availabitily.scoreboardType.ordinal()));
    	this.addTextField(new GuiTextFieldNop(20, this, guiLeft + 244, y, 60, 20, availabitily.scoreboardValue + ""));
    	this.getTextField(20).numbersOnly = true;

    	y += 23;
    	this.addTextField(new GuiTextFieldNop(11, this, guiLeft + 4, y, 140, 20, availabitily.scoreboard2Objective));
    	this.addButton(new GuiButtonNop(this, 1, guiLeft + 148, y, 90, 20, new String[]{"availability.smaller", "availability.equals", "availability.bigger"},availabitily.scoreboard2Type.ordinal()));
    	this.addTextField(new GuiTextFieldNop(21, this, guiLeft + 244, y, 60, 20, availabitily.scoreboard2Value + ""));
    	this.getTextField(21).numbersOnly = true;
    	
    	this.addButton(new GuiButtonNop(this, 66, guiLeft + 82, guiTop + 192,98, 20, "gui.done"));
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
    	GuiButtonNop button = guibutton;
        if(guibutton.id == 0){
        	availabitily.scoreboardType = EnumAvailabilityScoreboard.values()[button.getValue()];
        }
        if(guibutton.id == 1){
        	availabitily.scoreboard2Type = EnumAvailabilityScoreboard.values()[button.getValue()];
        }
        if(guibutton.id == 66){
    		close();
        }
    }

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 10) {
			availabitily.scoreboardObjective = textfield.getValue();
		}
		if(textfield.id == 11) {
			availabitily.scoreboard2Objective = textfield.getValue();
		}
		if(textfield.id == 20) {
			availabitily.scoreboardValue = NoppesStringUtils.parseInt(textfield.getValue(), 0);
		}
		if(textfield.id == 21) {
			availabitily.scoreboard2Value = NoppesStringUtils.parseInt(textfield.getValue(), 0);
		}
	}

}
