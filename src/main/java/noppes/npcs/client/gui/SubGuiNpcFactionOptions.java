package noppes.npcs.client.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.minecraft.client.resources.I18n;
import noppes.npcs.controllers.data.FactionOptions;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketFactionsGet;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

public class SubGuiNpcFactionOptions extends GuiBasic implements IScrollData, ICustomScrollListener {
	private final FactionOptions options;
	private Map<String,Integer> data = new HashMap<String,Integer>();
	private GuiCustomScroll scrollFactions;
	
	private int selected = -1;
	
    public SubGuiNpcFactionOptions(FactionOptions options){
    	this.options = options;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
		Packets.sendServer(new SPacketFactionsGet());
    }

    @Override
    public void init(){
        super.init();
        if(scrollFactions == null){
	        scrollFactions = new GuiCustomScroll(this,0);
	        scrollFactions.setSize(120, 208);
        }
        scrollFactions.guiLeft = guiLeft + 130;
        scrollFactions.guiTop = guiTop + 4;
        addScroll(scrollFactions);

        addLabel(new GuiLabel(0, "1: ", guiLeft + 4,  guiTop + 12));
        if(data.containsValue(options.factionId)){
            addLabel(new GuiLabel(1, getFactionName(options.factionId), guiLeft + 12,  guiTop + 8));
            
            String label = "";
            if(options.decreaseFactionPoints)
            	label += I18n.get("gui.decrease");
            else
            	label += I18n.get("gui.increase");
            label += " " + options.factionPoints + " " + I18n.get("faction.points");

            addLabel(new GuiLabel(3, label, guiLeft + 12,  guiTop + 16));
            addButton(new GuiButtonNop(this, 0, guiLeft + 110, guiTop + 7, 20, 20, "X"));
        }

        addLabel(new GuiLabel(4, "2: ", guiLeft + 4,  guiTop + 40));
        if(data.containsValue(options.faction2Id)){
            addLabel(new GuiLabel(5, getFactionName(options.faction2Id), guiLeft + 12,  guiTop + 36));

            String label = "";
            if(options.decreaseFaction2Points)
            	label += I18n.get("gui.decrease");
            else
            	label += I18n.get("gui.increase");
            label += " " + options.faction2Points + " " + I18n.get("faction.points");

            addLabel(new GuiLabel(6, label, guiLeft + 12,  guiTop + 44));
            addButton(new GuiButtonNop(this, 1, guiLeft + 110, guiTop + 35, 20, 20, "X"));
        }

        
        if(selected >= 0 && (!data.containsValue(options.faction2Id) || !data.containsValue(options.factionId)) && !options.hasFaction(selected)){
            addButton(new GuiButtonNop(this, 2, guiLeft + 4, guiTop + 60, 90, 20, new String[]{"gui.increase","gui.decrease"},0));
            
            addTextField(new GuiTextFieldNop(1, this,  guiLeft + 4, guiTop + 82, 110, 20, "10"));
            getTextField(1).numbersOnly = true;
            getTextField(1).setMinMaxDefault(1, 100000, 10);
            
            addButton(new GuiButtonNop(this, 3, guiLeft + 4, guiTop + 104, 60, 20, "gui.add"));
        }

        addButton(new GuiButtonNop(this, 66, guiLeft + 20, guiTop + 192, 90, 20, "gui.done"));
    }
    private String getFactionName(int faction){
    	for(String s : data.keySet())
    		if(data.get(s) == faction)
    			return s;
    	return null;
    }

	@Override
	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
        if(id == 0)
        {
        	options.factionId = -1;
        	init();
        }
        if(id == 1)
        {
        	options.faction2Id = -1;
        	init();
        }
        if(id == 3)
        {
        	if(!data.containsValue(options.factionId)){
        		options.factionId = selected;
        		options.decreaseFactionPoints = getButton(2).getValue() == 1;
        		options.factionPoints = getTextField(1).getInteger();
        	}
        	else if(!data.containsValue(options.faction2Id)){
        		options.faction2Id = selected;
        		options.decreaseFaction2Points = getButton(2).getValue() == 1;
        		options.faction2Points = getTextField(1).getInteger();
        	}
        	init();
        }
        if(id == 66)
        {
        	close();
        }
    }

	@Override
	public void scrollClicked(double i, double j, int k,
			GuiCustomScroll guiCustomScroll) {
		selected = data.get(guiCustomScroll.getSelected());
		init();
	}

	@Override
	public void setData(Vector<String> list, Map<String, Integer> data) {
		GuiCustomScroll scroll = getScroll(0);
		String name = scroll.getSelected();
		this.data = data;
		scroll.setList(list);
		
		if(name != null)
			scroll.setSelected(name);
		
		init();
	}

	@Override
	public void setSelected(String selected) {
		
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}

}
