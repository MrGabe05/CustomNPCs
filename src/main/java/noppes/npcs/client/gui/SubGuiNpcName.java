package noppes.npcs.client.gui;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.entity.data.DataDisplay;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpRandomNameSet;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcName extends GuiBasic implements ITextfieldListener, IGuiData {
	private DataDisplay display;
	public SubGuiNpcName(DataDisplay display){
    	this.display = display;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

    @Override
    public void init(){
        super.init();
        int y = guiTop + 4;
    	addButton(new GuiButtonNop(this, 66, guiLeft + imageWidth - 24, y, 20, 20, "X"));
    	
        addTextField(new GuiTextFieldNop(0,this,  guiLeft + 4, y += 50, 226, 20, display.getName()));
    	this.addButton(new GuiButtonBiDirectional(this,1, guiLeft + 4, y += 22 , 200, 20, new String[] {"markov.roman.name", "markov.japanese.name", "markov.slavic.name", "markov.welsh.name", "markov.sami.name", "markov.oldNorse.name", "markov.ancientGreek.name", "markov.aztec.name", "markov.classicCNPCs.name", "markov.spanish.name"}, display.getMarkovGeneratorId()));
    	
    	this.addButton(new GuiButtonBiDirectional(this,2, guiLeft + 64, y += 22 , 120, 20, new String[] {"markov.gender.either", "markov.gender.male", "markov.gender.female"}, display.getMarkovGender()));
    	addLabel(new GuiLabel(2,"markov.gender.name", guiLeft + 5, y + 5));
    	
    	addButton(new GuiButtonNop(this, 3, guiLeft + 4, y += 42, 70, 20, "markov.generate"));
    }

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 0){
			if(!textfield.isEmpty())
				display.setName(textfield.getValue());
			else
				textfield.setValue(display.getName());
		}
	}
    
	@Override
	public void buttonEvent(GuiButtonNop guibutton){
		GuiButtonNop button = (GuiButtonNop) guibutton;

		if(button.id == 1) {
			display.setMarkovGeneratorId(button.getValue());
		}
		if(button.id == 2) {
			display.setMarkovGender(button.getValue());
		}
		if(button.id == 3) {
			Packets.sendServer(new SPacketNpRandomNameSet(display.getMarkovGeneratorId(), display.getMarkovGender()));
		}
        if(button.id == 66){
        	close();
        }
    }

	@Override
	public void setGuiData(CompoundNBT compound) {
		display.readToNBT(compound);
		init();
	}
}
