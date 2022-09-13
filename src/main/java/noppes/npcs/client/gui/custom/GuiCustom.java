package noppes.npcs.client.gui.custom;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.*;
import noppes.npcs.client.gui.custom.components.*;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiScrollClick;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiCustom extends ContainerScreen<ContainerCustomGui> implements ICustomScrollListener, IGuiData {

    CustomGuiWrapper gui;
    int imageWidth, imageHeight;
    public static int guiLeft, guiTop;
    ResourceLocation background;

    public List<TranslationTextComponent> hoverText;

    Map<Integer, IGuiComponent> components = new HashMap<>();

    public GuiCustom(ContainerCustomGui container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void init() {
        super.init();
        if(gui != null) {
            guiLeft = (this.width - this.imageWidth) / 2;
            guiTop = (this.height - this.imageHeight) / 2;
            components.clear();
            for (ICustomGuiComponent c : gui.getComponents()) {
                addComponent(c);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        for(IGuiComponent component : components.values()) {
            if(component instanceof TextFieldWidget)
                ((TextFieldWidget) component).tick();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        hoverText = null;
        renderBackground(matrixStack);
        if(background!=null) {
            drawBackgroundTexture(matrixStack);
        }
        for(IGuiComponent component : components.values()) {
            component.onRender(matrixStack, mouseX, mouseY, partialTicks);
        }
        if(hoverText!=null && !hoverText.isEmpty()) {
            GuiUtils.drawHoveringText(matrixStack, this.hoverText, mouseX, mouseY, width, height, -1, this.font);
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {

    }

    void drawBackgroundTexture(MatrixStack matrixStack) {
        minecraft.getTextureManager().bind(background);
        blit(matrixStack, guiLeft, guiTop, 0,0, imageWidth, imageHeight);
    }

    private void addComponent(ICustomGuiComponent component) {
        CustomGuiComponentWrapper c = (CustomGuiComponentWrapper)component;
        switch (c.getType()) {
            case GuiComponentType.BUTTON:
                CustomGuiButton button = CustomGuiButton.fromComponent((CustomGuiButtonWrapper) component);
                button.setParent(this);
                this.components.put(button.getID(), button);
                break;
            case GuiComponentType.LABEL:
                CustomGuiLabel lbl = CustomGuiLabel.fromComponent((CustomGuiLabelWrapper)component);
                lbl.setParent(this);
                this.components.put(lbl.getID(), lbl);
                break;
            case GuiComponentType.TEXT_FIELD:
                CustomGuiTextField textField = CustomGuiTextField.fromComponent((CustomGuiTextFieldWrapper)component);
                textField.setParent(this);
                this.components.put(textField.id, textField);
                break;
            case GuiComponentType.TEXT_AREA:
                CustomGuiTextArea textArea = CustomGuiTextArea.fromComponent((CustomGuiTextAreaWrapper)component);
                textArea.setParent(this);
                this.components.put(textArea.id, textArea);
                break;
            case GuiComponentType.TEXTURED_RECT:
                CustomGuiTexturedRect rect = CustomGuiTexturedRect.fromComponent((CustomGuiTexturedRectWrapper)component);
                rect.setParent(this);
                this.components.put(rect.getID(), rect);
                break;
            case GuiComponentType.SCROLL:
                CustomGuiScrollComponent scroll = new CustomGuiScrollComponent(this, (CustomGuiScrollWrapper) component);
                scroll.setParent(this);
                this.components.put(scroll.getID(), scroll);
                break;
        }
    }

    @Override
    public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
        Packets.sendServer(new SPacketCustomGuiScrollClick(scroll.id, scroll.getSelectedIndex(), false, getScrollSelection((CustomGuiScrollComponent)scroll)));
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
        Packets.sendServer(new SPacketCustomGuiScrollClick(scroll.id, scroll.getSelectedIndex(), true, getScrollSelection((CustomGuiScrollComponent)scroll)));
    }

    CompoundNBT getScrollSelection(CustomGuiScrollComponent scroll) {
        ListNBT list = new ListNBT();
        if (scroll.multipleSelection) {
            for (String s : scroll.getSelectedList()) {
                list.add(StringNBT.valueOf(s));
            }
        } else {
            list.add(StringNBT.valueOf(scroll.getSelected()));
        }
        CompoundNBT selection = new CompoundNBT();
        selection.put("selection", list);
        return selection;
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        for(IGuiComponent comp : components.values()){
            if(comp instanceof IGuiEventListener){
                ((IGuiEventListener)comp).charTyped(typedChar, keyCode);
            }
        }
        return super.charTyped(typedChar, keyCode);
    }

    @Override
    public boolean keyPressed(int key, int p_keyPressed_2_, int p_keyPressed_3_) {
        for(IGuiComponent comp : components.values()){
            if(comp instanceof IGuiEventListener){
                ((IGuiEventListener)comp).keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
            }
        }
        if(this.minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(key, p_keyPressed_2_))){
            return true;
        }
        return super.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)  {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for(IGuiComponent comp : components.values()){
            if(comp instanceof IGuiEventListener){
                ((IGuiEventListener)comp).mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        if(gui!=null)
            return gui.getDoesPauseGame();
        return true;
    }

	@Override
	public void setGuiData(CompoundNBT compound) {
		Minecraft mc = Minecraft.getInstance();
		CustomGuiWrapper gui = (CustomGuiWrapper) new CustomGuiWrapper().fromNBT(compound);
		menu.setGui(gui, mc.player);
        this.gui = gui;
        imageWidth = gui.getWidth();
        imageHeight = gui.getHeight();
        if(!gui.getBackgroundTexture().isEmpty())
            background = new ResourceLocation(gui.getBackgroundTexture());
        init();
	}

}
