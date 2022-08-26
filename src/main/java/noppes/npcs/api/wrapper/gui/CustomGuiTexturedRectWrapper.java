package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.ITexturedRect;

public class CustomGuiTexturedRectWrapper extends CustomGuiComponentWrapper implements ITexturedRect {

    int width,height;
    int textureX,textureY = -1;
    float scale = 1.0f;
    String texture;

    public CustomGuiTexturedRectWrapper(){}

    public CustomGuiTexturedRectWrapper(int id, String texture, int x, int y, int width, int height) {
        setID(id);
        setTexture(texture);
        setPos(x,y);
        setSize(width, height);
    }

    public CustomGuiTexturedRectWrapper(int id, String texture, int x, int y, int width, int height, int textureX, int textureY) {
        this(id, texture, x, y, width, height);
        setTextureOffset(textureX, textureY);
    }

    @Override
    public String getTexture() {
        return texture;
    }

    @Override
    public ITexturedRect setTexture(String texture) {
        this.texture = texture;
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
    public ITexturedRect setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public ITexturedRect setScale(float scale) {
        this.scale = scale;
        return this;
    }

    @Override
    public int getTextureX() {
        return textureX;
    }

    @Override
    public int getTextureY() {
        return textureY;
    }

    @Override
    public ITexturedRect setTextureOffset(int offsetX, int offsetY) {
        this.textureX = offsetX;
        this.textureY = offsetY;
        return this;
    }

    @Override
    public int getType() {
        return GuiComponentType.TEXTURED_RECT;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT compound) {
        super.toNBT(compound);
        compound.putIntArray("size", new int[] {width,height});
        compound.putFloat("scale", scale);
        compound.putString("texture", texture);
        if(textureX >=0 && textureY >=0) {
            compound.putIntArray("texPos", new int[] {textureX, textureY});
        }
        return compound;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(CompoundNBT compound) {
        super.fromNBT(compound);
        setSize(compound.getIntArray("size")[0],compound.getIntArray("size")[1]);
        setScale(compound.getFloat("scale"));
        setTexture(compound.getString("texture"));
        if(compound.contains("texPos"))
            setTextureOffset(compound.getIntArray("texPos")[0],compound.getIntArray("texPos")[1]);
        return this;
    }

}
