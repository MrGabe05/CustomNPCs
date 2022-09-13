package noppes.npcs.client.gui.advanced;

import java.util.HashMap;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.select.GuiDialogSelection;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcDialogRemove;
import noppes.npcs.packets.server.SPacketNpcDialogSet;
import noppes.npcs.packets.server.SPacketNpcDialogsGet;


public class GuiNPCDialogNpcOptions extends GuiNPCInterface2 implements GuiSelectionListener,IGuiData{
	private final Screen parent;
	private final HashMap<Integer, DialogOption> data = new HashMap<Integer,DialogOption>();

	public GuiNPCDialogNpcOptions(EntityNPCInterface npc, Screen parent) {
		super(npc);
		this.parent = parent;
		this.drawDefaultBackground = true;
		Packets.sendServer(new SPacketNpcDialogsGet());
	}

	public void init() {
		super.init();
		for (int i = 0; i < 12; i++) {
			int offset = i >=6 ?200:0;
			this.addButton(new GuiButtonNop(this, i + 20, guiLeft + 20 + offset, guiTop + 13 + i % 6 * 22, 20, 20, "X"));
			this.addLabel(new GuiLabel(i, "" + i, guiLeft + 6 + offset, guiTop + 18 + i % 6 * 22));
			
			String title = "dialog.selectoption";
			if(data.containsKey(i))
				title = data.get(i).title;
			this.addButton(new GuiButtonNop(this, i, guiLeft + 44 + offset, guiTop + 13 +  i % 6 * 22, 140, 20, title));

		}
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	private int selectedSlot;
	@Override
	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
		
		if (id >= 0 && id < 20) {
			selectedSlot = id;
			int dialogID = -1;
			if(data.containsKey(id))
				dialogID = data.get(id).dialogId;
			setSubGui(new GuiDialogSelection(dialogID));
		}
		if (id >= 20 && id < 40) {
			int slot = id - 20;
			data.remove(slot);
			Packets.sendServer(new SPacketNpcDialogRemove(slot));
			init();
		}
	}

	public void save() {
		return;
	}

	@Override
	public void selected(int id, String name) {
		Packets.sendServer(new SPacketNpcDialogSet(selectedSlot, id));
	}


	@Override
	public void setGuiData(CompoundNBT compound) {
		int pos = compound.getInt("Position");
		
		DialogOption dialog = new DialogOption();
		dialog.readNBT(compound);
		
		data.put(pos, dialog);
		init();
	}

}
