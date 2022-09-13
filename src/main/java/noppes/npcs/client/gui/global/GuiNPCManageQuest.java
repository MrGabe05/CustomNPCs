package noppes.npcs.client.gui.global;

import java.util.HashMap;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.SubGuiEditText;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.*;

public class GuiNPCManageQuest extends GuiNPCInterface2 implements ICustomScrollListener{
	private HashMap<String,QuestCategory> categoryData = new HashMap<String,QuestCategory>();
	private HashMap<String,Quest> questData = new HashMap<String,Quest>();
	
	private GuiCustomScroll scrollCategories;
	private GuiCustomScroll scrollQuests;
	
	public static Screen Instance;

	private QuestCategory selectedCategory; 
	private Quest selectedQuest; 
	
    public GuiNPCManageQuest(EntityNPCInterface npc){
    	super(npc);
    	Instance = this;
    }

    @Override
    public void init(){
        super.init();
        this.addLabel(new GuiLabel(0, "gui.categories", guiLeft + 8, guiTop + 4));
        this.addLabel(new GuiLabel(1, "quest.quests", guiLeft + 175, guiTop + 4));

        this.addLabel(new GuiLabel(3, "quest.quests", guiLeft + 356, guiTop + 8));
    	this.addButton(new GuiButtonNop(this, 13, guiLeft + 356, guiTop + 18, 58, 20, "selectServer.edit", selectedQuest != null));
    	this.addButton(new GuiButtonNop(this, 12, guiLeft + 356, guiTop + 41, 58, 20, "gui.remove", selectedQuest != null));
       	this.addButton(new GuiButtonNop(this, 11, guiLeft + 356, guiTop + 64, 58, 20, "gui.add", selectedCategory != null));

        this.addLabel(new GuiLabel(2, "gui.categories", guiLeft + 356, guiTop + 110));
       	this.addButton(new GuiButtonNop(this, 3, guiLeft + 356, guiTop + 120, 58, 20, "selectServer.edit", selectedCategory != null));
    	this.addButton(new GuiButtonNop(this, 2, guiLeft + 356, guiTop + 143, 58, 20, "gui.remove", selectedCategory != null));
       	this.addButton(new GuiButtonNop(this, 1, guiLeft + 356, guiTop + 166, 58, 20, "gui.add"));

    	HashMap<String,QuestCategory> categoryData = new HashMap<String,QuestCategory>();
    	HashMap<String,Quest> questData = new HashMap<String,Quest>();
    	
    	for(QuestCategory category : QuestController.instance.categories.values()) {
    		categoryData.put(category.title, category);
    	}
    	this.categoryData = categoryData;
    	
		if(selectedCategory != null) {
			for(Quest quest : selectedCategory.quests.values()) {
				questData.put(quest.title, quest);
			}
		}
		this.questData = questData;
		
        if(scrollCategories == null){
	        scrollCategories = new GuiCustomScroll(this,0);
	        scrollCategories.setSize(170, 200);
        }
        scrollCategories.setList(Lists.newArrayList(categoryData.keySet()));
        
        scrollCategories.guiLeft = guiLeft + 4;
        scrollCategories.guiTop = guiTop + 14;
        this.addScroll(scrollCategories);
        
        if(scrollQuests == null){
        	scrollQuests = new GuiCustomScroll(this,1);
        	scrollQuests.setSize(170, 200);
        }        
        scrollQuests.setList(Lists.newArrayList(questData.keySet()));
        scrollQuests.guiLeft = guiLeft + 175;
        scrollQuests.guiTop = guiTop + 14;
        this.addScroll(scrollQuests);
                
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton) {
		GuiButtonNop button = guibutton;

        if(button.id == 1){
        	setSubGui(new SubGuiEditText(1, I18n.get("gui.new")));
        }
		if(button.id == 2) {
            ConfirmScreen guiyesno = new ConfirmScreen((bo) -> {
				if(bo) {
					Packets.sendServer(new SPacketQuestCategoryRemove(selectedCategory.id));
				}
				NoppesUtil.openGUI(player, GuiNPCManageQuest.this);
			}, new TranslationTextComponent(selectedCategory.title), new TranslationTextComponent("gui.deleteMessage"));
            setScreen(guiyesno);
		}
        if(button.id == 3){
        	setSubGui(new SubGuiEditText(3, selectedCategory.title));
        }
        if(button.id == 11){
        	setSubGui(new SubGuiEditText(11, I18n.get("gui.new")));
        }
		if(button.id == 12) {
            ConfirmScreen guiyesno = new ConfirmScreen((bo) -> {
				if(bo) {
					Packets.sendServer(new SPacketQuestRemove(selectedQuest.id));
				}
				NoppesUtil.openGUI(player, GuiNPCManageQuest.this);
			}, new TranslationTextComponent(selectedQuest.title), new TranslationTextComponent("gui.deleteMessage"));
            setScreen(guiyesno);
		}
        if(button.id == 13){
        	setSubGui(new GuiQuestEdit(selectedQuest));
        }
    }


	@Override
	public void subGuiClosed(Screen subgui){
		if(subgui instanceof SubGuiEditText && ((SubGuiEditText)subgui).cancelled) {
			return;
		}
		if(subgui instanceof SubGuiEditText){
			SubGuiEditText editText = (SubGuiEditText) subgui;
			if(editText.id == 1) {
				QuestCategory category = new QuestCategory();
				category.title = editText.text;
				while(QuestController.instance.containsCategoryName(category)) {
					category.title += "_";
				}
				Packets.sendServer(new SPacketQuestCategorySave(category.writeNBT(new CompoundNBT())));
			}
			if(editText.id == 3) {
				selectedCategory.title = editText.text;
				while(QuestController.instance.containsCategoryName(selectedCategory)) {
					selectedCategory.title += "_";
				}
				Packets.sendServer(new SPacketQuestCategorySave(selectedCategory.writeNBT(new CompoundNBT())));
			}
			if(editText.id == 11) {
				Quest quest = new Quest(selectedCategory);
				quest.title = editText.text;
				while(QuestController.instance.containsQuestName(selectedCategory, quest)) {
					quest.title += "_";
				}
				Packets.sendServer(new SPacketQuestSave(selectedCategory.id, quest.save(new CompoundNBT())));
			}
		}
		if(subgui instanceof GuiQuestEdit) {
			init();
		}
	}
		
	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll guiCustomScroll) {
		if(guiCustomScroll.id == 0){
			selectedCategory = categoryData.get(scrollCategories.getSelected());
			selectedQuest = null;
			scrollQuests.clearSelection();
		}
		if(guiCustomScroll.id == 1){
			selectedQuest = questData.get(scrollQuests.getSelected());	
		}		
		init();
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
		if(selectedQuest != null && scroll.id == 1) {
	    	setSubGui(new GuiQuestEdit(selectedQuest));
		}
	}
	
	@Override
	public void close(){
		super.close();
	}

	@Override
	public void save() {
    	GuiTextFieldNop.unfocus();
	}

}
