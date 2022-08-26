package noppes.npcs.client.gui.global;

import com.mojang.blaze3d.matrix.MatrixStack;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.GuiNPCTransportCategoryEdit;
import noppes.npcs.client.gui.mainmenu.GuiNPCGlobalMainMenu;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiStringSlotNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTransportCategoriesGet;
import noppes.npcs.packets.server.SPacketTransportCategoryRemove;
import noppes.npcs.packets.server.SPacketTransportGet;
import noppes.npcs.packets.server.SPacketTransportRemove;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class GuiNPCManageTransporters extends GuiNPCInterface implements IScrollData{
	private GuiStringSlotNop slot;
	private Map<String,Integer> data;
	private boolean selectCategory = true;
    public GuiNPCManageTransporters(EntityNPCInterface npc){
    	super(npc);
    	Packets.sendServer(new SPacketTransportCategoriesGet());
    	drawDefaultBackground = false;
		title = "";
		data = new HashMap<String, Integer>();
    }

    @Override
    public void init(){
        super.init();
        Vector<String> list = new Vector<String>();
        slot = new GuiStringSlotNop(list,this,false);
		this.children.add(this.slot);
        

    	this.addButton(new GuiButtonNop(this, 0,width / 2 - 100, height - 52, 65, 20, "gui.add"));
    	this.addButton(new GuiButtonNop(this, 1,width / 2 - 33 , height - 52, 65, 20, "selectServer.edit"));
    	getButton(0).setEnabled(selectCategory);
    	getButton(1).setEnabled(selectCategory);
    	this.addButton(new GuiButtonNop(this, 3, width / 2 + 33, height - 52,65, 20, "gui.remove"));
    	this.addButton(new GuiButtonNop(this, 2, width / 2 -100, height - 31,98, 20, "gui.open"));
    	getButton(2).setEnabled(selectCategory);
    	this.addButton(new GuiButtonNop(this, 4, width / 2  + 2, height - 31,98, 20, "gui.back"));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    	slot.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }


    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
        if(id == 0){
    		if(selectCategory){
    			NoppesUtil.openGUI(player, new GuiNPCTransportCategoryEdit(npc, this, "", -1));
    		}else{
    			
    		}
        }
        if(id == 1){
        	if(slot.getSelectedString() == null || slot.getSelectedString().isEmpty())
        		return;
    		if(selectCategory){
    			NoppesUtil.openGUI(player, new GuiNPCTransportCategoryEdit(npc, this,slot.getSelectedString(), data.get(slot.getSelectedString())));
    		}else{
    			
    		}
        }
        if(id == 4){
        	if(selectCategory){
            	close();
    			NoppesUtil.openGUI(player, new GuiNPCGlobalMainMenu(npc));
        	}else{
    			title = "";
        		selectCategory = true;
        		Packets.sendServer(new SPacketTransportCategoriesGet());
        		init();
        	}
        }
        if(id == 3){
        	if(slot.getSelectedString() == null || slot.getSelectedString().isEmpty())
        		return;
        	save();
        	if(selectCategory){
        		Packets.sendServer(new SPacketTransportCategoryRemove(data.get(slot.getSelectedString())));
        	}
        	else{
        		Packets.sendServer(new SPacketTransportRemove(data.get(slot.getSelectedString())));
        	}
        	init();
        }
        if(id == 2){
        	doubleClicked();
        }
    }

	@Override
	public void doubleClicked() {
    	if(slot.getSelectedString() == null || slot.getSelectedString().isEmpty())
    		return;
		if(selectCategory){
			selectCategory = false;
			title = "";
    		Packets.sendServer(new SPacketTransportGet(data.get(slot.getSelectedString())));
    		init();
		}
		
	}

	@Override
	public void save() {
	}

	@Override
	public void setData(Vector<String> list, Map<String, Integer> data) {
		this.data = data;
		slot.setList(list);
	}

	@Override
	public void setSelected(String selected) {
		// TODO Auto-generated method stub
		
	}


}
