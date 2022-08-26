package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiMenuTopButton extends GuiButtonNop
{
	public static final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/menutopbutton.png");
    protected int height;
    public boolean active;
    public boolean hover = false;
    public boolean rotated = false;

    public GuiMenuTopButton(IGuiInterface gui, int i, int j, int k, String s) {
    	super(gui, i, j, k, s);
        active = false;

        width = Minecraft.getInstance().font.width(getMessage()) + 12;
        height = 20;
    }

    public GuiMenuTopButton(IGuiInterface gui, int i, GuiButtonNop parent, String s) {
    	this(gui, i, parent.x + parent.getWidth(), parent.y, s);
    }

    @Override
    public int getYImage(boolean flag) {
        byte byte0 = 1;
        if (active)
        {
            byte0 = 0;
        }
        else if (flag)
        {
            byte0 = 2;
        }
        return byte0;
    }

    @Override
    public void render(MatrixStack matrixStack, int i, int j, float partialTicks) {
        if (!visible) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        matrixStack.pushPose();
        mc.getTextureManager().bind(resource);
        int height = this.height - (active?0:2);
        hover = i >= x && j >= y && i < x + getWidth() && j < y + height;
        int k = getYImage(hover);
        blit(matrixStack, x, y, 0,  k * 20, getWidth() / 2, height);
        blit(matrixStack, x + getWidth() / 2, y, 200 - getWidth() / 2,  k * 20, getWidth() / 2, height);
        //mouseDragged(mc, i, j);
        FontRenderer fontrenderer = mc.font;
        if(rotated)
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        if (active)
        {
            drawCenteredString(matrixStack, fontrenderer, getMessage(), x + getWidth() / 2, y + (height - 8) / 2, 0xffffa0);
        }
        else if (hover)
        {
            drawCenteredString(matrixStack, fontrenderer, getMessage(), x + getWidth() / 2, y + (height - 8) / 2, 0xffffa0);
        }
        else
        {
            drawCenteredString(matrixStack, fontrenderer, getMessage(), x + getWidth() / 2, y + (height - 8) / 2, 0xe0e0e0);
        }
        matrixStack.popPose();
    }

	@Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        return false;
    }

    @Override
    public boolean mouseReleased(double i, double j, int button)
    {
        return false;
    }

    @Override
    public boolean mouseClicked(double i, double j, int button) {
    	boolean bo = !active && visible && hover;
    	if(bo){
            onClick(i, j);
    	}
        return bo;
    }

    @Override
    public void onClick(double x, double y) {
        gui.buttonEvent(this);
    }
}
