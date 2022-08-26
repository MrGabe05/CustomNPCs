package noppes.npcs.client.gui.questtypes;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.quests.QuestManual;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import java.util.TreeMap;

public class GuiNpcQuestTypeManual extends GuiNPCInterface implements ITextfieldListener
{
	private Screen parent;
	
	private QuestManual quest;
	
	private GuiTextFieldNop lastSelected;

    public GuiNpcQuestTypeManual(EntityNPCInterface npc, Quest q,	Screen parent) {
    	this.npc = npc;
    	this.parent = parent;
    	title = "Quest Manual Setup";
    	
    	quest = (QuestManual) q.questInterface;

		setBackground("menubg.png");
		imageWidth = 356;
		imageHeight = 216;
	}

	@Override
	public void init() {
		super.init();
		int i = 0;
		addLabel(new GuiLabel(0, "You can fill in npc or player names too", guiLeft + 4, guiTop + 50));
		for (String name : quest.manuals.keySet()) {
			this.addTextField(new GuiTextFieldNop(i, this,  guiLeft + 4, guiTop + 70 + i * 22, 180, 20, name));
			this.addTextField(new GuiTextFieldNop(i + 3, this,  guiLeft + 186, guiTop + 70 + i * 22, 24, 20, quest.manuals.get(name) + ""));
			this.getTextField(i+3).numbersOnly = true;
			this.getTextField(i+3).setMinMaxDefault(1, Integer.MAX_VALUE, 1);
			i++;
		}
		
		for(;i < 3; i++){
			this.addTextField(new GuiTextFieldNop(i, this,  guiLeft + 4, guiTop + 70 + i * 22, 180, 20, ""));
			this.addTextField(new GuiTextFieldNop(i + 3, this,  guiLeft + 186, guiTop + 70 + i * 22, 24, 20, "1"));
			this.getTextField(i+3).numbersOnly = true;
			this.getTextField(i+3).setMinMaxDefault(1, Integer.MAX_VALUE, 1);
		} 
		this.addButton(new GuiButtonNop(this, 0, guiLeft + 4, guiTop + 140, 98, 20, "gui.back"));

	}

	@Override
	public void buttonEvent(GuiButtonNop guibutton) {

		if (guibutton.id == 0) {
			close();
		}
	}

	@Override
	public void save() {
	}

	@Override
	public void unFocused(GuiTextFieldNop guiNpcTextField) {
		if(guiNpcTextField.id < 3)
			lastSelected = guiNpcTextField;

		saveTargets();
	}
	
	private void saveTargets(){
		TreeMap<String,Integer> map = new TreeMap<String,Integer>(); 
		for(int i = 0; i< 3; i++){
			String name = getTextField(i).getValue();
			if(name.isEmpty())
				continue;
			map.put(name, getTextField(i+3).getInteger());
		}
		quest.manuals = map;
	}
}
