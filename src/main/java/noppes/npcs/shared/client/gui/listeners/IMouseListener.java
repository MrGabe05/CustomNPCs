package noppes.npcs.shared.client.gui.listeners;

public interface IMouseListener {
	boolean mouseClicked(double xMouse, double yMouse, int mouseButton);

	boolean mouseScrolled(double scrolled);
}
