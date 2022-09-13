package noppes.npcs.client.gui.questtypes;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.quests.QuestLocation;

public class GuiNpcQuestTypeLocation extends GuiNPCInterface implements ITextfieldListener
{
	private final Screen parent;
	
	private final QuestLocation quest;

    public GuiNpcQuestTypeLocation(EntityNPCInterface npc, Quest q,
			Screen parent) {
    	this.npc = npc;
    	this.parent = parent;
    	title = "Quest Location Setup";
    	
    	quest = (QuestLocation) q.questInterface;

		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
	}

	@Override
	public void init() {
		super.init();

		addLabel(new GuiLabel(0, "Fill in the name of your Location Quest Block", guiLeft + 4, guiTop + 50));
		this.addTextField(new GuiTextFieldNop(0, this,  guiLeft + 4, guiTop + 70, 180, 20, quest.location));
		this.addTextField(new GuiTextFieldNop(1, this,  guiLeft + 4, guiTop + 92, 180, 20, quest.location2));
		this.addTextField(new GuiTextFieldNop(2, this,  guiLeft + 4, guiTop + 114, 180, 20, quest.location3));
		this.addButton(new GuiButtonNop(this, 0, guiLeft + 150, guiTop + 190, 98, 20, "gui.back"));
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
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 0)
			quest.location = textfield.getValue();
		if(textfield.id == 1)
			quest.location2 = textfield.getValue();
		if(textfield.id == 2)
			quest.location3 = textfield.getValue();
	}

}
