package noppes.npcs.client.gui.roles;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcTransportGet;
import noppes.npcs.packets.server.SPacketTransportCategoriesGet;
import noppes.npcs.packets.server.SPacketTransportSave;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GuiNpcTransporter extends GuiNPCInterface2 implements IScrollData, IGuiData {
	private GuiCustomScroll scroll;
	public TransportLocation location = new TransportLocation();
	private Map<String,Integer> data = new HashMap<String,Integer>();
	
    public GuiNpcTransporter(EntityNPCInterface npc){
    	super(npc);
		Packets.sendServer(new SPacketTransportCategoriesGet());
		Packets.sendServer(new SPacketNpcTransportGet());
    }

    @Override
    public void init(){
        super.init();
        Vector<String> list = new Vector<String>();
        list.addAll(data.keySet());
        
        if(scroll == null){
	        scroll = new GuiCustomScroll(this,0);
	        scroll.setSize(143, 208);
        }
        scroll.guiLeft = guiLeft + 214;
        scroll.guiTop = guiTop + 4;
        
        addScroll(scroll);
        addLabel(new GuiLabel(0,"gui.name", guiLeft + 4, height + 8));
        addTextField(new GuiTextFieldNop(0, this,  guiLeft + 60, guiTop + 3, 140, 20, location.name));
        addButton(new GuiButtonNop(this, 0, guiLeft + 4, guiTop + 31, new String[]{"transporter.discovered","transporter.start","transporter.interaction"}, location.type));
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
    	GuiButtonNop button = (GuiButtonNop) guibutton;
    	if(button.id == 0){
    		location.type = button.getValue();
    	}    	
    }
    
	@Override
	public void save() {
		if(!scroll.hasSelected())
			return;
		
		String name = getTextField(0).getValue();
		if(!name.isEmpty())
			location.name = name;
		
		location.pos = player.blockPosition();
		location.dimension = player.getCommandSenderWorld().dimension();
		
		int cat = data.get(scroll.getSelected());
		Packets.sendServer(new SPacketTransportSave(cat, location.writeNBT()));
	}
	
	@Override
	public void setData(Vector<String> list, Map<String, Integer> data) {
		this.data = data;
		this.scroll.setList(list);
	}
	
	@Override
	public void setSelected(String selected) {
		scroll.setSelected(selected);
	}
	
	@Override
	public void setGuiData(CompoundNBT compound) {
		TransportLocation loc = new TransportLocation();
		loc.readNBT(compound);
		location = loc;
		init();
	}


}
