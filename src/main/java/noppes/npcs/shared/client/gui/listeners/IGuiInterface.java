package noppes.npcs.shared.client.gui.listeners;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public interface IGuiInterface {
	void buttonEvent(GuiButtonNop button);

	void save();

	boolean hasSubGui();

	Screen getSubGui();

	int getWidth();

	int getHeight();

	Screen getParent();
	void elementClicked();

	void subGuiClosed(Screen subgui);
}
