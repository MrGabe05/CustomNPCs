package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.listeners.IGui;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.IKeyListener;
import noppes.npcs.shared.client.gui.listeners.IMouseListener;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuiWrapper {
    public Map<Integer, GuiButtonNop> npcbuttons = new ConcurrentHashMap<Integer, GuiButtonNop>();
    public Map<Integer,GuiMenuTopButton> topbuttons = new ConcurrentHashMap<Integer,GuiMenuTopButton>();
    public Map<Integer,GuiMenuSideButton> sidebuttons = new ConcurrentHashMap<Integer,GuiMenuSideButton>();
    public Map<Integer, GuiTextFieldNop> textfields = new ConcurrentHashMap<Integer, GuiTextFieldNop>();
    public Map<Integer, GuiLabel> labels = new ConcurrentHashMap<Integer,GuiLabel>();
    public Map<Integer, GuiCustomScroll> scrolls = new ConcurrentHashMap<Integer,GuiCustomScroll>();
    public Map<Integer, GuiSliderNop> sliders = new ConcurrentHashMap<Integer, GuiSliderNop>();
    public Map<Integer,Screen> extra = new ConcurrentHashMap<Integer,Screen>();
    public List<IGui> components = new ArrayList<IGui>();


    public Screen parent;
    public Screen gui;
    public Screen subgui;
    public int mouseX, mouseY;

    public GuiWrapper(Screen gui){
        this.gui = gui;
    }

    public void init(Minecraft mc, int width, int height){
        GuiTextFieldNop.unfocus();
        if(subgui != null){
            subgui.init(mc, width, height);
        }

        npcbuttons = new ConcurrentHashMap<Integer, GuiButtonNop>();
        topbuttons = new ConcurrentHashMap<Integer,GuiMenuTopButton>();
        sidebuttons = new ConcurrentHashMap<Integer,GuiMenuSideButton>();
        textfields = new ConcurrentHashMap<Integer, GuiTextFieldNop>();
        labels = new ConcurrentHashMap<Integer,GuiLabel>();
        scrolls = new ConcurrentHashMap<Integer,GuiCustomScroll>();
        sliders = new ConcurrentHashMap<Integer, GuiSliderNop>();
        extra = new ConcurrentHashMap<Integer, Screen>();
        components = new ArrayList<IGui>();
    }

    public void tick(){
        if(subgui != null)
            subgui.tick();
        else{
            for(GuiTextFieldNop tf : new ArrayList<GuiTextFieldNop>(textfields.values())){
                if(tf.enabled)
                    tf.tick();
            }

            for(IGui comp : new ArrayList<IGui>(components)){
                comp.tick();
            }
        }
    }

    public void addExtra(GuiHoverText gui) {
        gui.init(Minecraft.getInstance(), 350, 250);
        extra.put(gui.id, gui);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrolled){
        if(subgui != null) {
            subgui.mouseScrolled(mouseX, mouseY, scrolled);
            return true;
        }
        for(IGui comp : new ArrayList<IGui>(components)){
            if(comp instanceof IMouseListener){
                if(comp.isActive() && ((IMouseListener)comp).mouseScrolled(scrolled)){
                    return true;
                }
            }
        }
        for(GuiCustomScroll scroll : scrolls.values()){
            if(scroll.visible && scroll.mouseScrolled(mouseX, mouseY, scrolled)){
                return true;
            }
        }
        return false;
    }

    public boolean mouseClicked(double i, double j, int k){
        if(subgui != null) {
            subgui.mouseClicked(i, j, k);
            return true;
        }

        for(GuiTextFieldNop tf : new ArrayList<GuiTextFieldNop>(textfields.values())){
            if(tf.enabled && tf.mouseClicked(i, j, k))
                return true;
        }

        for(IGui comp : new ArrayList<IGui>(components)){
            if(comp instanceof IMouseListener){
                if(((IMouseListener)comp).mouseClicked(i, j, k)){
                    return true;
                }
            }
        }

        if (k == 0){
            for(GuiCustomScroll scroll : new ArrayList<GuiCustomScroll>(scrolls.values())){
                if(scroll.mouseClicked(i, j, k)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if(subgui != null) {
            subgui.mouseDragged(x, y, button, dx, dy);
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double x, double y, int button) {
        if(subgui != null) {
            subgui.mouseReleased(x, y, button);
            return true;
        }
        return false;
    }

    public boolean charTyped(char c, int i){
        if(subgui != null){
            subgui.charTyped(c,i);
            return true;
        }

        for(GuiTextFieldNop tf : new ArrayList<GuiTextFieldNop>(textfields.values())){
            tf.charTyped(c, i);
        }

        for(GuiCustomScroll scroll : new ArrayList<GuiCustomScroll>(scrolls.values())){
            scroll.charTyped(c, i);
        }

        for(IGui comp : new ArrayList<IGui>(components)){
            if(comp instanceof IKeyListener){
                ((IKeyListener)comp).charTyped(c, i);
            }
        }
        return true;
    }

    public boolean keyPressed(int key, int p_keyPressed_2_, int p_keyPressed_3_) {
        if(subgui != null) {
            subgui.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
            return true;
        }


        boolean active = GuiTextFieldNop.isActive();
        for(IGui gui : components){
            if(gui.isActive()){
                active = true;
                break;
            }
        }

        if (gui.shouldCloseOnEsc() && (key == GLFW.GLFW_KEY_ESCAPE || !active && Minecraft.getInstance().options.keyInventory.getKey().getValue() == key)){
            gui.onClose();
            return true;
        }

        for(GuiTextFieldNop tf : textfields.values()) {
            tf.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
        }

        for(GuiCustomScroll scroll : new ArrayList<GuiCustomScroll>(scrolls.values())){
            scroll.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
        }

        for(IGui comp : new ArrayList<IGui>(components)){
            if(comp instanceof IKeyListener){
                if(((IKeyListener)comp).keyPressed(key, p_keyPressed_2_, p_keyPressed_3_)){
                    return true;
                }
            }
        }
        return active;
    }
    public void drawNpc(LivingEntity entity, int x, int y, float zoomed, int rotation, int guiLeft, int guiTop){
        EntityNPCInterface npc = null;
        if(entity instanceof EntityNPCInterface)
            npc = (EntityNPCInterface) entity;

        float f2 = entity.yBodyRot;
        float f3 = entity.yRot;
        float f4 = entity.xRot;
        float f5 = entity.yHeadRotO;
        float f6 = entity.yHeadRot;

        float scale = 1;
        if(entity.getBbHeight() > 2.4) {
            scale = 2 / entity.getBbHeight();
        }
        float f7 = (float) (guiLeft + x) - mouseX;
        float f8 = (float) ((guiTop + y) - 50 * scale * zoomed) - mouseY;

        entity.yBodyRot = 0;
        entity.yRot = (float)Math.atan(f7 / 80F) * 40F + rotation;
        entity.xRot = -(float) Math.atan(f8 / 40F) * 20F;
        entity.yHeadRot = 0;
        entity.yHeadRotO = 0;
        int orientation = 0;
        if(npc != null){
            orientation = npc.ais.orientation;
            npc.ais.orientation = rotation;
        }
        //entity.yBodyRot = rotation;
        //entity.yRot = (float)Math.atan(f5 / 80F) * 40F + rotation;
        //entity.xRot = -(float) Math.atan(f6 / 40F) * 20F;

        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)guiLeft + x, guiTop + y, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0F);
        matrixStack.scale(30 * scale * zoomed, 30 * scale * zoomed, 30 * scale * zoomed);

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        EntityRendererManager lvt_16_1_ = Minecraft.getInstance().getEntityRenderDispatcher();
        lvt_16_1_.setRenderShadow(false);
        IRenderTypeBuffer.Impl lvt_17_1_ = Minecraft.getInstance().renderBuffers().bufferSource();
        matrixStack.mulPose(Vector3f.YN.rotationDegrees(rotation));
        RenderSystem.runAsFancy(() -> {
            lvt_16_1_.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, lvt_17_1_, 15728880);
        });
        lvt_17_1_.endBatch();
        lvt_16_1_.setRenderShadow(true);
        RenderSystem.popMatrix();

        entity.yBodyRot = f2;
        entity.yRot = f3;
        entity.xRot = f4;
        entity.yHeadRotO = f5;
        entity.yHeadRot = f6;

        if(npc != null){
            npc.ais.orientation = orientation;
        }
    }

    public void changeFocus(IGuiEventListener old, IGuiEventListener gui) {
        if(old instanceof GuiSliderNop && gui != old){
            ((GuiSliderNop)old).onRelease(0, 0);
        }
    }

    public void setSubgui(Screen subgui) {
        gui.setFocused(null);
        this.subgui = subgui;
        subgui.init(Minecraft.getInstance(), gui.width, gui.height);
        if(subgui instanceof GuiBasic){
            ((GuiBasic)subgui).wrapper.parent = gui;
        }
        if(subgui instanceof GuiBasicContainer){
            ((GuiBasicContainer)subgui).wrapper.parent = gui;
        }
    }

    public Screen getSubGui() {
        if (subgui instanceof IGuiInterface && ((IGuiInterface)subgui).hasSubGui()) {
            return ((IGuiInterface) subgui).getSubGui();
        }
        return subgui;
    }

    public Screen getParent() {
        if (parent != null)
            return ((IGuiInterface)parent).getParent();
        return null;
    }

    public void close() {
        GuiTextFieldNop.unfocus();
        ((IGuiInterface) gui).save();
        if(parent != null){

            if(parent instanceof IGuiInterface) {
                parent.setFocused(null);
                ((GuiBasic) parent).wrapper.subgui = null;
                ((GuiBasic) parent).subGuiClosed(gui);
                ((GuiBasic) parent).init();
            }
            else
                gui.onClose();
        }
        else{
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.keyboardHandler.setSendRepeatsToGui(false);
            minecraft.setScreen(gui);
            minecraft.mouseHandler.grabMouse();
        }
    }
}
