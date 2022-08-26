package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiLabelWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.shared.client.gui.components.GuiLabel;

import java.util.List;

public class CustomGuiLabel extends GuiLabel implements IGuiComponent {
    GuiCustom parent;
    String fullLabel;
    int colour;

    List<TranslationTextComponent> hoverText;
    float scale = 1.0f;

    List<IReorderingProcessor> labels;

    public CustomGuiLabel(String label, int id, int x, int y, int width, int height, int colour) {
        super(id, new TranslationTextComponent(label), colour, x, y, width, height);
        this.x = GuiCustom.guiLeft + x;
        this.y = GuiCustom.guiTop + y;
        this.width = width;
        this.height = height;
        this.fullLabel = label;
        this.colour = colour;
        FontRenderer font = Minecraft.getInstance().font;
        labels = font.split(new TranslationTextComponent(label), width);
    }

    public void setParent(GuiCustom parent) {
        this.parent = parent;
    }

    @Override
    public void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.pushPose();
        matrixStack.translate(0, 0, id);
        matrixStack.scale(scale, scale, 0);
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        render(matrixStack, mouseX, mouseY, partialTicks);
        if(hovered && this.hoverText!=null && this.hoverText.size() > 0) {
            this.parent.hoverText = hoverText;
        }
        matrixStack.popPose();
    }

    @Override
    public int getID() {
        return id;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public static CustomGuiLabel fromComponent(CustomGuiLabelWrapper component) {
        CustomGuiLabel lbl = new CustomGuiLabel(component.getText(), component.getID(), component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), component.getColor());

        lbl.setScale(component.getScale());

        if(component.hasHoverText())
            lbl.hoverText = component.getHoverTextList();

        return lbl;
    }

    @Override
    public ICustomGuiComponent toComponent() {
        CustomGuiLabelWrapper component = new CustomGuiLabelWrapper(id, fullLabel, x, y, width, height, colour);
        component.setHoverText(hoverText);
        return component;
    }

}
