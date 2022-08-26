package noppes.npcs.client.gui;

import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcCommand extends GuiBasic implements ITextfieldListener {
	public String command;
	
    public SubGuiNpcCommand(String command) {
    	this.command = command;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

	@Override
    public void init() {
        super.init();
    	this.addTextField(new GuiTextFieldNop(4, this, guiLeft + 4,  guiTop + 84, 248, 20, command));
    	this.getTextField(4).setMaxLength(32767);

    	this.addLabel(new GuiLabel(4, "advMode.command", guiLeft + 4, guiTop + 110));
    	this.addLabel(new GuiLabel(5, "advMode.nearestPlayer", guiLeft + 4, guiTop + 125));
    	this.addLabel(new GuiLabel(6, "advMode.randomPlayer", guiLeft + 4, guiTop + 140));
    	this.addLabel(new GuiLabel(7, "advMode.allPlayers", guiLeft + 4, guiTop + 155));
    	this.addLabel(new GuiLabel(8, "dialog.commandoptionplayer", guiLeft + 4, guiTop + 170));

    	this.addButton(new GuiButtonNop(this, 66, guiLeft + 82, guiTop + 190,98, 20, "gui.done"));
    	
    }

	@Override
	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
        if(id == 66){
			onClose();
        }
    }

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 4){
			command = textfield.getValue();
		}
	}

}
