package noppes.npcs.shared.client.gui.listeners;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IGui {

	int getID();
	
	void render(MatrixStack matrixStack, int xMouse, int yMouse);

	void tick();
	
	boolean isActive();
}
