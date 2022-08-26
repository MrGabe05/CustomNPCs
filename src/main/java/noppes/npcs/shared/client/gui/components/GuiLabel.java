package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.CustomNpcResourceListener;

public class GuiLabel  extends Widget implements IGuiEventListener {
    public int id;
    private boolean centered = false;
    public boolean enabled = true;
    private boolean labelBgEnabled;
    private final int textColor;
    private int backColor;
    private int ulColor;
    private int brColor;
    private int border;

    public GuiLabel(int id, ITextComponent label, int color, int x, int y, int width, int height) {
        super(x, y, width, height, label);
        this.id = id;
        this.textColor = color;
    }

    public GuiLabel(int id, String s, int x, int y) {
        this(id, new TranslationTextComponent(s), CustomNpcResourceListener.DefaultTextColor, x, y, 40, 0);
    }

    public GuiLabel(int id, String s, int x, int y, int color) {
        this(id, new TranslationTextComponent(s), color, x, y, 40, 0);
    }

    public GuiLabel(int id, String s, int x, int y, int width, int height) {
        this(id, new TranslationTextComponent(s), CustomNpcResourceListener.DefaultTextColor, x, y, width, height);
        centered = true;
    }

    public GuiLabel(int id, String s, int x, int y, int color, int width, int height) {
        this(id, new TranslationTextComponent(s), color, x, y, width, height);
        centered = true;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick) {
        if (this.enabled) {
            //RenderSystem.enableBlend();
            //RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.drawBox(stack);
            int i = this.y + this.height / 2 + this.border / 2;

            if (this.centered) {
                Minecraft.getInstance().font.draw(stack, getMessage(), x + (width - Minecraft.getInstance().font.width(getMessage())) / 2f, y, textColor);
                //this.drawCenteredString(stack, Minecraft.getInstance().font, getMessage(), this.x + this.width / 2, i, this.textColor);
            } else {
                Minecraft.getInstance().font.draw(stack, getMessage(), x, y, textColor);
                //this.drawString(stack, Minecraft.getInstance().font, getMessage(), this.x, i, this.textColor);
            }

        }
    }

    protected void drawBox(MatrixStack stack) {
        if (this.labelBgEnabled) {
            int i = this.width + this.border * 2;
            int j = this.height + this.border * 2;
            int k = this.x - this.border;
            int l = this.y - this.border;
            fill(stack, k, l, k + i, l + j, this.backColor);
            this.hLine(stack, k, k + i, l, this.ulColor);
            this.hLine(stack, k, k + i, l + j, this.brColor);
            this.vLine(stack, k, l, l + j, this.ulColor);
            this.vLine(stack, k + i, l, l + j, this.brColor);
        }

    }
}
