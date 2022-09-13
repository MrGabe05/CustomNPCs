package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;

import java.util.Arrays;
import java.util.List;

public class CustomGuiTexturedRect extends AbstractGui implements IGuiComponent {

    GuiCustom parent;
    ResourceLocation texture;

    int id,x,y,width,height,textureX,textureY;
    float scale = 1.0f;
    List<TranslationTextComponent> hoverText;

    public CustomGuiTexturedRect(int id, String texture, int x, int y, int width, int height) {
        this(id, texture, x, y, width,height,0,0);
    }

    public CustomGuiTexturedRect(int id, String texture, int x, int y, int width, int height, int textureX, int textureY) {
        this.id = id;
        this.texture = new ResourceLocation(texture);
        this.x = GuiCustom.guiLeft+x;
        this.y = GuiCustom.guiTop+y;
        this.width = width;
        this.height = height;
        this.textureX = textureX;
        this.textureY = textureY;
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
        Minecraft mc = Minecraft.getInstance();
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        matrixStack.pushPose();
        mc.getTextureManager().bind(texture);
        //blit(matrixStack, x, y, textureX, textureY, width, height);

        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.vertex(x, y + height*scale, this.id).uv(textureX * 0.00390625F, (textureY + height) * 0.00390625F).endVertex();
        bufferbuilder.vertex(x + width*scale, (y + height*scale), this.id).uv((textureX + width) * 0.00390625F, (textureY + height) * 0.00390625F).endVertex();
        bufferbuilder.vertex(x + width*scale, y, this.id).uv((textureX + width) * 0.00390625F, textureY * 0.00390625F).endVertex();
        bufferbuilder.vertex(x, y, this.id).uv(textureX * 0.00390625F, textureY * 0.00390625F).endVertex();
        bufferbuilder.end();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.end(bufferbuilder);
        if(hovered && this.hoverText!=null && this.hoverText.size() > 0) {
            this.parent.hoverText = hoverText;
        }
        matrixStack.popPose();
    }

    @Override
    public ICustomGuiComponent toComponent() {
        CustomGuiTexturedRectWrapper component = new CustomGuiTexturedRectWrapper(id, texture.toString(), x, y, width, height, textureX, textureY);
        component.setHoverText(hoverText);
        component.setScale(scale);
        return component;
    }

    public static CustomGuiTexturedRect fromComponent(CustomGuiTexturedRectWrapper component) {
        CustomGuiTexturedRect rect;

        if(component.getTextureX()>=0 && component.getTextureY()>=0)
            rect = new CustomGuiTexturedRect(component.getID(), component.getTexture(), component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), component.getTextureX(), component.getTextureY());
        else
            rect = new CustomGuiTexturedRect(component.getID(), component.getTexture(), component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight());

        rect.scale = component.getScale();

        if(component.hasHoverText())
            rect.hoverText = component.getHoverTextList();

        return rect;
    }

}
