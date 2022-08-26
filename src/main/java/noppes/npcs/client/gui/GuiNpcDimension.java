package noppes.npcs.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.packets.server.SPacketDimensionsGet;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GuiNpcDimension extends GuiNPCInterface implements IScrollData {
    
    private GuiCustomScroll scroll;
    private Map<String, Integer> data = new HashMap<String, Integer>();
	public GuiNpcDimension() {
		super();
        imageWidth = 256;
        setBackground("menubg.png");
        Packets.sendServer(new SPacketDimensionsGet());
	}
    
    @Override
    public void init(){
        super.init();
        if(scroll == null){
	        scroll = new GuiCustomScroll(this,0);
	        scroll.setSize(165, 208);
        }
        scroll.guiLeft = guiLeft + 4;
        scroll.guiTop = guiTop + 4;
        addScroll(scroll);
        
        String title = I18n.get("gui.dimensions");
        int x = (imageWidth - this.font.width(title)) / 2;
        
        this.addLabel(new GuiLabel(0, title, guiLeft + x, guiTop - 8));

        this.addButton(new GuiButtonNop(this, 4, guiLeft + 170, guiTop + 72,82,20, "remote.tp"));
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
    	
    	if(!data.containsKey(scroll.getSelected()))
    		return;
    	
    	if(id == 4){
    		Packets.sendServer(new SPacketDimensionTeleport(new ResourceLocation(scroll.getSelected())));
    		close();
    	}
    }
	
    @Override
    public boolean mouseClicked(double i, double j, int k){
    	scroll.mouseClicked(i, j, k);
        return super.mouseClicked(i, j, k);
    }

	@Override
	public void save() {
		
	}
	
	@Override
	public void setData(Vector<String> list, Map<String, Integer> data) {
		scroll.setList(list);
		this.data = data;
	}
	
	@Override
	public void setSelected(String selected) {
		getButton(3).setDisplayText(selected);
	}

}
