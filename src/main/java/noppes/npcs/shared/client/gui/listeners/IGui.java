package noppes.npcs.shared.client.gui.listeners;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IGui {

	public int getID();
	
	public void render(MatrixStack matrixStack, int xMouse, int yMouse);

	public void tick();
	
	public boolean isActive();
}
