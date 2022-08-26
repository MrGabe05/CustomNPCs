package noppes.npcs.client.gui.script;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class GuiScriptGlobal extends GuiNPCInterface {

	private final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/smallbg.png");

	public GuiScriptGlobal() {
		super();
        imageWidth = 176;
        imageHeight = 222;
        this.drawDefaultBackground = false;
        title = "";
	}
	
    @Override
    public void init(){
        super.init();

        this.addButton(new GuiButtonNop(this, 0, guiLeft + 38, guiTop + 20, 100, 20, "Players"));
        this.addButton(new GuiButtonNop(this, 1, guiLeft + 38, guiTop + 50, 100, 20, "Forge"));
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    	renderBackground(matrixStack);
    	
    	RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(resource);
        blit(matrixStack, guiLeft, guiTop, 0, 0, imageWidth, imageHeight);
        
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		if(guibutton.id == 0){
			setScreen(new GuiScriptPlayers());
		}
		if(guibutton.id == 1){
			setScreen(new GuiScriptForge());
		}
    }

	@Override
	public void save() {
		
	}

}
