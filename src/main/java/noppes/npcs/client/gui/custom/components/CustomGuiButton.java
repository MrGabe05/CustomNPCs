package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomGuiButton extends Button implements IGuiComponent {

    GuiCustom parent;
    ResourceLocation texture;

    public int textureX,textureY;
    boolean hovered;

    String label;
    int colour = 0xffffff;
    List<TranslationTextComponent> hoverText;

    public int id;

    public CustomGuiButton(int buttonId, String buttonText, int x, int y, int width, int height, CustomGuiButtonWrapper component) {
        super(GuiCustom.guiLeft+x, GuiCustom.guiTop+y, width, height, new TranslationTextComponent(buttonText), (button) -> {
                Packets.sendServer(new SPacketCustomGuiButton(buttonId));
        });
        this.id = buttonId;
        if(component.hasTexture()) {
            this.textureX = component.getTextureX();
            this.textureY = component.getTextureY();
            this.texture = new ResourceLocation(component.getTexture());
        }
        this.label = buttonText;
    }


    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        return false;
    }

    public void setParent(GuiCustom parent) {
        this.parent = parent;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.pushPose();
        matrixStack.translate(0, 0, id);
        Minecraft mc = Minecraft.getInstance();
        FontRenderer font = mc.font;
        if(texture==null) {
            mc.getTextureManager().bind(WIDGETS_LOCATION);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getYImage(this.hovered);
            //RenderSystem.enableBlend();
            //RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            //RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.blit(matrixStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.blit(matrixStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.renderBg(matrixStack, mc, mouseX, mouseY);
            int j = 14737632;

            if (colour != 0)
            {
                j = colour;
            }
            else
            if (!this.active)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }
            matrixStack.translate(0, 0, .1f);
            this.drawCenteredString(matrixStack, font, this.label, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);

            if(hovered && this.hoverText!=null && this.hoverText.size() > 0) {
                this.parent.hoverText = hoverText;
            }
        }
        else {
            mc.getTextureManager().bind(texture);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.hoverState(this.hovered);
            //RenderSystem.enableBlend();
            //RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            //RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            this.blit(matrixStack, this.x, this.y, this.textureX, this.textureY + i * this.height, this.width, this.height);

            this.drawCenteredString(matrixStack, font, this.label, this.x + this.width / 2, this.y + (this.height - 8) / 2, this.colour);

            if(hovered && this.hoverText!=null && this.hoverText.size() > 0) {
                this.parent.hoverText = hoverText;
            }
        }
        matrixStack.popPose();
    }

    @Override
    public ICustomGuiComponent toComponent() {
        CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y, width, height, texture.toString(), textureX, textureY);
        component.setHoverText(hoverText);
        component.setEnabled(active);
        return component;
    }

    public static CustomGuiButton fromComponent(CustomGuiButtonWrapper component) {
        CustomGuiButton btn;
        if (component.getWidth() >= 0 && component.getHeight() >= 0)
            btn = new CustomGuiButton(component.getID(), component.getLabel(), component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), component);
        else
            btn = new CustomGuiButton(component.getID(), component.getLabel(), component.getPosX(), component.getPosY(), 200, 20, component);

        if(component.hasHoverText()) {
            btn.hoverText = component.getHoverTextList();
        }
        btn.active = component.getEnabled();
        return btn;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    protected int hoverState(boolean mouseOver)
    {
        int i = 0;

        if (mouseOver)
        {
            i = 1;
        }

        return i;
    }

}
