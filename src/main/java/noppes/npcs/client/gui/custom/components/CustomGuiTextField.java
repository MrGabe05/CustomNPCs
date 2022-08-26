package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiTextUpdate;

public class CustomGuiTextField extends TextFieldWidget implements IGuiComponent {

    GuiCustom parent;

    CustomGuiTextFieldWrapper component;

    public int id;

    public CustomGuiTextField(CustomGuiTextFieldWrapper component) {
        super(Minecraft.getInstance().font, GuiCustom.guiLeft+component.getPosX(), GuiCustom.guiTop+component.getPosY(), component.getWidth(), component.getHeight(), new TranslationTextComponent(component.getText()));
        this.id = component.getID();

        setMaxLength(500);
        this.component = component;

        if(component.getText()!=null && !component.getText().isEmpty()) {
            setValue(component.getText());
        }
    }

    @Override
    public void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.pushPose();
        matrixStack.translate(0, 0, id);
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        this.renderButton(matrixStack, mouseX, mouseY, partialTicks);
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
        String text = getValue();
        boolean bo = super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        if(!text.equals(getValue())){
            component.setText(getValue());
            Packets.sendServer(new SPacketCustomGuiTextUpdate(id, component.toNBT(new CompoundNBT())));
        }
        return bo;
    }

    @Override
    public boolean charTyped(char c, int i) {
        String text = getValue();
        boolean bo = super.charTyped(c, i);
        if(!text.equals(getValue())){
            component.setText(getValue());
            Packets.sendServer(new SPacketCustomGuiTextUpdate(id, component.toNBT(new CompoundNBT())));
        }
        return bo;
    }

    @Override
    public ICustomGuiComponent toComponent() {
        component.setText(getValue());
        return component;
    }

    public static CustomGuiTextField fromComponent(CustomGuiTextFieldWrapper component) {
        CustomGuiTextField txt = new CustomGuiTextField(component);

        if(component.getText()!=null && !component.getText().isEmpty())
            txt.setValue(component.getText());
        txt.active = component.getEnabled();
        return txt;
    }
}
