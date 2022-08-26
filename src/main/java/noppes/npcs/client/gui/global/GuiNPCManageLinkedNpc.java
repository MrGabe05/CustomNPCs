package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.SubGuiEditText;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketLinkedAdd;
import noppes.npcs.packets.server.SPacketLinkedGet;
import noppes.npcs.packets.server.SPacketLinkedRemove;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class GuiNPCManageLinkedNpc extends GuiNPCInterface2 implements IScrollData {
	private GuiCustomScroll scroll;
	private List<String> data = new ArrayList<String>();
	
	public static Screen Instance;
	
    public GuiNPCManageLinkedNpc(EntityNPCInterface npc){
    	super(npc);
    	Instance = this;
		Packets.sendServer(new SPacketLinkedGet());
    }

    @Override
    public void init(){
        super.init();
        
       	this.addButton(new GuiButtonNop(this, 1,guiLeft + 358, guiTop + 38, 58, 20, "gui.add"));
    	this.addButton(new GuiButtonNop(this, 2,guiLeft + 358, guiTop + 61, 58, 20, "gui.remove"));
    	
        if(scroll == null){
	        scroll = new GuiCustomScroll(this,0);
	        scroll.setSize(143, 208);
        }
        scroll.guiLeft = guiLeft + 214;
        scroll.guiTop = guiTop + 4;
        scroll.setList(data);
        this.addScroll(scroll);
    }

    @Override
	public void buttonEvent(GuiButtonNop button){
        if(button.id == 1){
        	save();
        	setSubGui(new SubGuiEditText("New"));
        }
        if(button.id == 2){
        	if(scroll.hasSelected())
        		Packets.sendServer(new SPacketLinkedRemove(scroll.getSelected()));
        }
        
    }

	@Override
	public void subGuiClosed(Screen subgui) {
		if(!((SubGuiEditText)subgui).cancelled){
			Packets.sendServer(new SPacketLinkedAdd(((SubGuiEditText)subgui).text));
		}
	}

	@Override
	public void setData(Vector<String> list, Map<String, Integer> data) {
		this.data = new ArrayList<String>(list);
		init();
	}

	@Override
	public void setSelected(String selected) {
		
	}

	@Override
	public void save() {
		
	}

}
