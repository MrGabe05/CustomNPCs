package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiButtonBiDirectional extends GuiButtonNop {
	public static final ResourceLocation resource = new ResourceLocation(CustomNpcs.MODID, "textures/gui/arrowbuttons.png");
	
	private int color = 0xffffff;

    public GuiButtonBiDirectional(IGuiInterface gui, int id, int x, int y, int width, int height, String[] arr, int current) {
        super(gui, id, x, y, width, height, arr, current);
    }

    public GuiButtonBiDirectional(IGuiInterface gui, int id, int x, int y, int width, int height, int current, String... arr) {
        super(gui, id, x, y, width, height, arr, current);
    }
	
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        if (!this.visible)
        	return;

        boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        
        boolean hoverL = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + 14 && mouseY < this.y + this.height;

        boolean hoverR = !hoverL && mouseX >= this.x + width - 14 && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bind(resource);

        RenderSystem.color4f(1, 1, 1, 1);

        this.blit(matrixStack, this.x, this.y, 0, hoverL?40:20, 11, 20);
        this.blit(matrixStack, this.x + width - 11, this.y, 11, hover && !hoverL || hoverR?40:20, 11, 20);
        
        int l = color;
        if (packedFGColor != 0){
            l = packedFGColor;
        }
        else if (!this.active){
            l = 10526880;
        }
        else if (hover){
            l = 16777120;
        }
        String text = "";
        float maxWidth = this.width - 36;
        String displayString = getMessage().getString();
        if(mc.font.width(displayString) > maxWidth){
        	for(int h = 0; h < displayString.length(); h++){
        		char c = displayString.charAt(h);
        		text += c;
        		if(mc.font.width(text) > maxWidth)
        			break;
        	}
        	text += "...";
        }
        else
        	text = displayString;
        if(hover)
        	text = (char)167 + "n" + text;
        
        this.drawCenteredString(matrixStack, mc.font, text, this.x + this.width / 2, this.y + (this.height - 8) / 2, l);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
    	int value = getValue();
    	if(this.isMouseOver(mouseX, mouseY) && display != null && display.length != 0){
            boolean hoverL = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + 14 && mouseY < this.y + this.height;

            boolean hoverR = !hoverL && mouseX >= this.x + 14 && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            if(hoverR)
            	value = (value+1) % display.length;
            if(hoverL){
            	if(value <= 0)
            		value = display.length;
            	value--;
            }
    		this.setDisplay(value);
    	}
    	return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public void onClick(double x, double y){
        if(gui.hasSubGui())
            return;
        gui.buttonEvent(this);
    }
}
