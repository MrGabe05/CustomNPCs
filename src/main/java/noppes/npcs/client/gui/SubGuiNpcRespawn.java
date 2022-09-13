package noppes.npcs.client.gui;

import noppes.npcs.entity.data.DataStats;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcRespawn extends GuiBasic implements ITextfieldListener {
	private final DataStats stats;
    public SubGuiNpcRespawn(DataStats stats){
    	this.stats = stats;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

    @Override
    public void init(){
        super.init();
    	addLabel(new GuiLabel(0,"stats.respawn", guiLeft + 5, guiTop + 35));
    	addButton(new GuiButtonBiDirectional(this,0,guiLeft + 122, guiTop + 30, 80, 20, new String[]{"gui.yes","gui.day","gui.night","gui.no","stats.naturally"} ,stats.spawnCycle));
    	if(stats.respawnTime > 0){
    		addLabel(new GuiLabel(3,"gui.time", guiLeft + 5, guiTop + 57));
    		addTextField(new GuiTextFieldNop(2,this, guiLeft + 122, guiTop + 53, 50, 18, stats.respawnTime + ""));
    		getTextField(2).numbersOnly = true;
    		getTextField(2).setMinMaxDefault(1, Integer.MAX_VALUE, 20);
    		
    		addLabel(new GuiLabel(4,"stats.deadbody", guiLeft + 4, guiTop + 79));
    		addButton(new GuiButtonNop(this, 4,guiLeft + 122, guiTop + 74, 60, 20, new String[]{"gui.no","gui.yes"} ,stats.hideKilledBody?1:0));
    	}
    	
    	addButton(new GuiButtonNop(this, 66, guiLeft + 82, guiTop + 190,98, 20, "gui.done"));
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
		GuiButtonNop button = guibutton;
		if(button.id == 0){
			stats.spawnCycle = button.getValue();
			if(stats.spawnCycle == 3 || stats.spawnCycle == 4)
				stats.respawnTime = 0;
			else
				stats.respawnTime = 20;
			init();
		}
		else if(button.id == 4){
			stats.hideKilledBody = button.getValue() == 1;
		}
        if(id == 66){
        	close();
        }
    }

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 2){
			stats.respawnTime = textfield.getInteger();
		}
	}

}
