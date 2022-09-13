package noppes.npcs.shared.client.gui.components;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.shared.client.gui.listeners.ISliderListener;

public class GuiSliderNop extends Widget {
    private final ISliderListener listener;
    public int id;

    public float sliderValue;
    public float startValue;

    public GuiSliderNop(Screen parent, int id, int xPos, int yPos, String displayString, float sliderValue) {
        super(xPos, yPos, 150, 20, new TranslationTextComponent(displayString));
        this.id = id;
        this.sliderValue = sliderValue;
        this.startValue = sliderValue;
        listener = (ISliderListener) parent;
    }

    public GuiSliderNop(Screen parent, int id, int xPos, int yPos, float sliderValue) {
        this(parent, id, xPos, yPos, "", sliderValue);
        listener.mouseDragged(this);
    }

    public GuiSliderNop(Screen parent, int id, int xPos, int yPos, int width, int height, float sliderValue) {
        this(parent, id, xPos, yPos, "", sliderValue);
        this.width = width;
        this.height = height;
        listener.mouseDragged(this);
    }

    @Override
    public void playDownSound(SoundHandler p_146113_1_) {

    }

    @Override
    protected int getYImage(boolean p_getYImage_1_) {
        return 0;
    }

    public void setString(String str) {
        setMessage(new TranslationTextComponent(str));
    }

    private void setSliderValue(float value){
        value = MathHelper.clamp(value, 0.0f, 1.0f);
        if(value == this.sliderValue)
            return;
        this.sliderValue = value;
        listener.mouseDragged(this);
    }

    @Override
    public void onClick(double x, double y){
        if(!visible || !active)
            return;
        setSliderValue((float)(x - (this.x + 4)) / (float)(this.width - 8));
        super.onClick(x, y);
    }

    @Override
    protected void onDrag(double x, double y, double p_onDrag_5_, double p_onDrag_7_) {
        setSliderValue((float)(x - (this.x + 4)) / (float)(this.width - 8));
        super.onDrag(x, y, p_onDrag_5_, p_onDrag_7_);
    }

    @Override
    public void onRelease(double x, double y) {
        if(sliderValue == startValue){
            return;
        }
        super.playDownSound(Minecraft.getInstance().getSoundManager());
        listener.mouseReleased(this);
        this.startValue = sliderValue;
    }

    @Override
    public void renderBg(MatrixStack matrixStack, Minecraft mc, int p_146119_2_, int p_146119_3_) {
        if(!visible)
            return;
        mc.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int lvt_4_1_ = (this.isHovered() ? 2 : 1) * 20;
        this.blit(matrixStack, this.x + (int)(this.sliderValue * (double)(this.width - 8)), this.y, 0, 46 + lvt_4_1_, 4, 20);
        this.blit(matrixStack, this.x + (int)(this.sliderValue * (double)(this.width - 8)) + 4, this.y, 196, 46 + lvt_4_1_, 4, 20);
        //this.blit(this.x + (int)(this.sliderValue * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
        //this.blit(this.x + (int)(this.sliderValue * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);

    }
}
