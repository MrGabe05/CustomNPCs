package noppes.npcs.shared.client.gui.listeners;

public interface IMouseListener {
	public boolean mouseClicked(double xMouse, double yMouse, int mouseButton);

	public boolean mouseScrolled(double scrolled);
}
