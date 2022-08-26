package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiColorButton extends GuiButtonNop {
	public int color;
	public GuiColorButton(IGuiInterface gui, int id, int x, int y, int color) {
		super(gui, id, x, y, 50, 20, "");
		this.color = color;
	}
	
	@Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        if (!this.visible)
        	return;
        fill(matrixStack,x, y, x + 50, y + 20, 0xFF000000 + color);
    }

}
