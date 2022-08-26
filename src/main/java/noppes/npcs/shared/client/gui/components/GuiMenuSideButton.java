package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiMenuSideButton extends GuiButtonNop {
	public static final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/menusidebutton.png");

    public boolean active;

    public GuiMenuSideButton(IGuiInterface gui, int i, int j, int k, String s){
        this(gui, i, j, k, 200, 20, s);
    }

    public GuiMenuSideButton(IGuiInterface gui, int i, int j, int k, int l, int i1, String s){
    	super(gui, i, j, k, l, i1, s);
    }

    @Override
    public int getYImage(boolean flag){
        if (active)
            return 0;
        return 1;
    }

    @Override
    public void render(MatrixStack matrixStack, int i, int j, float partialTicks) {
        if (!visible){
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.font;
        minecraft.getTextureManager().bind(resource);
        int width = this.width + (active?2:0);
        isHovered = i >= x && j >= y && i < x + width && j < y + height;
        int k = getYImage(isHovered);
        blit(matrixStack, x, y, 0,  k * 22, width, height);
        //mouseDragged(i, j);
        
        String text = "";
        float maxWidth = width * 0.75f;
        String displayString = getMessage().getString();
        if(fontrenderer.width(displayString) > maxWidth){
        	for(int h = 0; h < displayString.length(); h++){
        		char c = displayString.charAt(h);
        		if(fontrenderer.width(text + c) > maxWidth)
        			break;
        		text += c;
        	}
        	text += "...";
        }
        else
        	text = displayString;
        if (active){
            drawCenteredString(matrixStack, fontrenderer, text, x + width / 2, y + (height - 8) / 2, 0xffffa0);
        }
        else if (isHovered){
            drawCenteredString(matrixStack, fontrenderer, text, x + width / 2, y + (height - 8) / 2, 0xffffa0);
        }
        else{
            drawCenteredString(matrixStack, fontrenderer, text, x + width / 2, y + (height - 8) / 2, 0xe0e0e0);
        }
    }

    @Override
    public boolean mouseClicked(double i, double j, int button){
        if(!active){
            return super.mouseClicked(i, j, button);
        }
        return false;
    }
}
