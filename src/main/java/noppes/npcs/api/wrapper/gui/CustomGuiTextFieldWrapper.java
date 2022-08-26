package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.ITextField;

public class CustomGuiTextFieldWrapper extends CustomGuiComponentWrapper implements ITextField {

    int width,height;
    String defaultText = "";
    boolean enabled = true;

    public CustomGuiTextFieldWrapper(){}

    public CustomGuiTextFieldWrapper(int id, int x, int y, int width, int height) {
        setID(id);
        setPos(x,y);
        setSize(width, height);
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
    public ITextField setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public String getText() {
        return defaultText;
    }

    @Override
    public ITextField setText(String defaultText) {
        if(defaultText == null){
            this.defaultText = "";
        }
        else{
            this.defaultText = defaultText;
        }
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
    public int getType() {
        return GuiComponentType.TEXT_FIELD;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT nbt) {
        super.toNBT(nbt);
        nbt.putIntArray("size", new int[] {width,height});
        nbt.putString("default", defaultText);
        nbt.putBoolean("enabled", enabled);
        return nbt;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(CompoundNBT nbt) {
        super.fromNBT(nbt);
        setSize(nbt.getIntArray("size")[0],nbt.getIntArray("size")[1]);
        setText(nbt.getString("default"));
        setEnabled(nbt.getBoolean("enabled"));
        return this;
    }

}
