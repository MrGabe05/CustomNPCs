package noppes.npcs.client.gui;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.client.gui.player.GuiMailmanWrite;
import noppes.npcs.client.gui.select.GuiQuestSelection;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMailSetup;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiMailmanSendSetup extends GuiBasic implements ITextfieldListener, GuiSelectionListener {
	
	private final PlayerMail mail;
	
	public SubGuiMailmanSendSetup(PlayerMail mail){
        imageWidth = 256;
        setBackground("menubg.png");
		this.mail = mail;
	}

	@Override
	public void init(){
		super.init();
		addLabel(new GuiLabel(1, "mailbox.subject", guiLeft + 4, guiTop + 19));
		addTextField(new GuiTextFieldNop(1, this,  guiLeft + 60, guiTop + 14, 180, 20, mail.subject));
		addLabel(new GuiLabel(0, "mailbox.sender", guiLeft + 4, guiTop + 41));
		addTextField(new GuiTextFieldNop(0, this,  guiLeft + 60, guiTop + 36, 180, 20, mail.sender));

		addButton(new GuiButtonNop(this, 2, guiLeft + 29, guiTop + 100, "mailbox.write"));

		
		addLabel(new GuiLabel(3, "quest.quest", guiLeft + 13, guiTop + 135));
		IQuest quest = mail.getQuest();
		String title = "gui.select";
		if(quest != null)
			title = quest.getName();
		addButton(new GuiButtonNop(this, 3, guiLeft + 70, guiTop + 130, 100, 20, title));
		addButton(new GuiButtonNop(this, 4, guiLeft + 171, guiTop + 130, 20, 20, "X"));
		
		addButton(new GuiButtonNop(this, 0, guiLeft + 26, guiTop + 190, 100, 20, "gui.done"));
		addButton(new GuiButtonNop(this, 1, guiLeft + 130, guiTop + 190, 100, 20, "gui.cancel"));
		
		if(player.containerMenu instanceof ContainerMail){
			ContainerMail container = (ContainerMail) player.containerMenu;
			mail.items = container.mail.items;
		}
	}

	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
		if(id == 0){
			close();
		}
		if(id == 1){
			mail.questId = -1;
			mail.message = new CompoundNBT();
			close();
		}
		if(id == 2){
			GuiMailmanWrite.parent = getParent();
			GuiMailmanWrite.mail = mail;

    		Packets.sendServer(new SPacketMailSetup(mail.writeNBT()));
		}
    	if(id == 3){
			setSubGui(new GuiQuestSelection(mail.questId));
    	}
    	if(id == 4){
    		mail.questId = -1;
    		init();
    	}
	}

	@Override
	public void selected(int ob, String name) {
		mail.questId = ob;
		init();
	}
	@Override
	public void save() {
		
	}

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 0)
			mail.sender = textfield.getValue();
		if(textfield.id == 1)
			mail.subject = textfield.getValue();
	}
}
