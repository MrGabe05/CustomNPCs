package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.ContainerEmpty;
import noppes.npcs.shared.client.gui.listeners.IGui;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class GuiBasicContainer<T extends Container> extends ContainerScreen<T> implements IGuiInterface {
    public boolean drawDefaultBackground = true;
    public int guiLeft, guiTop;
    public ClientPlayerEntity player;

    public GuiWrapper wrapper = new GuiWrapper(this);

    public String title;
    public boolean closeOnEsc = true;
    public int mouseX, mouseY;

    public GuiBasicContainer(T cont, PlayerInventory inv, ITextComponent titleIn) {
        super(cont, inv, titleIn);
        this.player = Minecraft.getInstance().player;
        title = "";

        this.minecraft = Minecraft.getInstance();
        this.itemRenderer = minecraft.getItemRenderer();
        this.font = minecraft.font;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return closeOnEsc;
    }

    @Override
    public void init() {
        super.init();
        setFocused(null);
        guiLeft = (width - imageWidth) / 2;
        guiTop = (height - imageHeight) / 2;
        buttons.clear();
        children.clear();

        wrapper.init(minecraft, width, height);
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
    }

    public ResourceLocation getResource(String texture) {
        return new ResourceLocation(CustomNpcs.MODID, "textures/gui/" + texture);
    }
//
//	@Override
//	public IGuiEventListener getFocused(){
//		return null;
//	}

    @Override
    public void tick() {
        wrapper.tick();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrolled) {
        if (wrapper.mouseScrolled(mouseX, mouseY, scrolled))
            return true;
        return super.mouseScrolled(mouseX, mouseY, scrolled);
    }

    @Override
    public boolean mouseClicked(double i, double j, int k) {
        if (wrapper.mouseClicked(i, j, k))
            return true;
        return super.mouseClicked(i, j, k);
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (wrapper.mouseDragged(x, y, button, dx, dy))
            return true;
        if (this.getFocused() != null && this.isDragging() && button == 0) {
            this.getFocused().mouseDragged(x, y, button, dx, dy);
            return true;
        }
        return super.mouseDragged(x, y, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (wrapper.mouseReleased(x, y, button))
            return true;
        return super.mouseReleased(x, y, button);
    }

    @Override
    public void elementClicked() {
        if (wrapper.subgui != null){
            ((IGuiInterface)wrapper.subgui).elementClicked();
        }
    }

    @Override
    public void subGuiClosed(Screen subgui) {

    }

    public boolean isInventoryKey(int i) {
        return minecraft.options.keyInventory.getKey().getValue() == i; //inventory key
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (wrapper.charTyped(c, i)) {
            return true;
        }
        return super.charTyped(c, i);
    }

    @Override
    public boolean keyPressed(int key, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (wrapper.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_))
            return true;
//		InputMappings.Input mouseKey = InputMappings.getKey(key, p_keyPressed_2_);
//		if(this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)){
//			return true;
//		}
        return super.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener gui) {
        if (wrapper.subgui != null) {
            wrapper.subgui.setFocused(gui);
        } else {
            if (gui != null && !this.children.contains(gui)) {
                return;
            }
            wrapper.changeFocus(getFocused(), gui);
            super.setFocused(gui);
        }
    }

    @Override
    public IGuiEventListener getFocused() {
        if (wrapper.subgui != null) {
            return wrapper.subgui.getFocused();
        }
        return super.getFocused();
    }

    public void buttonEvent(Button guibutton) {
    }

    public void close() {
        save();
        player.closeContainer();
        setScreen(null);
        minecraft.mouseHandler.grabMouse();
    }

    @Override
    public void onClose() {
        this.close();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        GuiTextFieldNop.unfocus();
    }

    public void addExtra(GuiHoverText gui) {
        wrapper.addExtra(gui);
    }

    public void addButton(GuiButtonNop button) {
        wrapper.npcbuttons.put(button.id, button);
        super.addButton(button);
    }

    public void addTopButton(GuiMenuTopButton button) {
        wrapper.topbuttons.put(button.id, button);
        super.addButton(button);
    }

    public void addSideButton(GuiMenuSideButton button) {
        wrapper.sidebuttons.put(button.id, button);
        super.addButton(button);
    }

    public GuiButtonNop getButton(int i) {
        return wrapper.npcbuttons.get(i);
    }

    public GuiMenuSideButton getSideButton(int i) {
        return wrapper.sidebuttons.get(i);
    }

    public GuiMenuTopButton getTopButton(int i) {
        return wrapper.topbuttons.get(i);
    }

    public void addTextField(GuiTextFieldNop tf) {
        wrapper.textfields.put(tf.id, tf);
    }

    public GuiTextFieldNop getTextField(int i) {
        return wrapper.textfields.get(i);
    }

    public void add(IGui gui) {
        wrapper.components.add(gui);
    }

    public IGui get(int id) {
        for (IGui comp : wrapper.components) {
            if (comp.getID() == id)
                return comp;
        }
        return null;
    }

    public void addLabel(GuiLabel label) {
        wrapper.labels.put(label.id, label);
    }

    public GuiLabel getLabel(int i) {
        return wrapper.labels.get(i);
    }

    public void addSlider(GuiSliderNop slider) {
        wrapper.sliders.put(slider.id, slider);
        buttons.add(slider);
        children.add(slider);
    }

    public GuiSliderNop getSlider(int i) {
        return wrapper.sliders.get(i);
    }

    public void addScroll(GuiCustomScroll scroll) {
        scroll.init(minecraft, scroll.width, scroll.height);
        wrapper.scrolls.put(scroll.id, scroll);
    }

    public GuiCustomScroll getScroll(int id) {
        return wrapper.scrolls.get(id);
    }


    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {

    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {

    }

    @Override
    public void buttonEvent(GuiButtonNop button) {

    }

    public void save(){

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.wrapper.mouseX = mouseX;
        this.wrapper.mouseY = mouseY;
        this.mouseX = mouseX;
        this.mouseY = mouseX;

        T container = this.menu;
        if (wrapper.subgui != null) {
            this.menu = (T) new ContainerEmpty();
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, getFontRenderer(), I18n.get(title), width / 2, guiTop - 8, 0xffffff);
        for (GuiLabel label : new ArrayList<>(wrapper.labels.values()))
            label.render(matrixStack, mouseX, mouseY, partialTicks);
        for (GuiTextFieldNop tf : new ArrayList<>(wrapper.textfields.values()))
            tf.renderButton(matrixStack, mouseX, mouseY, partialTicks);
        for (GuiCustomScroll scroll : new ArrayList<>(wrapper.scrolls.values()))
            scroll.render(matrixStack, mouseX, mouseY, partialTicks);
        for (IGui comp : new ArrayList<>(wrapper.components)) {
            comp.render(matrixStack, mouseX, mouseY);
            for (Screen gui : new ArrayList<>(wrapper.extra.values()))
                gui.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        this.setBlitOffset(0);
        if (wrapper.subgui != null) {
            this.menu = container;
            //RenderHelper.disableStandardItemLighting();
            wrapper.subgui.render(matrixStack, mouseX, mouseY, partialTicks);
        } else {
            this.renderTooltip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderBackground(MatrixStack matrixStack) {
        if (drawDefaultBackground && wrapper.subgui == null)
            super.renderBackground(matrixStack);
    }

    public FontRenderer getFontRenderer() {
        return this.font;
    }

    public void setScreen(Screen gui) {
        this.minecraft.setScreen(gui);
    }


    public void setSubGui(Screen gui) {
        wrapper.setSubgui(gui);
        init();
    }

    public boolean hasSubGui() {
        return wrapper.subgui != null;
    }

    public Screen getSubGui() {
        return wrapper.getSubGui();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Screen getParent() {
        return wrapper.getParent();
    }
}
