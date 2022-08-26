package noppes.npcs.client.gui;

import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.*;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GuiNpcRemoteEditor extends GuiNPCInterface implements IScrollData {
    
    private GuiCustomScroll scroll;
    private Map<String, Integer> data = new HashMap<String, Integer>();
	public GuiNpcRemoteEditor() {
		super();
        imageWidth = 256;
        setBackground("menubg.png");
		Packets.sendServer(new SPacketRemoteNpcsGet());
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
        
        String title = I18n.get("remote.title");
        int x = (imageWidth - this.font.width(title)) / 2;
        
        this.addLabel(new GuiLabel(0, title, guiLeft + x, guiTop - 8));

        this.addButton(new GuiButtonNop(this, 0, guiLeft + 170, guiTop + 6,82,20, "selectServer.edit"));
        this.addButton(new GuiButtonNop(this, 1, guiLeft + 170, guiTop + 28,82,20, "selectWorld.deleteButton"));
        this.addButton(new GuiButtonNop(this, 2, guiLeft + 170, guiTop + 50,82,20, "gui.reset"));
        this.addButton(new GuiButtonNop(this, 4, guiLeft + 170, guiTop + 72,82,20, "remote.tp"));
        this.addButton(new GuiButtonNop(this, 5, guiLeft + 170, guiTop + 110,82,20, "remote.resetall"));
        this.addButton(new GuiButtonNop(this, 3, guiLeft + 170, guiTop + 132,82,20, "remote.freeze"));
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
    	if(id == 3){
    		Packets.sendServer(new SPacketRemoteFreeze());
    	}
    	if(id == 5){
    		for(int ids : data.values()){
    			Packets.sendServer(new SPacketRemoteNpcReset(ids));
	    		Entity entity  = player.level.getEntity(ids);
	    		if(entity != null && entity instanceof EntityNPCInterface)
	    			((EntityNPCInterface)entity).reset();
    		}
    	}
    	
    	if(!data.containsKey(scroll.getSelected()))
    		return;
    	
    	if(id == 0){
    		Packets.sendServer(new SPacketRemoteMenuOpen(data.get(scroll.getSelected())));
    	}
    	if(id == 1){
            ConfirmScreen guiyesno = new ConfirmScreen((bo) -> {
				if(bo){
					Packets.sendServer(new SPacketRemoteNpcDelete(data.get(scroll.getSelected())));
				}
				NoppesUtil.openGUI(player, this);}, new TranslationTextComponent(""),new TranslationTextComponent("gui.deleteMessage"));
            setScreen(guiyesno);
    	}
    	if(id == 2){
    		Packets.sendServer(new SPacketRemoteNpcReset(data.get(scroll.getSelected())));
    		Entity entity  = player.level.getEntity(data.get(scroll.getSelected()));
    		if(entity != null && entity instanceof EntityNPCInterface)
    			((EntityNPCInterface)entity).reset();
    	}
    	if(id == 4){
    		Packets.sendServer(new SPacketRemoteNpcTp(data.get(scroll.getSelected())));
    		close();
    	}
    }
	
    @Override
    public boolean mouseClicked(double i, double j, int k) {
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
