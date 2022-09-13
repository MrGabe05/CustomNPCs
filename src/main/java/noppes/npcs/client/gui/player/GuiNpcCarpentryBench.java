package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.containers.ContainerCarpentryBench;
import noppes.npcs.controllers.RecipeController;

public class GuiNpcCarpentryBench extends GuiContainerNPCInterface<ContainerCarpentryBench> {
	private final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/carpentry.png");
    private final ContainerCarpentryBench container;
    private GuiButtonNop button;
    
    public GuiNpcCarpentryBench(ContainerCarpentryBench container, PlayerInventory inv, ITextComponent titleIn) {
        super(null, container, inv, titleIn);
        this.container = container;
        this.title = "";
        passEvents = false;//passEvents
        imageHeight = 180;
    }

    @Override
    public void init(){
    	super.init();
    	addButton(button = new GuiButtonNop(this, 0, guiLeft + 158, guiTop + 4, 12, 20, "..."));
    }

    public void buttonEvent(GuiButtonNop guibutton){
    	setScreen(new GuiRecipes());
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
    	button.active = RecipeController.instance != null && !RecipeController.instance.anvilRecipes.isEmpty();
    	RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(resource);
        int l = (width - imageWidth) / 2;
        int i1 = (height - imageHeight) / 2;
        String title = I18n.get("block.customnpcs.npccarpentybench");
        blit(matrixStack, l, i1, 0, 0, imageWidth, imageHeight);

      	font.draw(matrixStack, title, guiLeft + 4 , guiTop + 4 , CustomNpcResourceListener.DefaultTextColor);
        font.draw(matrixStack, I18n.get("container.inventory"), guiLeft + 4, guiTop + 87, CustomNpcResourceListener.DefaultTextColor);
    }

	@Override
	public void save() {
		return;
	}
}
