package noppes.npcs.client.gui.select;

import com.google.common.collect.Lists;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

import java.util.HashMap;

public class GuiDialogSelection extends GuiBasic implements ICustomScrollListener {
	private HashMap<String,DialogCategory> categoryData = new HashMap<String,DialogCategory>();
	private HashMap<String,Dialog> dialogData = new HashMap<String,Dialog>();
	
	private GuiCustomScroll scrollCategories;
	private GuiCustomScroll scrollDialogs;

	private DialogCategory selectedCategory; 
	public Dialog selectedDialog; 
	
	private GuiSelectionListener listener;
	
    public GuiDialogSelection(int dialog){
    	drawDefaultBackground = false;
		title = "";
		setBackground("menubg.png");
		imageWidth = 366;
		imageHeight = 226;
    	this.selectedDialog = DialogController.instance.dialogs.get(dialog);
    	if(selectedDialog != null) {
    		selectedCategory = selectedDialog.category;
    	}
    }

    @Override
    public void init(){
        super.init();
    	
		if(wrapper.parent instanceof GuiSelectionListener){
			listener = (GuiSelectionListener) wrapper.parent;
		}
        this.addLabel(new GuiLabel(0, "gui.categories", guiLeft + 8, guiTop + 4));
        this.addLabel(new GuiLabel(1, "dialog.dialogs", guiLeft + 175, guiTop + 4));
        
    	this.addButton(new GuiButtonNop(this, 2, guiLeft + imageWidth - 26, guiTop + 4, 20, 20, "X"));
    	
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
        if(selectedCategory != null) {
        	scrollCategories.setSelected(selectedCategory.title);
        }
        
        scrollCategories.guiLeft = guiLeft + 4;
        scrollCategories.guiTop = guiTop + 14;
        this.addScroll(scrollCategories);
        
        if(scrollDialogs == null){
        	scrollDialogs = new GuiCustomScroll(this,1);
        	scrollDialogs.setSize(170, 200);
        }        
        scrollDialogs.setList(Lists.newArrayList(dialogData.keySet()));
        if(selectedDialog != null) {
        	scrollDialogs.setSelected(selectedDialog.title);
        }
        scrollDialogs.guiLeft = guiLeft + 175;
        scrollDialogs.guiTop = guiTop + 14;
        this.addScroll(scrollDialogs);
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
		if(selectedDialog == null)
			return;
		if(listener != null) {
			listener.selected(selectedDialog.id, selectedDialog.title);
		}
		close();
	}
    
    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
        if(id == 2){
        	if(selectedDialog != null) {
            	scrollDoubleClicked(null, null);
        	}
        	else {
        		close();
        	}
        }
    }
}
