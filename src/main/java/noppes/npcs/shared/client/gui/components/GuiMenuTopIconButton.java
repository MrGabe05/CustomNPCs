package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.gui.GuiUtils;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

import java.util.Arrays;
import java.util.Collections;

public class GuiMenuTopIconButton extends GuiMenuTopButton{
    private static final ResourceLocation resource = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    protected static ItemRenderer itemRenderer;
    private ItemStack item;
    
	public GuiMenuTopIconButton(IGuiInterface gui, int i, int x, int y, String s, ItemStack item) {
		super(gui, i, x, y, s);
		width = 28;
		height = 28;
		this.item = item;
		itemRenderer = Minecraft.getInstance().getItemRenderer();
	}
	
    public GuiMenuTopIconButton(IGuiInterface gui, int i, GuiButtonNop parent, String s, ItemStack item){
    	super(gui, i, parent, s);
		width = 28;
		height = 28;
		this.item = item;
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

	@Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible){
            return;
        }
        if(item.isEmpty())
        	item = new ItemStack(Blocks.DIRT);
        hover = mouseX >= x && mouseY >= y && mouseX < x + getWidth() && mouseY < y + height;
        Minecraft mc = Minecraft.getInstance();
        if(hover){
            GuiUtils.drawHoveringText(matrixStack, Collections.singletonList(getMessage()), mouseX, mouseY, gui.getWidth(), gui.getHeight(), -1, Minecraft.getInstance().font);
        }
        mc.getTextureManager().bind(resource);
        matrixStack.pushPose();
        //RenderSystem.enableBlend();
        //RenderSystem.disableLighting();
        this.blit(matrixStack, x, y + (active?2:0), 0, active?32:0, 28, 28);
        this.setBlitOffset(100);
        itemRenderer.blitOffset = 100.0F;
        //RenderSystem.enableLighting();
        //RenderSystem.enableRescaleNormal();
        //RenderHelper.enableGUIStandardItemLighting();
        itemRenderer.renderAndDecorateItem(item, x + 6, y + 10);
        itemRenderer.renderGuiItemDecorations(mc.font, item, x + 6, y + 10);
        ////RenderHelper.disableStandardItemLighting();
        //RenderSystem.disableLighting();
        itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
        matrixStack.popPose();
    }
}
