package noppes.npcs.client.gui.questtypes;

import java.util.HashMap;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.select.GuiDialogSelection;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketQuestDialogTitles;
import noppes.npcs.quests.QuestDialog;

public class GuiNpcQuestTypeDialog extends GuiNPCInterface implements GuiSelectionListener, IGuiData
{
	private final Screen parent;
	
	private final QuestDialog quest;
	
	private final HashMap<Integer, String> data = new HashMap<Integer, String>();

    public GuiNpcQuestTypeDialog(EntityNPCInterface npc, Quest q, Screen parent) {
    	this.npc = npc;
    	this.parent = parent;
    	title = "Quest Dialog Setup";
    	
    	quest = (QuestDialog) q.questInterface;

		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
		Packets.sendServer(new SPacketQuestDialogTitles(
				quest.dialogs.containsKey(0)?quest.dialogs.get(0):-1, 
				quest.dialogs.containsKey(1)?quest.dialogs.get(1):-1, 
				quest.dialogs.containsKey(2)?quest.dialogs.get(2):-1));
	}

	@Override
	public void init() {
		super.init();
		for (int i = 0; i < 3; i++) {
			String title = "dialog.selectoption";
			if(data.containsKey(i))
				title = data.get(i);
			this.addButton(new GuiButtonNop(this, i + 9, guiLeft + 10, 55 + i * 22, 20, 20, "X"));
			this.addButton(new GuiButtonNop(this, i + 3, guiLeft + 34, 55 + i * 22, 210, 20, title));

		}
		this.addButton(new GuiButtonNop(this, 0, guiLeft + 150, guiTop + 190, 98, 20, "gui.back"));
		
	}

	private int selectedSlot;
	@Override
	public void buttonEvent(GuiButtonNop guibutton) {
		GuiButtonNop button = guibutton;
		if (button.id == 0) {
			close();
		}
		if (button.id >= 3 && button.id < 9) {
			selectedSlot = button.id - 3;
			int id = -1;
			if(quest.dialogs.containsKey(selectedSlot))
				id = quest.dialogs.get(selectedSlot);
			setSubGui(new GuiDialogSelection(id));
		}
		if (button.id >= 9 && button.id < 15) {
			int slot = button.id - 9;
			quest.dialogs.remove(slot);
			data.remove(slot);
			save();
			init();
		}
	}

	@Override
	public void save() {
	}

	@Override
	public void selected(int id, String name) {
		quest.dialogs.put(selectedSlot, id);
		data.put(selectedSlot, name);
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		data.clear();
		if(compound.contains("1")){
			data.put(0, compound.getString("1"));
		}
		if(compound.contains("2")){
			data.put(1, compound.getString("2"));
		}
		if(compound.contains("3")){
			data.put(2, compound.getString("3"));
		}
		init();
	}

}
