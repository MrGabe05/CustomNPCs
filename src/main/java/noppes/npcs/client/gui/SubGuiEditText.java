package noppes.npcs.client.gui;

import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;

public class SubGuiEditText extends GuiBasic {
	public String text;
	public boolean cancelled = true;
	public int id;
	public SubGuiEditText(String text){
		this.text = text;
		setBackground("extrasmallbg.png");
		imageWidth = 176;
		imageHeight = 71;
	}
	
	public SubGuiEditText(int id, String text){
		this(text);
		this.id = id;
	}
	
	@Override
	public void init(){
		super.init();
		addTextField(new GuiTextFieldNop(0, wrapper.parent, guiLeft + 4, guiTop + 14, 168, 20, text));
		
		this.addButton(new GuiButtonNop(this, 0, guiLeft + 4, guiTop + 44, 80, 20, "gui.done"));
		this.addButton(new GuiButtonNop(this, 1, guiLeft + 90, guiTop + 44, 80, 20, "gui.cancel"));
	}
	
	@Override
	public void buttonEvent(GuiButtonNop button){
		if(button.id == 0){
			cancelled = false;
			text = getTextField(0).getValue();
		}
		close();
	}
	
	@Override
	public void save() {
	}
}
