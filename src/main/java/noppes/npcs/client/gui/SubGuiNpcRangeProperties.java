package noppes.npcs.client.gui;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.entity.data.DataRanged;
import noppes.npcs.entity.data.DataStats;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcRangeProperties extends GuiBasic implements ITextfieldListener {
	private DataRanged ranged;
	private DataStats stats;
	private GuiSoundSelection gui;
	private int soundSelected = -1;
	
    public SubGuiNpcRangeProperties(DataStats stats){
    	this.ranged = stats.ranged;
    	this.stats = stats;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

	@Override
    public void init(){
        super.init();
        int y = guiTop + 4;
        addTextField(new GuiTextFieldNop(1,this, guiLeft + 80, y, 50, 18, ranged.getAccuracy()+""));
        addLabel(new GuiLabel(1,"stats.accuracy", guiLeft + 5, y + 5));
        getTextField(1).numbersOnly = true;
        getTextField(1).setMinMaxDefault(0, 100, 90);
    	addTextField(new GuiTextFieldNop(8, this, guiLeft + 200, y, 50, 18, ranged.getShotCount() + ""));
    	addLabel(new GuiLabel(8, "stats.shotcount", guiLeft + 135, y + 5));
    	getTextField(8).numbersOnly = true;
    	getTextField(8).setMinMaxDefault(1, 10, 1);

        addTextField(new GuiTextFieldNop(2,this, guiLeft + 80, y += 22, 50, 18, ranged.getRange()+""));
        addLabel(new GuiLabel(2,"gui.range", guiLeft + 5, y + 5));
        getTextField(2).numbersOnly = true;
        getTextField(2).setMinMaxDefault(1, 64, 2);
    	
		addTextField(new GuiTextFieldNop(9,this, guiLeft + 200, y, 30, 20, ranged.getMeleeRange() + ""));
		addLabel(new GuiLabel(16, "stats.meleerange", guiLeft + 135, y + 5));
    	getTextField(9).numbersOnly = true;
        getTextField(9).setMinMaxDefault(0, stats.aggroRange, 5);
    	
    	addTextField(new GuiTextFieldNop(3,this, guiLeft + 80, y += 22, 50, 18, ranged.getDelayMin()+""));
        addLabel(new GuiLabel(3,"stats.mindelay", guiLeft + 5, y + 5));
        getTextField(3).numbersOnly = true;
        getTextField(3).setMinMaxDefault(1, 9999, 20);
        addTextField(new GuiTextFieldNop(4,this, guiLeft + 200, y, 50, 18, ranged.getDelayMax()+""));
        addLabel(new GuiLabel(4,"stats.maxdelay", guiLeft + 135, y + 5));
        getTextField(4).numbersOnly = true;
        getTextField(4).setMinMaxDefault(1, 9999, 20);

    	addTextField(new GuiTextFieldNop(6, this, guiLeft + 80, y += 22, 50, 18, ranged.getBurst() + ""));
    	addLabel(new GuiLabel(6, "stats.burstcount", guiLeft + 5, y + 5));
    	getTextField(6).numbersOnly = true;
    	getTextField(6).setMinMaxDefault(1, 100, 20);
		addTextField(new GuiTextFieldNop(5, this, guiLeft + 200, y , 50, 18, ranged.getBurstDelay() + ""));
        addLabel(new GuiLabel(5, "stats.burstspeed", guiLeft + 135, y + 5));
		getTextField(5).numbersOnly = true;
		getTextField(5).setMinMaxDefault(0, 30, 0);
		
    	addTextField(new GuiTextFieldNop(7,this, guiLeft + 80, y += 22, 100, 20, ranged.getSound(0)));
    	addLabel(new GuiLabel(7, "stats.firesound", guiLeft + 5, y + 5));
    	addButton(new GuiButtonNop(this, 7, guiLeft + 187, y, 60, 20, "mco.template.button.select"));
		
    	addTextField(new GuiTextFieldNop(11,this, guiLeft + 80, y += 22, 100, 20, ranged.getSound(1)));
    	addLabel(new GuiLabel(11, "stats.hitsound", guiLeft + 5, y + 5));
    	addButton(new GuiButtonNop(this, 11, guiLeft + 187, y, 60, 20, "mco.template.button.select"));
		
    	addTextField(new GuiTextFieldNop(10,this, guiLeft + 80, y += 22, 100, 20, ranged.getSound(2)));
    	addLabel(new GuiLabel(10, "stats.groundsound", guiLeft + 5, y + 5));
    	addButton(new GuiButtonNop(this, 10, guiLeft + 187, y, 60, 20, "mco.template.button.select"));

    	addButton(new GuiButtonYesNo(this, 9, guiLeft + 100, y += 22, ranged.getHasAimAnimation()));
    	addLabel(new GuiLabel(9, "stats.aimWhileShooting", guiLeft + 5, y + 5));
    	
    	addButton(new GuiButtonNop(this, 13, guiLeft + 100, y += 22, 80, 20, new String[]{"gui.no", "gui.whendistant", "gui.whenhidden"}, ranged.getFireType()));
    	addLabel(new GuiLabel(13, "stats.indirect", guiLeft + 5, y + 5));
    	
    	addButton(new GuiButtonNop(this, 66, guiLeft + 190, guiTop + 190, 60, 20, "gui.done"));
    }

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 1){
			ranged.setAccuracy(textfield.getInteger());
		}
		else if(textfield.id == 2){
			ranged.setRange(textfield.getInteger());
		}
		else if(textfield.id == 3){
			ranged.setDelay(textfield.getInteger(), ranged.getDelayMax());
			init();
		}
		else if(textfield.id == 4){
			ranged.setDelay(ranged.getDelayMin(), textfield.getInteger());
			init();
		}
		else if(textfield.id == 5){
			ranged.setBurstDelay(textfield.getInteger());
		}
		else if(textfield.id == 6){
			ranged.setBurst(textfield.getInteger());
		}
		else if(textfield.id == 7){
			ranged.setSound(0, textfield.getValue());
		}
		else if(textfield.id == 8){
			ranged.setShotCount(textfield.getInteger());
		}
		else if(textfield.id == 9){
			ranged.setMeleeRange(textfield.getInteger());
		}
		else if(textfield.id == 10){
			ranged.setSound(2, textfield.getValue());
		}
		else if(textfield.id == 11){
			ranged.setSound(1, textfield.getValue());
		}
	}

	@Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
        if(id == 7){
			soundSelected = 0;
        	setSubGui(new GuiSoundSelection(getTextField(7).getValue()));
        }
        if(id == 11){
			soundSelected = 1;
        	setSubGui(new GuiSoundSelection(getTextField(11).getValue()));
        }
        if(id == 10){
			soundSelected = 2;
        	setSubGui(new GuiSoundSelection(getTextField(10).getValue()));
        }
        else if(id == 66){
        	close();
        }
		else if(id == 9){
			ranged.setHasAimAnimation(((GuiButtonYesNo)guibutton).getBoolean());
		}
		else if(id == 13){
			ranged.setFireType(((GuiButtonNop)guibutton).getValue());
		}
    }

	@Override
	public void subGuiClosed(Screen subgui) {
		GuiSoundSelection gss = (GuiSoundSelection) subgui;
		if(gss.selectedResource != null) {
			ranged.setSound(soundSelected, gss.selectedResource.toString());
		}		
	}
}
