package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiButtonNextPage extends GuiButtonNop {
    private final boolean isLeftButton;
    private static final String __OBFID = "CL_00000745";
    private static final ResourceLocation field_110405_a = new ResourceLocation("textures/gui/book.png");

    public GuiButtonNextPage(IGuiInterface gui, int id, int x, int y, boolean par4, IPressable press) {
        super(gui, id, x, y, 23, 13, "", press);
        this.isLeftButton = par4;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        if (this.visible) {
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            Minecraft mc = Minecraft.getInstance();
            mc.getTextureManager().bind(field_110405_a);
            int k = 0;
            int l = 192;

            if (flag)
            {
                k += 23;
            }

            if (!this.isLeftButton)
            {
                l += 13;
            }

            this.blit(matrixStack, this.x, this.y, k, l, 23, 13);
        }
    }
}
