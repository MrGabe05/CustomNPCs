package noppes.npcs.client.gui.global;

import noppes.npcs.api.constants.OptionType;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;

public class SubGuiNpcDialogOptions extends GuiBasic {
	private final Dialog dialog;
	
    public SubGuiNpcDialogOptions(Dialog dialog){
    	this.dialog = dialog;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

    @Override
    public void init(){
        super.init();
        this.addLabel(new GuiLabel(66, "dialog.options", guiLeft, guiTop + 4, imageWidth, 0));
        
		for (int i = 0; i < 6; i++) {
			String optionString = "";
			DialogOption option = dialog.options.get(i);
			if(option != null && option.optionType != OptionType.DISABLED)
				optionString += option.title;

			this.addLabel(new GuiLabel(i + 10, i + 1 + ": ", guiLeft + 4, guiTop + 16 + i * 32));
			this.addLabel(new GuiLabel(i, optionString, guiLeft + 14, guiTop + 12 + i * 32));
	    	this.addButton(new GuiButtonNop(this, i, guiLeft + 13,  guiTop + 21 + i * 32, 60, 20, "selectServer.edit"));
			
		}

    	this.addButton(new GuiButtonNop(this, 66, guiLeft + 82, guiTop + 194,98, 20, "gui.done"));
    	
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
		if(id < 6){
			DialogOption option = dialog.options.get(id);
        	if(option == null){
        		dialog.options.put(id, option = new DialogOption());
        		option.optionColor = SubGuiNpcDialogOption.LastColor;
        	}
        	this.setSubGui(new SubGuiNpcDialogOption(option));
        }
        if(id == 66){
        	close();
        }
    }
}
