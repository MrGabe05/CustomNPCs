package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class GuiHoverText extends Screen{
	private int x, y;
	public int id;

    protected static final ResourceLocation buttonTextures = new ResourceLocation("customnpcs:textures/gui/info.png");
	private String text;
	public GuiHoverText(int id, String text, int x, int y){
		super(null);
		this.text = text;
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        minecraft.getTextureManager().bind(buttonTextures);
        this.blit(matrixStack, this.x, this.y, 0, 0, 12, 12);
        
        if(inArea(x, y, 12, 12, mouseX, mouseY)){
	        List<TranslationTextComponent> lines = new ArrayList<TranslationTextComponent>();
	        lines.add(new TranslationTextComponent(text));
			GuiUtils.drawHoveringText(matrixStack, lines, x + 8, y + 6, width, height, -1, this.font);
	        //RenderSystem.disableLighting();;
        }
    }
	public boolean inArea(int x, int y, int width, int height, int mouseX, int mouseY){
		if(mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height)
			return false;
		return true;
	}
}
