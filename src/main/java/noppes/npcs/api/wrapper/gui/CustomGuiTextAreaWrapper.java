package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.ITextArea;
import noppes.npcs.api.gui.ITextField;

public class CustomGuiTextAreaWrapper extends CustomGuiTextFieldWrapper implements ITextArea {

	private boolean codeTheme = false;
	
    public CustomGuiTextAreaWrapper(){}

    public CustomGuiTextAreaWrapper(int id, int x, int y, int width, int height) {
    	super(id, x, y, width, height);
    }

    @Override
    public int getType() {
        return GuiComponentType.TEXT_AREA;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT nbt) {
        super.toNBT(nbt);
        nbt.putBoolean("codetheme", codeTheme);
        return nbt;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(CompoundNBT nbt) {
        super.fromNBT(nbt);
        setCodeTheme(nbt.getBoolean("codetheme"));
        return this;
    }

	@Override
	public void setCodeTheme(boolean bo) {
		codeTheme = bo;
	}

	@Override
	public boolean getCodeTheme() {
		return codeTheme;
	}

}
