package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.SubGuiNpcFactionPoints;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketFactionGet;
import noppes.npcs.packets.server.SPacketFactionRemove;
import noppes.npcs.packets.server.SPacketFactionSave;
import noppes.npcs.packets.server.SPacketFactionsGet;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import java.util.*;

public class GuiNPCManageFactions extends GuiNPCInterface2 implements IScrollData, ICustomScrollListener, ITextfieldListener, IGuiData
{
	private GuiCustomScroll scrollFactions;
	private Map<String,Integer> data = new HashMap<String,Integer>();
	private Faction faction = new Faction();
	private String selected = null;
	
    public GuiNPCManageFactions(EntityNPCInterface npc) {
    	super(npc);
    	Packets.sendServer(new SPacketFactionsGet());
    }

    @Override
    public void init() {
        super.init();
        
       	this.addButton(new GuiButtonNop(this, 0,guiLeft + 368, guiTop + 8, 45, 20, "gui.add"));
    	this.addButton(new GuiButtonNop(this, 1,guiLeft + 368, guiTop + 32, 45, 20, "gui.remove"));
        
    	if(scrollFactions == null){
	        scrollFactions = new GuiCustomScroll(this,0);
	        scrollFactions.setSize(143, 208);
    	}
        scrollFactions.guiLeft = guiLeft + 220;
        scrollFactions.guiTop = guiTop + 4;
    	addScroll(scrollFactions);
        
    	if (faction.id == -1)
    		return;
           	
    	this.addTextField(new GuiTextFieldNop(0, this, guiLeft + 40, guiTop + 4, 136, 20, faction.name));
    	getTextField(0).setMaxLength(20);
    	addLabel(new GuiLabel(0,"gui.name", guiLeft + 8, guiTop + 9));

		addLabel(new GuiLabel(10,"ID", guiLeft + 178, guiTop + 4));
		addLabel(new GuiLabel(11, faction.id + "", guiLeft + 178, guiTop + 14));

    	String color = Integer.toHexString(faction.color);
    	while(color.length() < 6)
    		color = "0" + color;
    	addButton(new GuiButtonNop(this, 10, guiLeft + 40, guiTop + 26, 60, 20, color));
    	addLabel(new GuiLabel(1,"gui.color", guiLeft + 8, guiTop + 31));
    	getButton(10).setFGColor(faction.color);

    	addLabel(new GuiLabel(2,"faction.points", guiLeft + 8, guiTop + 53));
       	this.addButton(new GuiButtonNop(this, 2,guiLeft + 100, guiTop + 48, 45, 20, "selectServer.edit"));

    	addLabel(new GuiLabel(3,"faction.hidden", guiLeft + 8, guiTop + 75));
       	this.addButton(new GuiButtonNop(this, 3,guiLeft + 100, guiTop + 70, 45, 20, new String[]{"gui.no","gui.yes"},faction.hideFaction?1:0));


    	addLabel(new GuiLabel(4,"faction.attacked", guiLeft + 8, guiTop + 97));
       	this.addButton(new GuiButtonNop(this, 4,guiLeft + 100, guiTop + 92, 45, 20, new String[]{"gui.no","gui.yes"},faction.getsAttacked?1:0));
    	
    	addLabel(new GuiLabel(6,"faction.hostiles", guiLeft + 8, guiTop + 145));
    	
		ArrayList<String> hostileList = new ArrayList<String>(scrollFactions.getList());
		hostileList.remove(faction.name);

		HashSet<String> set = new HashSet<String>();
		for(String s : data.keySet()){
			if(!s.equals(faction.name) && faction.attackFactions.contains(data.get(s)))
				set.add(s);
		}
		
    	GuiCustomScroll scrollHostileFactions = new GuiCustomScroll(this,1,true);
        scrollHostileFactions.setSize(163, 58);
        scrollHostileFactions.guiLeft = guiLeft + 4;
        scrollHostileFactions.guiTop = guiTop + 154;
		scrollHostileFactions.setList(hostileList);
		scrollHostileFactions.setSelectedList(set);
        addScroll(scrollHostileFactions);
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		GuiButtonNop button = guibutton;
        if(button.id == 0){
        	save();
        	String name = I18n.get("gui.new");
        	while(data.containsKey(name))
        		name += "_";
        	Faction faction = new Faction(-1, name, 0x00FF00, 1000);
        	
			CompoundNBT compound = new CompoundNBT();
			faction.writeNBT(compound);
			Packets.sendServer(new SPacketFactionSave(compound));
        }
        if(button.id == 1){
        	if(data.containsKey(scrollFactions.getSelected())) {
        		Packets.sendServer(new SPacketFactionRemove(data.get(selected)));
        		scrollFactions.clear();
        		faction = new Faction();
        		init();
        	}
        }
        if(button.id == 2){
        	this.setSubGui(new SubGuiNpcFactionPoints(faction));
        }
        if(button.id == 3){
        	faction.hideFaction = button.getValue() == 1;
        }
        if(button.id == 4){
        	faction.getsAttacked = button.getValue() == 1;
        }
        if(button.id == 10){
        	this.setSubGui(new SubGuiColorSelector(faction.color));
        }
    }

	@Override
	public void setGuiData(CompoundNBT compound) {
		this.faction = new Faction();
		faction.readNBT(compound);
		
		setSelected(faction.name);
		init();
	}
	

	@Override
	public void setData(Vector<String> list, Map<String, Integer> data) {
		String name = scrollFactions.getSelected();
		this.data = data;
		scrollFactions.setList(list);
		
		if(name != null)
			scrollFactions.setSelected(name);
	}
    
	@Override
	public void setSelected(String selected) {
		this.selected = selected;
		scrollFactions.setSelected(selected);
	}
    
	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll guiCustomScroll) {
		if(guiCustomScroll.id == 0)
		{
			save();
			selected = scrollFactions.getSelected();
			Packets.sendServer(new SPacketFactionGet(data.get(selected)));
		}
		else if(guiCustomScroll.id == 1)
		{
			HashSet<Integer> set = new HashSet<Integer>();
			for(String s : guiCustomScroll.getSelectedList()){
				if(data.containsKey(s))
					set.add(data.get(s));
			}
			faction.attackFactions = set;
			save();
		}
	}
	
	public void save() {
		if(selected != null && data.containsKey(selected) && faction != null){
			CompoundNBT compound = new CompoundNBT();
			faction.writeNBT(compound);
    	
			Packets.sendServer(new SPacketFactionSave(compound));
		}
	}
		
	@Override
	public void unFocused(GuiTextFieldNop guiNpcTextField) {
		if(faction.id == -1) 
			return;
		
		if(guiNpcTextField.id == 0) {
			String name = guiNpcTextField.getValue();
			if(!name.isEmpty() && !data.containsKey(name)){
				String old = faction.name;
				data.remove(faction.name);
				faction.name = name;
				data.put(faction.name, faction.id);
				selected = name;
				scrollFactions.replace(old,faction.name);
			}
		} else if(guiNpcTextField.id == 1) {
			int color = 0;
			try{
				color = Integer.parseInt(guiNpcTextField.getValue(),16);
			}
			catch(NumberFormatException e){
				color = 0;
			}
	    	faction.color = color;
	    	guiNpcTextField.setTextColor(faction.color);
		} 
		
	}

	@Override
	public void subGuiClosed(Screen subgui) {
		if(subgui instanceof SubGuiColorSelector){
	    	faction.color = ((SubGuiColorSelector)subgui).color;
	    	init();
		}
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}

}
