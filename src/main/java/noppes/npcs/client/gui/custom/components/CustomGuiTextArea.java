package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiTextAreaWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.shared.client.gui.components.GuiTextArea;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiTextUpdate;

public class CustomGuiTextArea extends GuiTextArea implements IGuiComponent {

    GuiCustom parent;

    CustomGuiTextFieldWrapper component;

    public CustomGuiTextArea(int id, int x, int y, int width, int height) {
        super(id, GuiCustom.guiLeft+x, GuiCustom.guiTop+y, width, height, "");
    }

    @Override
    public void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.pushPose();
        matrixStack.translate(0, 0, id);
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        super.render(matrixStack, mouseX, mouseY);
        if(hovered && component.hasHoverText()) {
            this.parent.hoverText = component.getHoverTextList();
        }
        matrixStack.popPose();
    }

    public void setParent(GuiCustom parent) {
        this.parent = parent;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        String text = getText();
        boolean bo = super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        if(!text.equals(getText())){
            component.setText(getText());
            Packets.sendServer(new SPacketCustomGuiTextUpdate(id, component.toNBT(new CompoundNBT())));
        }
        return bo;
    }

    @Override
    public boolean charTyped(char c, int i) {
        String text = getText();
        boolean bo = super.charTyped(c, i);
        if(!text.equals(getText())){
            component.setText(getText());
            Packets.sendServer(new SPacketCustomGuiTextUpdate(id, component.toNBT(new CompoundNBT())));
        }
        return bo;
    }

    @Override
    public ICustomGuiComponent toComponent() {
        component.setText(getText());
        return component;
    }

    public static CustomGuiTextArea fromComponent(CustomGuiTextAreaWrapper component) {
        CustomGuiTextArea txt = new CustomGuiTextArea(component.getID(), component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight());
        txt.component = component;

        if(component.getText()!=null && !component.getText().isEmpty())
            txt.text = component.getText();
        txt.enabled = component.getEnabled();
        return txt;
    }
}
