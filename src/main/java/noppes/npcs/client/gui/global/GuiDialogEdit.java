package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.SubGuiMailmanSendSetup;
import noppes.npcs.client.gui.SubGuiNpcAvailability;
import noppes.npcs.client.gui.SubGuiNpcCommand;
import noppes.npcs.client.gui.SubGuiNpcFactionOptions;
import noppes.npcs.shared.client.gui.GuiTextAreaScreen;
import noppes.npcs.client.gui.select.GuiQuestSelection;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketDialogSave;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiDialogEdit extends GuiBasic implements ITextfieldListener {
	private final Dialog dialog;
	
    public GuiDialogEdit(Dialog dialog){
    	this.dialog = dialog;
		setBackground("menubg.png");
		imageWidth = 386;
		imageHeight = 226;
    }

    @Override
    public void init(){
        super.init();

		addLabel(new GuiLabel(1,"gui.title", guiLeft + 4, guiTop + 8));
		addTextField(new GuiTextFieldNop(1, this, guiLeft + 46 , guiTop + 3, 220, 20, dialog.title));

		addLabel(new GuiLabel(0,"ID", guiLeft + 268, guiTop + 4));
		addLabel(new GuiLabel(2,	dialog.id + "", guiLeft + 268, guiTop + 14));
		
		addLabel(new GuiLabel(3, "dialog.dialogtext", guiLeft + 4, guiTop + 30));
    	addButton(new GuiButtonNop(this, 3, guiLeft + 120, guiTop + 25, 50, 20, "selectServer.edit"));
    	
		addLabel(new GuiLabel(4, "availability.options", guiLeft + 4, guiTop + 51));
		addButton(new GuiButtonNop(this, 4, guiLeft + 120, guiTop + 46, 50, 20, "selectServer.edit"));
    	
		addLabel(new GuiLabel(5, "faction.options", guiLeft + 4, guiTop + 72));
		addButton(new GuiButtonNop(this, 5, guiLeft + 120, guiTop + 67, 50, 20, "selectServer.edit"));

		addLabel(new GuiLabel(6, "dialog.options", guiLeft + 4, guiTop + 93));
		addButton(new GuiButtonNop(this, 6, guiLeft + 120, guiTop + 89, 50, 20, "selectServer.edit"));

		addButton(new GuiButtonNop(this, 7, guiLeft + 4, guiTop + 114, 144, 20, "availability.selectquest"));
		addButton(new GuiButtonNop(this, 8, guiLeft + 150, guiTop + 114, 20, 20, "X"));
		if(dialog.hasQuest()) {
			getButton(7).setDisplayText(dialog.getQuest().title);
		}

		addLabel(new GuiLabel(9, "gui.selectSound", guiLeft + 4, guiTop + 138));
		addTextField(new GuiTextFieldNop(2, this, guiLeft + 4, guiTop + 148, 264, 20, dialog.sound));
		addButton(new GuiButtonNop(this, 9, guiLeft + 270, guiTop + 148, 60, 20, "mco.template.button.select"));

		addButton(new GuiButtonNop(this, 13, guiLeft + 4, guiTop + 172, 164, 20, "mailbox.setup"));
		addButton(new GuiButtonNop(this, 14, guiLeft + 170, guiTop + 172, 20, 20, "X"));
		if(!dialog.mail.subject.isEmpty())
			getButton(13).setDisplayText(dialog.mail.subject);

        int y = guiTop + 4;
		addButton(new GuiButtonNop(this, 10, guiLeft + 330, y += 22, 50, 20, "selectServer.edit"));
		addLabel(new GuiLabel(10, "advMode.command", guiLeft + 214, y + 5));

		addButton(new GuiButtonYesNo(this, 11, guiLeft + 330, y += 22, dialog.hideNPC));
		addLabel(new GuiLabel(11, "dialog.hideNPC", guiLeft + 214, y + 5));

		addButton(new GuiButtonYesNo(this, 12, guiLeft + 330, y += 22, dialog.showWheel));
		addLabel(new GuiLabel(12, "dialog.showWheel", guiLeft + 214, y + 5));
		
		addButton(new GuiButtonYesNo(this, 15, guiLeft + 330, y += 22, dialog.disableEsc));
		addLabel(new GuiLabel(15, "dialog.disableEsc", guiLeft + 214, y + 5));

    	addButton(new GuiButtonNop(this, 66, guiLeft + 362, guiTop + 4, 20, 20, "X"));
    }

	@Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
		GuiButtonNop button = guibutton;
        if(id == 3){
        	setSubGui(new GuiTextAreaScreen(dialog.text));
        }
        if(id == 4){
        	setSubGui(new SubGuiNpcAvailability(dialog.availability));
        }
        if(id == 5){
        	setSubGui(new SubGuiNpcFactionOptions(dialog.factionOptions));
        }
        if(id == 6){
        	setSubGui(new SubGuiNpcDialogOptions(dialog));
        }
        if(id == 7){
        	setSubGui(new GuiQuestSelection(dialog.quest));
        }
        if(id == 8){
        	dialog.quest = -1;
        	init();
        }
        if(id == 9){
        	setSubGui(new GuiSoundSelection(getTextField(2).getValue()));
        }
    	if(id == 10){
    		setSubGui(new SubGuiNpcCommand(dialog.command));
    	}
    	if(id == 11){
    		dialog.hideNPC = button.getValue() == 1;
    	}
    	if(id == 12){
    		dialog.showWheel = button.getValue() == 1;
    	}
    	if(id == 15){
    		dialog.disableEsc = button.getValue() == 1;
    	}
    	if(id == 13){
			setSubGui(new SubGuiMailmanSendSetup(dialog.mail));
    	}
        if(id == 14){
        	dialog.mail = new PlayerMail();
        	init();
        }
        if(id == 66){
        	close();
        }
    }
	
	@Override
	public void unFocused(GuiTextFieldNop guiNpcTextField) {
		if(guiNpcTextField.id == 1) {
	    	dialog.title = guiNpcTextField.getValue();
			while(DialogController.instance.containsDialogName(dialog.category, dialog)) {
				dialog.title += "_";
			}
		}
		if(guiNpcTextField.id == 2) {
			dialog.sound = guiNpcTextField.getValue();
		}
		
	}

	@Override
	public void subGuiClosed(Screen subgui){
		if(subgui instanceof GuiTextAreaScreen){
			GuiTextAreaScreen gui = (GuiTextAreaScreen) subgui;
			dialog.text = gui.text;
		}
		if(subgui instanceof SubGuiNpcDialogOption){
        	setSubGui(new SubGuiNpcDialogOptions(dialog));
		}
		if(subgui instanceof SubGuiNpcCommand){
			dialog.command = ((SubGuiNpcCommand) subgui).command;
		}
		if(subgui instanceof GuiQuestSelection){
			GuiQuestSelection gqs = (GuiQuestSelection) subgui;
			if(gqs.selectedQuest != null) {
				dialog.quest = gqs.selectedQuest.id;
				init();
			}
		}
		if(subgui instanceof GuiSoundSelection) {
			GuiSoundSelection gss = (GuiSoundSelection) subgui;
			if(gss.selectedResource != null) {
				getTextField(2).setValue(gss.selectedResource.toString());
				unFocused(getTextField(2));
			}
		}
	}

	@Override
	public void save() {
    	GuiTextFieldNop.unfocus();
		Packets.sendServer(new SPacketDialogSave(dialog.category.id, dialog.save(new CompoundNBT())));
	}

}
