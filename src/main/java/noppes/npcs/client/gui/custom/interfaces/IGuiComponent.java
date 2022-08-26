package noppes.npcs.client.gui.custom.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import noppes.npcs.api.gui.ICustomGuiComponent;

public interface IGuiComponent {

    int getID();
    void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);
    ICustomGuiComponent toComponent();

}
