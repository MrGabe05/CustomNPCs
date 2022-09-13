package noppes.npcs.client.gui;

import noppes.npcs.Resistances;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiSliderNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ISliderListener;

public class SubGuiNpcResistanceProperties extends GuiBasic implements ISliderListener {
	private final Resistances resistances;
    public SubGuiNpcResistanceProperties(Resistances resistances) {
    	this.resistances = resistances;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

	@Override
    public void init() {
        super.init();
        addLabel(new GuiLabel(0,"enchantment.minecraft.knockback", guiLeft + 4, guiTop + 15));
        addSlider(new GuiSliderNop(this, 0, guiLeft + 94, guiTop + 10, (int)(resistances.knockback * 100 - 100)  + "%", resistances.knockback / 2));

        addLabel(new GuiLabel(1,"item.minecraft.arrow", guiLeft + 4, guiTop + 37));
        addSlider(new GuiSliderNop(this, 1, guiLeft + 94, guiTop + 32, (int)(resistances.arrow * 100 - 100)  + "%", resistances.arrow / 2));

        addLabel(new GuiLabel(2,"stats.melee", guiLeft + 4, guiTop + 59));
        addSlider(new GuiSliderNop(this, 2, guiLeft + 94, guiTop + 54, (int)(resistances.melee * 100 - 100)  + "%", resistances.melee / 2));

        addLabel(new GuiLabel(3,"stats.explosion", guiLeft + 4, guiTop + 81));
        addSlider(new GuiSliderNop(this, 3, guiLeft + 94, guiTop + 76, (int)(resistances.explosion * 100 - 100)  + "%", resistances.explosion / 2));

    	addButton(new GuiButtonNop(this, 66, guiLeft + 190, guiTop + 190, 60, 20, "gui.done"));
    }

	@Override
	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
        if(id == 66) {
        	close();
        }
    }

	@Override
	public void mouseDragged(GuiSliderNop slider) {
		slider.setString((int)(slider.sliderValue * 200 - 100) + "%");
	}

	@Override
	public void mousePressed(GuiSliderNop slider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(GuiSliderNop slider) {
		if(slider.id == 0){
			resistances.knockback = slider.sliderValue * 2;
		}
		if(slider.id == 1){
			resistances.arrow = slider.sliderValue * 2;
		}
		if(slider.id == 2){
			resistances.melee = slider.sliderValue * 2;
		}
		if(slider.id == 3){
			resistances.explosion = slider.sliderValue * 2;
		}
			
	}

}
