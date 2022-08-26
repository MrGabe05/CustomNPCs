package noppes.npcs.client.gui.roles;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcConversationLine extends GuiBasic implements ITextfieldListener {
	public String line;
	public String sound;
	private GuiSoundSelection gui;
	
    public SubGuiNpcConversationLine(String line, String sound){
    	this.line = line;
    	this.sound = sound;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

    @Override
    public void init(){
        super.init();
        
        addLabel(new GuiLabel(0, "Line", guiLeft + 4, guiTop+ 10));
        addTextField(new GuiTextFieldNop(0, this,  guiLeft + 4, guiTop + 22, 200, 20, line));

        addButton(new GuiButtonNop(this, 1, guiLeft + 4, guiTop + 55, 90, 20, "Select Sound"));
        addButton(new GuiButtonNop(this, 2, guiLeft + 96, guiTop + 55, 20, 20, "X"));
        addLabel(new GuiLabel(1, sound, guiLeft + 4, guiTop + 81));
        
    	addButton(new GuiButtonNop(this, 66, guiLeft + 162, guiTop + 192, 90, 20, "gui.done"));
    }

    @Override
	public void unFocused(GuiTextFieldNop textfield) {
		line = textfield.getValue();
	}

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
        if(id == 1){
        	setSubGui(new GuiSoundSelection(sound));
        }
        if(id == 2){
        	sound = "";
        	init();
        }
        if(id == 66){
        	close();
        }
    }

	@Override
	public void subGuiClosed(Screen subgui) {
		GuiSoundSelection gss = (GuiSoundSelection) subgui;
		if(gss.selectedResource != null) {
			sound = gss.selectedResource.toString();
		}		
	}

}
