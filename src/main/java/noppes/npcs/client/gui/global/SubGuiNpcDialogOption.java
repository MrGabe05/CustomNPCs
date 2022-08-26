package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.select.GuiDialogSelection;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcDialogOption extends GuiBasic implements ITextfieldListener {
	private DialogOption option;
	public static int LastColor = 0xe0e0e0;
	
    public SubGuiNpcDialogOption(DialogOption option){
    	this.option = option;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

    @Override
    public void init(){
        super.init();
        this.addLabel(new GuiLabel(66, "dialog.editoption", guiLeft, guiTop + 4, imageWidth, 0));

        this.addLabel(new GuiLabel(0, "gui.title", guiLeft + 4, guiTop + 20));
        this.addTextField(new GuiTextFieldNop(0, this, guiLeft + 40, guiTop + 15, 196, 20, option.title));
        
        String color = Integer.toHexString(option.optionColor);
    	while(color.length() < 6)
    		color = 0 + color;
        addLabel(new GuiLabel(2, "gui.color", guiLeft + 4, guiTop + 45));
    	this.addButton(new GuiButtonNop(this, 2, guiLeft + 62, guiTop + 40, 92, 20, color));
    	this.getButton(2).setFGColor(option.optionColor);
    	
        this.addLabel(new GuiLabel(1, "dialog.optiontype", guiLeft + 4, guiTop + 67));
    	this.addButton(new GuiButtonNop(this, 1, guiLeft + 62, guiTop + 62, 92, 20, new String[]{"gui.close","dialog.dialog","gui.disabled","menu.role","block.minecraft.command_block"},option.optionType));

    	if(option.optionType == OptionType.DIALOG_OPTION){
    		this.addButton(new GuiButtonNop(this, 3, guiLeft + 4, guiTop + 84, "availability.selectdialog"));
    		if(option.dialogId >= 0) {
    			Dialog dialog = DialogController.instance.dialogs.get(option.dialogId);
    			if(dialog != null) {
    				getButton(3).setDisplayText(dialog.title);
    			}
    		}
    	}
    	if(option.optionType == OptionType.COMMAND_BLOCK){
	    	this.addTextField(new GuiTextFieldNop(4, this, guiLeft + 4,  guiTop + 84, 248, 20, option.command));
	    	this.getTextField(4).setMaxLength(32767);

	    	this.addLabel(new GuiLabel(4, "advMode.command", guiLeft + 4, guiTop + 110));
	    	this.addLabel(new GuiLabel(5, "advMode.nearestPlayer", guiLeft + 4, guiTop + 125));
	    	this.addLabel(new GuiLabel(6, "advMode.randomPlayer", guiLeft + 4, guiTop + 140));
	    	this.addLabel(new GuiLabel(7, "advMode.allPlayers", guiLeft + 4, guiTop + 155));
	    	this.addLabel(new GuiLabel(8, "dialog.commandoptionplayer", guiLeft + 4, guiTop + 170));
    	}

    	this.addButton(new GuiButtonNop(this, 66, guiLeft + 82, guiTop + 190,98, 20, "gui.done"));
    	
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
    	GuiButtonNop button = (GuiButtonNop) guibutton;

    	if(button.id == 1){
			option.optionType = button.getValue();
			init();
    	}
    	if(button.id == 2){
        	setSubGui(new SubGuiColorSelector(option.optionColor));
    	}
    	if(button.id == 3){
    		setSubGui(new GuiDialogSelection(option.dialogId));
    	}
        if(button.id == 66)
        {
        	close();
        }
    }

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 0){
			if(textfield.isEmpty())
				textfield.setValue(option.title);
			else{
				option.title = textfield.getValue();
			}
		}
		if(textfield.id == 4){
			option.command = textfield.getValue();
		}
	}

	@Override
	public void subGuiClosed(Screen subgui) {
		if(subgui instanceof SubGuiColorSelector) {
			LastColor = option.optionColor = ((SubGuiColorSelector)subgui).color;
		}
		if(subgui instanceof GuiDialogSelection) {
			Dialog dialog = ((GuiDialogSelection)subgui).selectedDialog;
			if(dialog != null) {
				option.dialogId = dialog.id;
			}
		}

    	init();
	}

}
