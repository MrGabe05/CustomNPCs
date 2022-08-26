package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.ILabel;

public class CustomGuiLabelWrapper extends CustomGuiComponentWrapper implements ILabel {

    String label;
    int width,height;
    int color = 0xffffff;
    float scale = 1.0f;

    public CustomGuiLabelWrapper(){}

    public CustomGuiLabelWrapper(int id, String label, int x, int y, int width, int height) {
        setID(id);
        setText(label);
        setPos(x,y);
        setSize(width,height);
    }

    public CustomGuiLabelWrapper(int id, String label, int x, int y, int width, int height, int color) {
        this(id, label, x, y, width, height);
        setColor(color);
    }

    @Override
    public String getText() {
        return label;
    }

    @Override
    public ILabel setText(String label) {
        this.label = label;
        return this;
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
    public ILabel setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public ILabel setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public ILabel setScale(float scale) {
        this.scale = scale;
        return this;
    }

    @Override
    public int getType() {
        return GuiComponentType.LABEL;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT compound) {
        super.toNBT(compound);
        compound.putString("label", label);
        compound.putIntArray("size", new int[] {width,height});
        compound.putInt("color", color);
        compound.putFloat("scale", scale);
        return compound;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(CompoundNBT compound) {
        super.fromNBT(compound);
        setText(compound.getString("label"));
        setSize(compound.getIntArray("size")[0], compound.getIntArray("size")[1]);
        setColor(compound.getInt("color"));
        setScale(compound.getFloat("scale"));
        return this;
    }

}
