package noppes.npcs.client.gui.roles;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.SubGuiNpcAvailability;
import noppes.npcs.client.gui.select.GuiQuestSelection;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.roles.JobConversation;
import noppes.npcs.roles.JobConversation.ConversationLine;

public class GuiNpcConversation extends GuiNPCInterface2 implements ITextfieldListener, GuiSelectionListener {
	private final JobConversation job;
	
	private int slot = -1;
	
    public GuiNpcConversation(EntityNPCInterface npc){
    	super(npc);    	
    	job = (JobConversation) npc.job;
    }

	@Override
    public void init(){
    	super.init();

    	addLabel(new GuiLabel(40, "gui.name", guiLeft + 40, guiTop + 4));
    	addLabel(new GuiLabel(41, "gui.name", guiLeft + 240, guiTop + 4));
    	
    	addLabel(new GuiLabel(42, "conversation.delay", guiLeft + 164, guiTop + 4));
    	addLabel(new GuiLabel(43, "conversation.delay", guiLeft + 364, guiTop + 4));

		for (int i = 0; i < 14; i++) {
    		ConversationLine line = job.getLine(i);
			int offset = i >=7 ?200:0;
			this.addLabel(new GuiLabel(i, "" + (i + 1), guiLeft + 5 + offset - (i > 8?6:0), guiTop + 18 + i % 7 * 22));
			this.addTextField(new GuiTextFieldNop(i, this, guiLeft + 13 + offset, guiTop + 13 +  i % 7 * 22, 100, 20, line.npc));
			this.addButton(new GuiButtonNop(this, i, guiLeft + 115 + offset, guiTop + 13 + i % 7 * 22, 46, 20, "conversation.line"));
			
			if(i > 0){
				this.addTextField(new GuiTextFieldNop(i + 14, this, guiLeft + 164 + offset, guiTop + 13 + i % 7 * 22, 30, 20, line.delay + ""));
				this.getTextField(i + 14).numbersOnly = true;
				this.getTextField(i + 14).setMinMaxDefault(5, 1000, 40);
			}
		}
		addLabel(new GuiLabel(50, "conversation.delay", guiLeft + 202, guiTop + 175));
		addTextField(new GuiTextFieldNop(50, this,  guiLeft + 260, guiTop + 170, 40, 20, job.generalDelay + ""));
		getTextField(50).numbersOnly = true;
		getTextField(50).setMinMaxDefault(10, 1000000, 12000);

		addLabel(new GuiLabel(54, "gui.range", guiLeft + 202, guiTop + 196));
		addTextField(new GuiTextFieldNop(54, this,  guiLeft + 260, guiTop + 191, 40, 20, job.range + ""));
		getTextField(54).numbersOnly = true;
		getTextField(54).setMinMaxDefault(4, 60, 20);
		
		addLabel(new GuiLabel(51, "quest.quest", guiLeft + 13, guiTop + 175));
		String title = job.questTitle;
		if(title.isEmpty())
			title = "gui.select";
		addButton(new GuiButtonNop(this, 51, guiLeft + 70, guiTop + 170, 100, 20, title));
		addButton(new GuiButtonNop(this, 52, guiLeft + 171, guiTop + 170, 20, 20, "X"));

		addLabel(new GuiLabel(53, "availability.name", guiLeft + 13, guiTop + 196));
		addButton(new GuiButtonNop(this, 53, guiLeft + 110, guiTop + 191, 60, 20, "selectServer.edit"));

		addButton(new GuiButtonNop(this, 55, guiLeft + 310, guiTop + 181, 96, 20, new String[]{"gui.always", "gui.playernearby"}, job.mode));
    }

	@Override
    public void buttonEvent(GuiButtonNop guibutton){
    	GuiButtonNop button = guibutton;
    	if(button.id >= 0 && button.id < 14){
    		slot = button.id;
    		ConversationLine line = job.getLine(slot);
    		setSubGui(new SubGuiNpcConversationLine(line.getText(), line.getSound()));
    	}
    	if(button.id == 51){
    		setSubGui(new GuiQuestSelection(job.quest));
    	}
    	if(button.id == 52){
    		job.quest = -1;
    		job.questTitle = "";
    		init();
    	}
    	if(button.id == 53){
			setSubGui(new SubGuiNpcAvailability(job.availability));
    	}
    	if(button.id == 55){
    		job.mode = button.getValue();
    	}
    }

	@Override
	public void selected(int ob, String name) {
		job.quest = ob;
		job.questTitle = name;
		init();
	}

    @Override
	public void subGuiClosed(Screen gui) {
		if(gui instanceof SubGuiNpcConversationLine){
	    	SubGuiNpcConversationLine sub = (SubGuiNpcConversationLine) gui;
			ConversationLine line = job.getLine(slot);
			line.setText(sub.line);
			line.setSound(sub.sound);
		}
	}
	
    @Override
	public void save() {
    	Packets.sendServer(new SPacketNpcJobSave(job.save(new CompoundNBT())));
	}

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
    	if(textfield.id >= 0 && textfield.id < 14){
			ConversationLine line = job.getLine(textfield.id);
			line.npc = textfield.getValue();
    	}
    	if(textfield.id >= 14 && textfield.id < 28){
			ConversationLine line = job.getLine(textfield.id - 14);
			line.delay = textfield.getInteger();
    	}
    	if(textfield.id == 50){
    		job.generalDelay = textfield.getInteger();
    	}
    	if(textfield.id == 54){
    		job.range = textfield.getInteger();
    	}
	}


}
