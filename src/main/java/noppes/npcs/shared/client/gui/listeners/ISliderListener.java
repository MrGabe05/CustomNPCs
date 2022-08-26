package noppes.npcs.shared.client.gui.listeners;


import noppes.npcs.shared.client.gui.components.GuiSliderNop;

public interface ISliderListener {

	void mouseDragged(GuiSliderNop guiNpcSlider);
	
	void mousePressed(GuiSliderNop guiNpcSlider);
	
	void mouseReleased(GuiSliderNop guiNpcSlider);
	
}
