package noppes.npcs.client.gui.model;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.ModelData;
import noppes.npcs.client.controllers.Preset;
import noppes.npcs.client.controllers.PresetController;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;

public class GuiPresetSave extends GuiBasic {
	private final ModelData data;
	private final Screen parent;
	
	public GuiPresetSave(Screen parent, ModelData data){
		this.data = data;
		this.parent = parent;
		imageWidth = 200;
		this.drawDefaultBackground = true;
	}
	@Override
	public void init(){
		super.init();
		this.addTextField(new GuiTextFieldNop(0, this, guiLeft, guiTop + 70,200, 20, ""));
		this.addButton(new GuiButtonNop(this, 0,guiLeft, guiTop + 100,98, 20, "Save"));
		this.addButton(new GuiButtonNop(this, 1,guiLeft + 100, guiTop + 100, 98, 20, "Cancel"));
	}
    @Override
    public void buttonEvent(GuiButtonNop btn) {
    	GuiButtonNop button = btn;
    	if(button.id == 0){
    		String name = this.getTextField(0).getValue().trim();
    		if(name.isEmpty())
    			return;
    		Preset preset = new Preset();
    		preset.name = name;
    		preset.data = data.copy();
    		PresetController.instance.addPreset(preset);
    	}
    	close();
    }
}
