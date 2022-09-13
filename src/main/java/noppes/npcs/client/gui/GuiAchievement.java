package noppes.npcs.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class GuiAchievement implements IToast{
    private final String title;
    private final String subtitle;
    private final int type;
    
    private long firstDrawTime;
    private boolean newDisplay;

    public GuiAchievement(ITextComponent titleComponent, ITextComponent subtitleComponent, int type) {
        this.title = titleComponent.getString();
        this.subtitle = subtitleComponent == null ? null : subtitleComponent.getString();
        this.type = type;
    }

    @Override
    public IToast.Visibility render(MatrixStack matrixStack, ToastGui toastGui, long delta) {
        if (this.newDisplay) {
            this.firstDrawTime = delta;
            this.newDisplay = false;
        }

        toastGui.getMinecraft().getTextureManager().bind(TEXTURE);
        toastGui.blit(matrixStack, 0, 0, 0, 32 * type, 160, 32);
        
        int color1 = -256;
        int color2 = -1;
        
        if(type == 1 || type == 3) {
        	color1 = -11534256;
        	color2 = -16777216;
        }

        toastGui.getMinecraft().font.draw(matrixStack, this.title, 18, 7, color1);
        toastGui.getMinecraft().font.draw(matrixStack, this.subtitle, 18, 18, color2);

        return delta - this.firstDrawTime < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }
}