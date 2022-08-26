package noppes.npcs.shared.client.gui;

import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextArea;
import noppes.npcs.shared.client.gui.listeners.ITextChangeListener;

public class GuiTextAreaScreen extends GuiBasic implements ITextChangeListener{
	public String text;
	public String originalText;
	private GuiTextArea textarea;
	private boolean highlighting = false;

	public GuiTextAreaScreen(String text){
		this.text = text;
		this.originalText = text;
		setBackground("bgfilled.png");
		imageWidth = 256;
		imageHeight = 256;
	}

	public GuiTextAreaScreen(String originalText, String text){
		this(text);
		this.originalText = originalText;
	}
	
	@Override
	public void init(){
		imageWidth = (int) (width * 0.88);
		imageHeight = (int) (imageWidth * 0.56);
		if(imageHeight > height * 0.95){
			imageHeight = (int)(height * 0.95);
			imageWidth = (int)(imageHeight / 0.56);
		}
		bgScale = imageWidth / 440f;
		super.init();
		if(textarea != null)
			this.text = textarea.getText();
		int yoffset = (int) (imageHeight * 0.02);
		
		textarea = new GuiTextArea(2, guiLeft + 1 + yoffset, guiTop + yoffset, imageWidth - 100 - yoffset, (int) (imageHeight) - yoffset * 2, text);
		textarea.setListener(this);
		if(highlighting)
			textarea.enableCodeHighlighting();
		add(textarea);
		
		this.addButton(new GuiButtonNop(this, 102, guiLeft + imageWidth - 90 - yoffset, guiTop + 20, 56, 20, "gui.clear"));
		this.addButton(new GuiButtonNop(this, 101, guiLeft + imageWidth - 90 - yoffset, guiTop + 43, 56, 20, "gui.paste"));
		this.addButton(new GuiButtonNop(this, 100, guiLeft + imageWidth - 90 - yoffset, guiTop + 66, 56, 20, "gui.copy"));
		this.addButton(new GuiButtonNop(this, 103, guiLeft + imageWidth - 90 - yoffset, guiTop + 89, 56, 20, "gui.reset"));

		this.addButton(new GuiButtonNop(this, 0, guiLeft + imageWidth - 90 - yoffset, guiTop + 160, 56, 20, "gui.close"));

		imageWidth = 420;
		imageHeight = 256;
	}
	
	public GuiTextAreaScreen enableHighlighting() {
		highlighting = true;
		return this;
	}

	@Override
	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
		if (id == 100) {
			NoppesStringUtils.setClipboardContents(textarea.getText());
		}
		if (id == 101) {
			textarea.setText(NoppesStringUtils.getClipboardContents());
		}
		if (id == 102) {
			textarea.setText("");
		}
		if (id == 103) {
			textarea.setText(originalText);
		}
		if(id == 0){
			close();
		}
	}

	@Override
	public void textUpdate(String text) {
		this.text = text;
	}
}
