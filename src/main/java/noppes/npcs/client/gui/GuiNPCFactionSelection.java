package noppes.npcs.client.gui;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.api.handler.data.IFaction;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiStringSlotNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.entity.EntityNPCInterface;


public class GuiNPCFactionSelection extends GuiNPCInterface {
	private GuiStringSlotNop slot;
	private Screen parent;
	private int factionId;
	public GuiSelectionListener listener;
	
    public GuiNPCFactionSelection(EntityNPCInterface npc,Screen parent,int dialog){
    	super(npc);
    	drawDefaultBackground = false;
		title = "Select Dialog Category";
    	this.parent = parent;
    	this.factionId = dialog;
    	
    	
    	if(parent instanceof GuiSelectionListener){
    		listener = (GuiSelectionListener) parent;
    	}
    }

    @Override
    public void init(){
        super.init();
		Map<String, Integer> coloredMap = new HashMap<>();
		String selected = null;
		for(IFaction f : FactionController.instance.list()){
			coloredMap.put(f.getName(), f.getColor());
			if(factionId == f.getId()) {
				selected = f.getName();
			}
		}
        slot = new GuiStringSlotNop(null,this,false);
		slot.setColoredList(coloredMap);
		slot.setSelected(selected);
		this.children.add(this.slot);
        
    	this.addButton(new GuiButtonNop(this, 2, width / 2 -100, height - 41,98, 20, "gui.back"));
    	this.addButton(new GuiButtonNop(this, 4, width / 2  + 2, height - 41,98, 20, "mco.template.button.select"));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    	slot.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
        if(id == 2){
        	close();
        	NoppesUtil.openGUI(player, parent);
        }
        if(id == 4){
        	doubleClicked();
        }
    }
	public void doubleClicked() {
    	if(slot.getSelectedString() == null || slot.getSelectedString().isEmpty())
    		return;
		factionId = FactionController.instance.getFactionFromName(slot.getSelectedString()).id;
		close();
		NoppesUtil.openGUI(player, parent);
		
	}
	public void save() {
		if(factionId >= 0){
			if(listener != null)
				listener.selected(factionId, slot.getSelectedString());
		}
	}


}
