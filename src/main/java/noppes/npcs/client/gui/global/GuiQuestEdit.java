package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.client.gui.SubGuiMailmanSendSetup;
import noppes.npcs.client.gui.SubGuiNpcCommand;
import noppes.npcs.client.gui.SubGuiNpcFactionOptions;
import noppes.npcs.shared.client.gui.GuiTextAreaScreen;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeDialog;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeKill;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeLocation;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeManual;
import noppes.npcs.client.gui.select.GuiQuestSelection;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumQuestCompletion;
import noppes.npcs.constants.EnumQuestRepeat;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketQuestOpen;
import noppes.npcs.packets.server.SPacketQuestSave;
import noppes.npcs.shared.client.gui.components.GuiButtonBiDirectional;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiQuestEdit extends GuiNPCInterface implements GuiSelectionListener, ITextfieldListener {
	private final Quest quest;
	private boolean questlogTA = false;
	
    public GuiQuestEdit(Quest quest){
    	this.quest = quest;
		setBackground("menubg.png");
		imageWidth = 386;
		imageHeight = 226;
    	NoppesUtilServer.setEditingQuest(player, quest);
    }

    @Override
    public void init(){
        super.init();

		addLabel(new GuiLabel(1,"gui.title", guiLeft + 4, guiTop + 8));
		addTextField(new GuiTextFieldNop(1, this, guiLeft + 46 , guiTop + 3, 220, 20, quest.title));

		addLabel(new GuiLabel(0,"ID", guiLeft + 268, guiTop + 4));
		addLabel(new GuiLabel(2,	quest.id + "", guiLeft + 268, guiTop + 14));
		
		addLabel(new GuiLabel(3, "quest.completedtext", guiLeft + 4, guiTop + 30));
    	addButton(new GuiButtonNop(this, 3, guiLeft + 120, guiTop + 25, 50, 20, "selectServer.edit"));
    	
		addLabel(new GuiLabel(4, "quest.questlogtext", guiLeft + 4, guiTop + 51));
		addButton(new GuiButtonNop(this, 4, guiLeft + 120, guiTop + 46, 50, 20, "selectServer.edit"));

		addLabel(new GuiLabel(5, "quest.reward", guiLeft + 4, guiTop + 72));
    	addButton(new GuiButtonNop(this, 5, guiLeft + 120, guiTop + 67, 50, 20, "selectServer.edit"));

		addLabel(new GuiLabel(6, "gui.type", guiLeft + 4, guiTop + 93));
    	addButton(new GuiButtonBiDirectional(this,6, guiLeft + 70, guiTop + 88, 90, 20, new String[]{"quest.item","quest.dialog","quest.kill","quest.location","quest.areakill","quest.manual"},quest.type));
    	addButton(new GuiButtonNop(this, 7, guiLeft + 162, guiTop + 88,50, 20, "selectServer.edit"));

		addLabel(new GuiLabel(8, "quest.repeatable", guiLeft + 4, guiTop + 114));
    	this.addButton(new GuiButtonBiDirectional(this,8, guiLeft + 70, guiTop + 109, 140, 20, new String[]{"gui.no","gui.yes","quest.mcdaily","quest.mcweekly","quest.rldaily","quest.rlweekly"}, quest.repeat.ordinal()));

    	this.addButton(new GuiButtonNop(this, 9, guiLeft + 4, guiTop + 131,90, 20, new String[]{"quest.npc","quest.instant"},quest.completion.ordinal()));

    	if(quest.completerNpc.isEmpty())
    		quest.completerNpc = npc.display.getName();
    	
		this.addTextField(new GuiTextFieldNop(2,this, guiLeft + 96, guiTop + 131, 114, 20, quest.completerNpc));
		this.getTextField(2).enabled = quest.completion == EnumQuestCompletion.Npc;

		addLabel(new GuiLabel(10, "faction.options", guiLeft + 214, guiTop + 30));
		addButton(new GuiButtonNop(this, 10, guiLeft + 330, guiTop + 25, 50, 20, "selectServer.edit"));

		addLabel(new GuiLabel(15, "advMode.command", guiLeft + 214, guiTop + 52));
		addButton(new GuiButtonNop(this, 15, guiLeft + 330, guiTop + 47, 50, 20, "selectServer.edit"));

		addButton(new GuiButtonNop(this, 13, guiLeft + 4, guiTop + 153, 164, 20, "mailbox.setup"));
		addButton(new GuiButtonNop(this, 14, guiLeft + 170, guiTop + 153, 20, 20, "X"));
		if(!quest.mail.subject.isEmpty())
			getButton(13).setDisplayText(quest.mail.subject);
		
		addButton(new GuiButtonNop(this, 11, guiLeft + 4, guiTop + 175, 164, 20, "quest.next"));
		addButton(new GuiButtonNop(this, 12, guiLeft + 170, guiTop + 175, 20, 20, "X"));

		IQuest q = QuestController.instance.get(quest.nextQuestid);
		if(q != null)
			getButton(11).setDisplayText(q.getName());

    	addButton(new GuiButtonNop(this, 66, guiLeft + 362, guiTop + 4, 20, 20, "X"));
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		GuiButtonNop button = guibutton;

        if(button.id == 3){
        	questlogTA = false;
        	setSubGui(new GuiTextAreaScreen(quest.completeText));
        }
        if(button.id == 4){
        	questlogTA = true;
        	setSubGui(new GuiTextAreaScreen(quest.logText));
        }
        if(button.id == 5){
        	Packets.sendServer(new SPacketQuestOpen(EnumGuiType.QuestReward, quest.save(new CompoundNBT())));
        }
        if(button.id == 6){
        	quest.setType(button.getValue());
        }
        if(button.id == 7){
        	if(quest.type == QuestType.ITEM)
        		Packets.sendServer(new SPacketQuestOpen(EnumGuiType.QuestItem, quest.save(new CompoundNBT())));
        	
        	if(quest.type == QuestType.DIALOG)
        		setSubGui(new GuiNpcQuestTypeDialog(npc, quest, wrapper.parent));
        	
        	if(quest.type == QuestType.KILL)
        		setSubGui(new GuiNpcQuestTypeKill(npc, quest, wrapper.parent));
        	
        	if(quest.type == QuestType.LOCATION)
        		setSubGui(new GuiNpcQuestTypeLocation(npc, quest, wrapper.parent));
        	
        	if(quest.type == QuestType.AREA_KILL)
        		setSubGui(new GuiNpcQuestTypeKill(npc, quest, wrapper.parent));
        	
        	if(quest.type == QuestType.MANUAL)
        		setSubGui(new GuiNpcQuestTypeManual(npc, quest, wrapper.parent));
        }
        if(button.id == 8){
        	quest.repeat = EnumQuestRepeat.values()[button.getValue()];
        }
        if(button.id == 9){	
        	quest.completion = EnumQuestCompletion.values()[button.getValue()];
    		this.getTextField(2).enabled = quest.completion == EnumQuestCompletion.Npc;
        }
    	if(button.id == 15){
    		setSubGui(new SubGuiNpcCommand(quest.command));
    	}
        if(button.id == 10){	
        	setSubGui(new SubGuiNpcFactionOptions(quest.factionOptions));
        }
        
        if(button.id == 11){
        	setSubGui(new GuiQuestSelection(quest.nextQuestid));
        }
        
        if(button.id == 12){
        	quest.nextQuestid = -1;
        	init();
        }
    	if(button.id == 13){
    		setSubGui(new SubGuiMailmanSendSetup(quest.mail));
    	}
        if(button.id == 14){
        	quest.mail = new PlayerMail();
        	init();
        }
        if(button.id == 66){
        	close();
        }        
    }
	
	@Override
	public void unFocused(GuiTextFieldNop guiNpcTextField) {
		if(guiNpcTextField.id == 1){
	    	quest.title = guiNpcTextField.getValue();
			while(QuestController.instance.containsQuestName(quest.category, quest)) {
				quest.title += "_";
			}
		}	
		if(guiNpcTextField.id == 2){
	    	quest.completerNpc = guiNpcTextField.getValue();
		}		
	}

	@Override
	public void subGuiClosed(Screen subgui){
		if(subgui instanceof GuiTextAreaScreen){
			GuiTextAreaScreen gui = (GuiTextAreaScreen) subgui;
			if(questlogTA)
				quest.logText = gui.text;
			else
				quest.completeText = gui.text;
		}
		else if(subgui instanceof SubGuiNpcCommand){
			SubGuiNpcCommand sub = (SubGuiNpcCommand) subgui;
			quest.command = sub.command;
		}
		else{
			init();
		}
	}
	
	@Override
	public void selected(int id, String name) {
		quest.nextQuestid = id;
	}
		
	@Override
	public void close(){
		super.close();
	}

	@Override
	public void save() {
    	GuiTextFieldNop.unfocus();
		Packets.sendServer(new SPacketQuestSave(quest.category.id, quest.save(new CompoundNBT())));
	}
}
