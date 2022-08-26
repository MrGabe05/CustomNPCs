package noppes.npcs.client.gui;

import net.minecraft.client.resources.I18n;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcFactionPoints extends GuiBasic implements ITextfieldListener {
	private Faction faction;
    public SubGuiNpcFactionPoints(Faction faction){
    	this.faction = faction;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

    @Override
    public void init(){
        super.init();
    	
    	addLabel(new GuiLabel(2,"faction.default", guiLeft + 4, guiTop + 33));
    	
    	this.addTextField(new GuiTextFieldNop(2, this, guiLeft + 8 + font.width(getLabel(2).getMessage()), guiTop + 28, 70, 20, faction.defaultPoints + ""));
    	getTextField(2).setMaxLength(6);
    	getTextField(2).numbersOnly = true;
    	
    	String title = I18n.get("faction.unfriendly") + "<->" + I18n.get("faction.neutral");
    	addLabel(new GuiLabel(3, title, guiLeft + 4, guiTop + 80));
    	addTextField(new GuiTextFieldNop(3, this, guiLeft + 8 + font.width(title), guiTop + 75, 70, 20, faction.neutralPoints + ""));

    	title = I18n.get("faction.neutral") + "<->" + I18n.get("faction.friendly");
    	addLabel(new GuiLabel(4, title, guiLeft + 4, guiTop + 105));
    	addTextField(new GuiTextFieldNop(4, this, guiLeft +  8 + font.width(title), guiTop + 100, 70, 20, faction.friendlyPoints + ""));

    	getTextField(3).numbersOnly = true;
    	getTextField(4).numbersOnly = true;
    	
    	if(getTextField(3).x > getTextField(4).x)
    		getTextField(4).x = getTextField(3).x;
    	else
    		getTextField(3).x = getTextField(4).x;
    	
    	addButton(new GuiButtonNop(this, 66, guiLeft + 20, guiTop + 192, 90, 20, "gui.done"));
    }

    @Override
	public void unFocused(GuiTextFieldNop textfield) {
		 if(textfield.id == 2) {
			faction.defaultPoints = textfield.getInteger();
		}else if(textfield.id == 3) {
			faction.neutralPoints = textfield.getInteger();
		}else if(textfield.id == 4) {
			faction.friendlyPoints = textfield.getInteger();
		}
	}

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
        if(id == 66){
        	close();
        }
    }

}
