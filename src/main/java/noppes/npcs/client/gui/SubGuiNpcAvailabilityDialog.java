package noppes.npcs.client.gui;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.select.GuiDialogSelection;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.constants.EnumAvailabilityDialog;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Dialog;

public class SubGuiNpcAvailabilityDialog extends GuiBasic{
	private final Availability availabitily;
	private int slot = 0;
	
    public SubGuiNpcAvailabilityDialog(Availability availabitily){
    	this.availabitily = availabitily;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

    @Override
    public void init(){
        super.init();
        addLabel(new GuiLabel(1,"availability.available", guiLeft, guiTop + 4, imageWidth, 0));

        int y = guiTop + 12;
        this.addButton(new GuiButtonNop(this, 0, guiLeft + 4, y, 50, 20, new String[]{"availability.always","availability.after","availability.before"},availabitily.dialogAvailable.ordinal()));
    	this.addButton(new GuiButtonNop(this, 10, guiLeft + 56, y, 172, 20, "availability.selectdialog"));
    	getButton(10).setEnabled(availabitily.dialogAvailable != EnumAvailabilityDialog.Always);
    	this.addButton(new GuiButtonNop(this, 20, guiLeft + 230, y,20, 20, "X"));

    	y += 23;
        this.addButton(new GuiButtonNop(this, 1, guiLeft + 4, y, 50, 20, new String[]{"availability.always","availability.after","availability.before"},availabitily.dialog2Available.ordinal()));
    	this.addButton(new GuiButtonNop(this, 11, guiLeft + 56, y, 172, 20, "availability.selectdialog"));
    	getButton(11).setEnabled(availabitily.dialog2Available != EnumAvailabilityDialog.Always);
    	this.addButton(new GuiButtonNop(this, 21, guiLeft + 230, y,20, 20, "X"));

    	y += 23;
        this.addButton(new GuiButtonNop(this, 2, guiLeft + 4, y, 50, 20, new String[]{"availability.always","availability.after","availability.before"},availabitily.dialog3Available.ordinal()));
    	this.addButton(new GuiButtonNop(this, 12, guiLeft + 56, y, 172, 20, "availability.selectdialog"));
    	getButton(12).setEnabled(availabitily.dialog3Available != EnumAvailabilityDialog.Always);
    	this.addButton(new GuiButtonNop(this, 22, guiLeft + 230, y,20, 20, "X"));

    	y += 23;
        this.addButton(new GuiButtonNop(this, 3, guiLeft + 4, y, 50, 20, new String[]{"availability.always","availability.after","availability.before"},availabitily.dialog4Available.ordinal()));
    	this.addButton(new GuiButtonNop(this, 13, guiLeft + 56, y, 172, 20, "availability.selectdialog"));
    	getButton(13).setEnabled(availabitily.dialog4Available != EnumAvailabilityDialog.Always);
    	this.addButton(new GuiButtonNop(this, 23, guiLeft + 230, y,20, 20, "X"));
        
    	
    	this.addButton(new GuiButtonNop(this, 66, guiLeft + 82, guiTop + 192,98, 20, "gui.done"));
    	
    	updateGuiButtons();
    }


    private void updateGuiButtons() {
		getButton(10).setDisplayText("availability.selectdialog");
		getButton(11).setDisplayText("availability.selectdialog");
		getButton(12).setDisplayText("availability.selectdialog");
		getButton(13).setDisplayText("availability.selectdialog");
		
		if(availabitily.dialogId >= 0){
			Dialog dialog = DialogController.instance.dialogs.get(availabitily.dialogId);
			if(dialog != null) {
				getButton(10).setDisplayText(dialog.title);
			}
		}
		if(availabitily.dialog2Id >= 0){
			Dialog dialog = DialogController.instance.dialogs.get(availabitily.dialog2Id);
			if(dialog != null) {
				getButton(11).setDisplayText(dialog.title);
			}
		}
		if(availabitily.dialog3Id >= 0){
			Dialog dialog = DialogController.instance.dialogs.get(availabitily.dialog3Id);
			if(dialog != null) {
				getButton(12).setDisplayText(dialog.title);
			}
		}
		if(availabitily.dialog4Id >= 0){
			Dialog dialog = DialogController.instance.dialogs.get(availabitily.dialog4Id);
			if(dialog != null) {
				getButton(13).setDisplayText(dialog.title);
			}
		}
	}

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
    	GuiButtonNop button = guibutton;

        if(button.id == 0){
        	availabitily.dialogAvailable = EnumAvailabilityDialog.values()[button.getValue()];
        	if(availabitily.dialogAvailable == EnumAvailabilityDialog.Always)
        		availabitily.dialogId = -1;
        	init();
        }
        if(button.id == 1){
        	availabitily.dialog2Available = EnumAvailabilityDialog.values()[button.getValue()];
        	if(availabitily.dialog2Available == EnumAvailabilityDialog.Always)
        		availabitily.dialog2Id = -1;
        	init();
        }
        if(button.id == 2){
        	availabitily.dialog3Available = EnumAvailabilityDialog.values()[button.getValue()];
        	if(availabitily.dialog3Available == EnumAvailabilityDialog.Always)
        		availabitily.dialog3Id = -1;
        	init();
        }
        if(button.id == 3){
        	availabitily.dialog4Available = EnumAvailabilityDialog.values()[button.getValue()];
        	if(availabitily.dialog4Available == EnumAvailabilityDialog.Always)
        		availabitily.dialog4Id = -1;
        	init();
        }
        if(button.id == 10){
        	slot = 1;
        	setSubGui(new GuiDialogSelection(availabitily.dialogId));
        }
        if(button.id == 11){
        	slot = 2;
        	setSubGui(new GuiDialogSelection(availabitily.dialog2Id));
        }
        if(button.id == 12){
        	slot = 3;
        	setSubGui(new GuiDialogSelection(availabitily.dialog3Id));
        }
        if(button.id == 13){
        	slot = 4;
        	setSubGui(new GuiDialogSelection(availabitily.dialog4Id));
        }
        if(button.id == 20){
        	availabitily.dialogId = -1;
    		getButton(10).setDisplayText("availability.selectdialog");
        }
        if(button.id == 21){
        	availabitily.dialog2Id = -1;
    		getButton(11).setDisplayText("availability.selectdialog");
        }
        if(button.id == 22){
        	availabitily.dialog3Id = -1;
    		getButton(12).setDisplayText("availability.selectdialog");
        }
        if(button.id == 23){
        	availabitily.dialog4Id = -1;
    		getButton(13).setDisplayText("availability.selectdialog");
        }
        
        if(button.id == 66){
    		close();
        }
    }

	@Override
	public void subGuiClosed(Screen subgui){
		GuiDialogSelection selector = (GuiDialogSelection) subgui;
		if(selector.selectedDialog == null) {
			return;
		}
		if(slot == 1)
			availabitily.dialogId = selector.selectedDialog.id;
		if(slot == 2)
			availabitily.dialog2Id = selector.selectedDialog.id;
		if(slot == 3)
			availabitily.dialog3Id = selector.selectedDialog.id;
		if(slot == 4)
			availabitily.dialog4Id = selector.selectedDialog.id;
		init();
	}

}
