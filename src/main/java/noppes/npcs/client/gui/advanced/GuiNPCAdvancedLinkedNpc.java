package noppes.npcs.client.gui.advanced;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketLinkedGet;
import noppes.npcs.packets.server.SPacketLinkedSet;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class GuiNPCAdvancedLinkedNpc extends GuiNPCInterface2 implements IScrollData, ICustomScrollListener {
	private GuiCustomScroll scroll;
	private List<String> data = new ArrayList<String>();
	
	public static Screen Instance;
	
    public GuiNPCAdvancedLinkedNpc(EntityNPCInterface npc){
    	super(npc);
    	Instance = this;
		Packets.sendServer(new SPacketLinkedGet());
    }

    @Override
    public void init(){
        super.init();
        
       	this.addButton(new GuiButtonNop(this, 1,guiLeft + 358, guiTop + 38, 58, 20, "gui.clear"));
    	
        if(scroll == null){
	        scroll = new GuiCustomScroll(this,0);
	        scroll.setSize(143, 208);
        }
        scroll.guiLeft = guiLeft + 137;
        scroll.guiTop = guiTop + 4;
        scroll.setSelected(npc.linkedName);
        scroll.setList(data);
        this.addScroll(scroll);
    }

    @Override
	public void buttonEvent(GuiButtonNop button){
        if(button.id == 1){
        	Packets.sendServer(new SPacketLinkedSet(""));
			scroll.setSelected("");
        }
        
    }

	@Override
	public void setData(Vector<String> list, Map<String, Integer> data) {
		this.data = new ArrayList<String>(list);
		init();
	}

	@Override
	public void setSelected(String selected) {
		scroll.setSelected(selected);
	}

	@Override
	public void save() {
		
	}

	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll guiCustomScroll) {
    	Packets.sendServer(new SPacketLinkedSet(guiCustomScroll.getSelected()));
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}

}
