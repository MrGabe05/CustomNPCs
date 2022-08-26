package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiTextFieldNop extends TextFieldWidget {
	public boolean enabled = true;
	public boolean inMenu = true;
	public boolean numbersOnly = false;
	public ITextfieldListener listener;
	public int id;
	public int min = 0,max = Integer.MAX_VALUE,def = 0;
	private static GuiTextFieldNop activeTextfield = null;

	private final int[] allowedSpecialChars = {14,211,203,205};

	public GuiTextFieldNop(int id, Screen parent, int i, int j, int k, int l, String s) {
		this(id, parent, i, j, k, l, new TranslationTextComponent(s != null ? s : ""));
	}
	public GuiTextFieldNop(int id, Screen parent, int i, int j, int k, int l, ITextComponent s) {
		super(Minecraft.getInstance().font, i, j, k, l, s);
		setMaxLength(500);
		if(!s.getString().isEmpty()){
			this.setValue(s.getString());
		}
		this.id = id;
		if(parent instanceof ITextfieldListener)
			listener = (ITextfieldListener) parent;
	}

	public static boolean isActive(){
		return activeTextfield != null;
	}

	public static GuiTextFieldNop getActive() {
		return activeTextfield;
	}

	private boolean charAllowed(char c, int i){
		if(!numbersOnly || Character.isDigit(c))
			return true;
		for(int j : allowedSpecialChars)
			if(j == i)
				return true;

		return false;
	}

	@Override
	public boolean charTyped(char c, int i) {
		if(!charAllowed(c,i))
			return false;
		return super.charTyped(c, i);
	}
	public boolean isEmpty(){
		return getValue().trim().length() == 0;
	}

	public int getInteger(){
		return Integer.parseInt(getValue());
	}
	public boolean isInteger(){
		try{
			Integer.parseInt(getValue());
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	@Override
	public boolean mouseClicked(double i, double j, int k){
		boolean wasFocused = this.isFocused();
		boolean clicked = super.mouseClicked(i, j, k);
		if(!wasFocused && isFocused()){
			GuiTextFieldNop.unfocus();
			activeTextfield = this;
		}
		if(wasFocused && !isFocused()){
			unFocused();
		}
		return clicked;
	}

	public void unFocused(){
		if(numbersOnly){
			if(isEmpty() || !isInteger())
				setValue(def + "");
			else if( getInteger() < min)
				setValue(min+"");
			else if(getInteger() > max)
				setValue(max+"");
		}
		if(listener != null)
			listener.unFocused(this);
		setFocus(false);
		if(this == activeTextfield)
			activeTextfield = null;
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int x, int y, float f) {
		if(enabled)
			super.renderButton(matrixStack, y, x, f);
	}
	public void setMinMaxDefault(int i, int j, int k) {
		min = i;
		max = j;
		def = k;
	}
	public static void unfocus() {
		GuiTextFieldNop field = activeTextfield;
		activeTextfield = null;//prevent infinite loop, set null before calling unfocused
		if(field != null)
			field.unFocused();
	}


    public GuiTextFieldNop setNumbersOnly() {
		this.numbersOnly = true;
		return this;
    }
}
