package noppes.npcs.client.gui.model;

import java.util.ArrayList;
import java.util.List;

import noppes.npcs.client.controllers.Preset;
import noppes.npcs.client.controllers.PresetController;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiCreationLoad extends GuiCreationScreenInterface implements ICustomScrollListener{

	private final List<String> list = new ArrayList<String>();
	private GuiCustomScroll scroll;
	
	public GuiCreationLoad(EntityNPCInterface npc){
		super(npc);
		active = 5;
		xOffset = 60;
		PresetController.instance.load();
	}

    @Override
    public void init() {
    	super.init();
    	if(scroll == null){
    		scroll = new GuiCustomScroll(this, 0);
    	}
    	list.clear();
        for(Preset preset : PresetController.instance.presets.values())
        	list.add(preset.name);
		scroll.setList(list);
    	scroll.guiLeft = guiLeft;
    	scroll.guiTop = guiTop + 45;
    	scroll.setSize(100, imageHeight - 96);
    	
    	addScroll(scroll);
    	
    	addButton(new GuiButtonNop(this, 10, guiLeft, guiTop + imageHeight - 46, 120, 20, "gui.remove"));
    }

    @Override
    public void buttonEvent(GuiButtonNop btn) {

    	if(btn.id == 10 && scroll.hasSelected()){
    		PresetController.instance.removePreset(scroll.getSelected());
    		init();
    	}
    }

	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
    	Preset preset = PresetController.instance.getPreset(scroll.getSelected());
    	playerdata.load(preset.data.save());
		init();
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
