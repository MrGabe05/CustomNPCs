package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiButtonNop extends Button {
	public boolean shown = true;
	public IGuiInterface gui;

	protected String[] display;
	private int displayValue = 0;
	public int id;
	protected static final Button.IPressable clicked = button -> {
		GuiButtonNop b = (GuiButtonNop) button;
		b.gui.buttonEvent(b);
	};

	public GuiButtonNop(IGuiInterface gui, int i, int j, int k, String s) {
		super(j, k, 200, 20,  new TranslationTextComponent(s), clicked);
		id = i;
		this.gui = gui;
	}
	public GuiButtonNop(IGuiInterface gui, int i, int j, int k, String[] display, int val) {
		this(gui, i, j, k, display[val]);
		this.display = display;
		this.displayValue = val;
	}
	public GuiButtonNop(IGuiInterface gui, int i, int j, int k, int l, int m, String string) {
		super(j, k, l, m, new TranslationTextComponent(string), clicked);
		id = i;
		this.gui = gui;
	}
	public GuiButtonNop(IGuiInterface gui, int i, int j, int k, int l, int m, String string, Button.IPressable clicked) {
		super(j, k, l, m, new TranslationTextComponent(string), clicked);
		id = i;
		this.gui = gui;
	}

	public GuiButtonNop(IGuiInterface gui, int i, int j, int k, int l, int m, String string, boolean enabled) {
		this(gui, i, j, k, l, m, string);
		this.active = enabled;
	}

	public GuiButtonNop(IGuiInterface gui, int i, int j, int k, int l, int m, String[] display, int val) {
		this(gui, i, j, k, l, m, val, display);
	}

	public GuiButtonNop(IGuiInterface gui, int i, int j, int k, int l, int m, int val, String... display) {
		this(gui, i, j, k, l, m, display.length == 0?"":display[val % display.length]);
		this.display = display;
		this.displayValue = display.length == 0?0:val % display.length;
	}

	public GuiButtonNop(IGuiInterface gui, int i, int j, int k, int l, int m, Button.IPressable clicked, int val, String... display) {
		this(gui, i, j, k, l, m, display.length == 0?"":display[val % display.length], clicked);
		this.display = display;
		this.displayValue = display.length == 0?0:val % display.length;
	}

	public void setDisplayText(String text){
		this.setMessage(new TranslationTextComponent(text));
	}
	public int getValue(){
		return displayValue;
	}
	public void clicked(){

	}
	@Override
	public void render(MatrixStack matrixStack, int i, int j, float partialTicks) {
		if(!shown)
			return;
		super.render(matrixStack, i, j, partialTicks);
	}

	@Override
	public void onClick(double x, double y){
		if(gui.hasSubGui())
			return;
		if(display != null)
			setDisplay((displayValue+1) % display.length);
		super.onPress();
	}

	public void setDisplay(int value){
		this.displayValue = value;
		this.setDisplayText(display[value]);
	}

	public void setEnabled(boolean bo){
		this.active = bo;
	}
}
