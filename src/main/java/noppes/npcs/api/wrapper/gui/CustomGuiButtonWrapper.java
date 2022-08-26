package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.IButton;
import noppes.npcs.api.gui.ICustomGuiComponent;

public class CustomGuiButtonWrapper extends CustomGuiComponentWrapper implements IButton {

    int width,height = -1;
    String label;
    String texture;
    int textureX,textureY = -1;
    boolean enabled = true;

    public CustomGuiButtonWrapper(){}

    public CustomGuiButtonWrapper(int id, String label, int x, int y) {
        setID(id);
        setLabel(label);
        setPos(x,y);
    }

    public CustomGuiButtonWrapper(int id, String label, int x, int y, int width, int height) {
        this(id,label,x,y);
        setSize(width,height);
    }

    public CustomGuiButtonWrapper(int id, String label, int x, int y, int width, int height, String texture) {
        this(id,label,x,y,width,height);
        setTexture(texture);
    }

    public CustomGuiButtonWrapper(int id, String label, int x, int y, int width, int height, String texture, int textureX, int textureY) {
        this(id,label,x,y,width,height,texture);
        setTextureOffset(textureX, textureY);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public IButton setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public IButton setLabel(String label) {
        this.label = label;
        return this;
    }


    @Override
    public String getTexture() {
        return texture;
    }

    @Override
    public boolean hasTexture() {
        return this.texture!=null;
    }

    @Override
    public IButton setTexture(String texture) {
        this.texture = texture;
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
    public IButton setTextureOffset(int textureX, int textureY) {
        this.textureX = textureX;
        this.textureY = textureY;
        return this;
    }

    @Override
    public void setEnabled(boolean bo) {
        enabled = bo;
    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public ICustomGuiComponent setID(int id) {
        this.id = id;
        return this;
    }

    @Override
    public int getPosX() {
        return posX;
    }

    @Override
    public int getPosY() {
        return posY;
    }

    @Override
    public int getType() {
        return GuiComponentType.BUTTON;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT nbt) {
        super.toNBT(nbt);
        if(width > 0 && height > 0)
            nbt.putIntArray("size", new int[] {width,height});
        nbt.putString("label", label);
        if(hasTexture())
            nbt.putString("texture", texture);
        if(textureX >=0 && textureY >=0) {
            nbt.putIntArray("texPos", new int[] {textureX, textureY});
        }
        nbt.putBoolean("enabled", enabled);
        return nbt;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(CompoundNBT nbt) {
        super.fromNBT(nbt);
        if(nbt.contains("size"))
            setSize(nbt.getIntArray("size")[0],nbt.getIntArray("size")[1]);
        setLabel(nbt.getString("label"));
        if(nbt.contains("texture"))
            setTexture(nbt.getString("texture"));
        if(nbt.contains("texPos"))
            setTextureOffset(nbt.getIntArray("texPos")[0],nbt.getIntArray("texPos")[1]);
        setEnabled(nbt.getBoolean("enabled"));
        return this;
    }
}
