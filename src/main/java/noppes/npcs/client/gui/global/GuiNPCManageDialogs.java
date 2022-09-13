package noppes.npcs.client.gui.global;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.SubGuiEditText;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketDialogCategoryRemove;
import noppes.npcs.packets.server.SPacketDialogCategorySave;
import noppes.npcs.packets.server.SPacketDialogRemove;
import noppes.npcs.packets.server.SPacketDialogSave;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

import java.util.HashMap;

public class GuiNPCManageDialogs extends GuiNPCInterface2 implements ICustomScrollListener {
	private HashMap<String,DialogCategory> categoryData = new HashMap<String,DialogCategory>();
	private HashMap<String,Dialog> dialogData = new HashMap<String,Dialog>();
	
	private GuiCustomScroll scrollCategories;
	private GuiCustomScroll scrollDialogs;
	
	public static Screen Instance;

	private DialogCategory selectedCategory; 
	private Dialog selectedDialog; 
	
    public GuiNPCManageDialogs(EntityNPCInterface npc){
    	super(npc);
    	Instance = this;
    }

    @Override
    public void init(){
        super.init();
        this.addLabel(new GuiLabel(0, "gui.categories", guiLeft + 8, guiTop + 4));
        this.addLabel(new GuiLabel(1, "dialog.dialogs", guiLeft + 175, guiTop + 4));

        this.addLabel(new GuiLabel(3, "dialog.dialogs", guiLeft + 356, guiTop + 8));
    	this.addButton(new GuiButtonNop(this, 13, guiLeft + 356, guiTop + 18, 58, 20, "selectServer.edit", selectedDialog != null));
    	this.addButton(new GuiButtonNop(this, 12, guiLeft + 356, guiTop + 41, 58, 20, "gui.remove", selectedDialog != null));
       	this.addButton(new GuiButtonNop(this, 11, guiLeft + 356, guiTop + 64, 58, 20, "gui.add", selectedCategory != null));

        this.addLabel(new GuiLabel(2, "gui.categories", guiLeft + 356, guiTop + 110));
       	this.addButton(new GuiButtonNop(this, 3, guiLeft + 356, guiTop + 120, 58, 20, "selectServer.edit", selectedCategory != null));
    	this.addButton(new GuiButtonNop(this, 2, guiLeft + 356, guiTop + 143, 58, 20, "gui.remove", selectedCategory != null));
       	this.addButton(new GuiButtonNop(this, 1, guiLeft + 356, guiTop + 166, 58, 20, "gui.add"));

    	HashMap<String,DialogCategory> categoryData = new HashMap<String,DialogCategory>();
    	HashMap<String,Dialog> dialogData = new HashMap<String,Dialog>();
    	
    	for(DialogCategory category : DialogController.instance.categories.values()) {
    		categoryData.put(category.title, category);
    	}
    	this.categoryData = categoryData;
    	
		if(selectedCategory != null) {
			for(Dialog dialog : selectedCategory.dialogs.values()) {
				dialogData.put(dialog.title, dialog);
			}
		}
		this.dialogData = dialogData;
		
        if(scrollCategories == null){
	        scrollCategories = new GuiCustomScroll(this,0);
	        scrollCategories.setSize(170, 200);
        }
        scrollCategories.setList(Lists.newArrayList(categoryData.keySet()));
        
        scrollCategories.guiLeft = guiLeft + 4;
        scrollCategories.guiTop = guiTop + 14;
        this.addScroll(scrollCategories);
        
        if(scrollDialogs == null){
        	scrollDialogs = new GuiCustomScroll(this,1);
        	scrollDialogs.setSize(170, 200);
        }        
        scrollDialogs.setList(Lists.newArrayList(dialogData.keySet()));
        scrollDialogs.guiLeft = guiLeft + 175;
        scrollDialogs.guiTop = guiTop + 14;
        this.addScroll(scrollDialogs);
                
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
					Packets.sendServer(new SPacketDialogCategoryRemove(selectedCategory.id));
				}
				NoppesUtil.openGUI(player, GuiNPCManageDialogs.this);
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
					Packets.sendServer(new SPacketDialogRemove(selectedDialog.id));
				}
				NoppesUtil.openGUI(player, GuiNPCManageDialogs.this);
			}, new TranslationTextComponent(selectedDialog.title), new TranslationTextComponent("gui.deleteMessage"));
            setScreen(guiyesno);
		}
        if(button.id == 13){
        	setSubGui(new GuiDialogEdit(selectedDialog));
        }
    }


	@Override
	public void subGuiClosed(Screen subgui){
		if(subgui instanceof SubGuiEditText && ((SubGuiEditText)subgui).cancelled) {
			return;
		}
		if(subgui instanceof SubGuiEditText) {
			SubGuiEditText dialogEdit = (SubGuiEditText) subgui;
			if(dialogEdit.id == 1) {
				DialogCategory category = new DialogCategory();
				category.title = dialogEdit.text;
				while(DialogController.instance.containsCategoryName(category)) {
					category.title += "_";
				}
				Packets.sendServer(new SPacketDialogCategorySave(category.writeNBT(new CompoundNBT())));
			}
			if(dialogEdit.id == 3) {
				selectedCategory.title = dialogEdit.text;
				while(DialogController.instance.containsCategoryName(selectedCategory)) {
					selectedCategory.title += "_";
				}
				Packets.sendServer(new SPacketDialogCategorySave(selectedCategory.writeNBT(new CompoundNBT())));
			}
			if(dialogEdit.id == 11) {
				Dialog dialog = new Dialog(selectedCategory);
				dialog.title = dialogEdit.text;
				while(DialogController.instance.containsDialogName(selectedCategory, dialog)) {
					dialog.title += "_";
				}
				Packets.sendServer(new SPacketDialogSave(selectedCategory.id, dialog.save(new CompoundNBT())));
			}
		}
		if(subgui instanceof GuiDialogEdit) {
			init();
		}
	}
		
	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll guiCustomScroll) {
		if(guiCustomScroll.id == 0){
			selectedCategory = categoryData.get(scrollCategories.getSelected());
			selectedDialog = null;
			scrollDialogs.clearSelection();
		}
		if(guiCustomScroll.id == 1){
			selectedDialog = dialogData.get(scrollDialogs.getSelected());	
		}		
		init();
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
		if(selectedDialog != null && scroll.id == 1) {
	    	setSubGui(new GuiDialogEdit(selectedDialog));
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
