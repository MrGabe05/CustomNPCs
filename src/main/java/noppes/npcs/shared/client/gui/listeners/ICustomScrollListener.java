package noppes.npcs.shared.client.gui.listeners;


import noppes.npcs.shared.client.gui.components.GuiCustomScroll;

public interface ICustomScrollListener {

	void scrollClicked(double i, double j, int k, GuiCustomScroll scroll);
	
	void scrollDoubleClicked(String selection, GuiCustomScroll scroll);

}
