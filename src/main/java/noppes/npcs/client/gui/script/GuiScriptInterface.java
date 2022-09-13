package noppes.npcs.client.gui.script;

import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.NBTTags;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextChangeListener;

import java.util.*;
import java.util.Map.Entry;

public class GuiScriptInterface extends GuiNPCInterface implements IGuiData, ITextChangeListener {
	private int activeTab = 0;
	public IScriptHandler handler;
	public Map<String,List<String>> languages = new HashMap<String,List<String>>();
	public List<String> methods = new ArrayList<>();

	public boolean showFunctions = false;
		
	public GuiScriptInterface() {
		drawDefaultBackground = true;
        imageWidth = 420;
        setBackground("menubg.png");
	}

	@Override
	public void init() {
		imageWidth = (int) (width * 0.88);
		imageHeight = (int) (imageWidth * 0.56);
		if(imageHeight > height * 0.95){
			imageHeight = (int)(height * 0.95);
			imageWidth = (int)(imageHeight / 0.56);
		}
		bgScale = imageWidth / 400f;
		super.init();
		guiTop += 10;
		int yoffset = (int) (imageHeight * 0.02);
		GuiMenuTopButton top;
		addTopButton(top = new GuiMenuTopButton(this, 0, guiLeft + 4, guiTop - 17, "gui.settings"));
		
		for(int i = 0; i < handler.getScripts().size(); i++){
			ScriptContainer script = this.handler.getScripts().get(i);
			addTopButton(top = new GuiMenuTopButton(this, i + 1, top, i + 1 + ""));
		}
		if(handler.getScripts().size() < 40)
			addTopButton(top = new GuiMenuTopButton(this, 41, top, "+"));
		//addTopButton(new GuiMenuTopButton(this, 15, top, "gui.website"));
		
		top = getTopButton(activeTab);
		if(top == null){
			activeTab = 0;
			top = getTopButton(0);
		}
		top.active = true;
        
		if(activeTab > 0){	        
			ScriptContainer container = handler.getScripts().get(activeTab - 1);
			
			GuiTextArea ta = new GuiTextArea(3, guiLeft + 1 + yoffset, guiTop + yoffset, imageWidth - 108 - yoffset, (int) (imageHeight * 0.96) - yoffset * 2, container == null?"":container.script);
			ta.enableCodeHighlighting();
			ta.setListener(this);
			add(ta);
			int left = guiLeft + imageWidth - 104;
			addButton(new GuiButtonNop(this, 99, left, guiTop + yoffset, 121, 20, showFunctions ? "script.hideFunctions" : "script.showFuncions", (button) -> {
				showFunctions = !showFunctions;
				init();
			}));

			if(!showFunctions){
				addButton(new GuiButtonNop(this, 102, left, guiTop + yoffset + 22, 60, 20, "gui.clear"));
				addButton(new GuiButtonNop(this, 101, left + 61, guiTop + yoffset + 22, 60, 20, "gui.paste"));
				addButton(new GuiButtonNop(this, 100, left, guiTop + 21 + yoffset + 22, 60, 20, "gui.copy"));
				addButton(new GuiButtonNop(this, 105, left + 61, guiTop + 21 + yoffset + 22, 60, 20, "gui.remove"));

				addButton(new GuiButtonNop(this, 107, left, guiTop + 66 + yoffset, 121, 20, "script.loadscript"));

				GuiCustomScroll scroll = new GuiCustomScroll(this, 0).setUnselectable();
				scroll.setSize(104 + (int) (16 * bgScale), (int) (imageHeight * 0.54) - yoffset * 2);
				scroll.guiLeft = left;
				scroll.guiTop = guiTop + 88 + yoffset;
				if(container != null)
					scroll.setList(container.scripts);
				addScroll(scroll);
			}
			else{
				GuiCustomScroll scroll = new GuiCustomScroll(this, 1);
				scroll.setSize(104 + (int) (16 * bgScale), (int) (imageHeight * 0.6) - yoffset * 2);
				scroll.guiLeft = left;
				scroll.guiTop = guiTop + yoffset + 22;
				scroll.setList(methods);
				scroll.listener = new ICustomScrollListener(){

					@Override
					public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {

					}

					@Override
					public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
						ta.addText(selection);
					}
				};
				addScroll(scroll);

				scroll = new GuiCustomScroll(this, 2);
				scroll.setSize(104 + (int) (16 * bgScale), (int) (imageHeight * 0.32) - yoffset * 2);
				scroll.guiLeft = left;
				scroll.guiTop = guiTop + yoffset + (int) (imageHeight * 0.6) - yoffset * 2 + 26;
				scroll.setList(new ArrayList<>(ScriptContainer.Data.keySet()));
				scroll.listener = new ICustomScrollListener(){

					@Override
					public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {

					}

					@Override
					public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
						ta.addText(selection);
					}
				};
				addScroll(scroll);
			}
		}
		else{
			GuiTextArea ta = new GuiTextArea(3, guiLeft + 4 + yoffset, guiTop + 6 + yoffset, imageWidth - 160 - yoffset, (int)(imageHeight * 0.92f) - yoffset * 2, getConsoleText());
			ta.enabled = false;
			add(ta);
			
			int left = guiLeft + imageWidth - 150;

			addButton(new GuiButtonNop(this, 100, left, guiTop + 145, 60, 20, "gui.copy"));
			addButton(new GuiButtonNop(this, 102, left, guiTop + 166, 60, 20, "gui.clear"));
			
			addLabel(new GuiLabel(1, "script.language", left, guiTop + 15));
			addButton(new GuiButtonNop(this, 103, left + 60, guiTop + 10, 80, 20, languages.keySet().toArray(new String[languages.keySet().size()]), getScriptIndex()));
			getButton(103).active = languages.size() > 0;

			addLabel(new GuiLabel(2, "gui.enabled", left, guiTop + 36));
			addButton(new GuiButtonNop(this, 104, left + 60, guiTop + 31, 50, 20, new String[]{"gui.no","gui.yes"}, handler.getEnabled()?1:0));
			
			if(player.getServer() != null)
				addButton(new GuiButtonNop(this, 106, left, guiTop + 55, 150, 20, "script.openfolder"));

			addButton(new GuiButtonNop(this, 109, left, guiTop + 78, 80, 20, "gui.website"));
			addButton(new GuiButtonNop(this, 112, left + 81, guiTop + 78, 80, 20, "script.examples"));
			addButton(new GuiButtonNop(this, 110, left, guiTop + 99, 80, 20, "script.apidoc"));
			addButton(new GuiButtonNop(this, 111, left + 81, guiTop + 99, 80, 20, "script.apisrc"));
		}

		imageWidth = 420;
		imageHeight = 256;
	}

	private String getConsoleText() {
		Map<Long, String> map = handler.getConsoleText();	
		StringBuilder builder = new StringBuilder();
		for(Entry<Long, String> entry : map.entrySet()){
			builder.insert(0, new Date(entry.getKey()) + entry.getValue() + "\n");
		}
		return builder.toString();
	}

	private int getScriptIndex() {
		int i = 0;
		for(String language : languages.keySet()){
			if(language.equalsIgnoreCase(handler.getLanguage()))
				return i;
			i++;
		}
		return 0;
	}
    
	@Override
	public void buttonEvent(GuiButtonNop guibutton) {
		if(guibutton.id  >= 0 && guibutton.id < 41){
			setScript();
			activeTab = guibutton.id;
			init();
		}
		if(guibutton.id == 41){
			handler.getScripts().add(new ScriptContainer(handler));
			activeTab = handler.getScripts().size();
			init();
		}
		if(guibutton.id == 109){
			setScreen(new ConfirmOpenLinkScreen((bo) -> {
				if(bo){
					Util.getPlatform().openUri("http://www.kodevelopment.nl/minecraft/customnpcs/scripting");
				}
				setScreen(GuiScriptInterface.this);
			}, "http://www.kodevelopment.nl/minecraft/customnpcs/scripting", true));
		}
		if(guibutton.id == 110){
			setScreen(new ConfirmOpenLinkScreen((bo) -> {
				if(bo){
					Util.getPlatform().openUri("http://www.kodevelopment.nl/customnpcs/api/");
				}
				setScreen(GuiScriptInterface.this);
			}, "http://www.kodevelopment.nl/customnpcs/api/", true));
		}
		if(guibutton.id == 111){
			setScreen(new ConfirmOpenLinkScreen((bo) -> {
				if(bo){
					Util.getPlatform().openUri("https://github.com/Noppes/CustomNPCsAPI");
				}
				setScreen(GuiScriptInterface.this);
			}, "https://github.com/Noppes/CustomNPCsAPI", true));
		}
		if(guibutton.id == 112){
			setScreen(new ConfirmOpenLinkScreen((bo) -> {
				if(bo){
					Util.getPlatform().openUri("https://github.com/Noppes/cnpcs-scripting-examples");
				}
				setScreen(GuiScriptInterface.this);
			}, "https://github.com/Noppes/cnpcs-scripting-examples", true));
		}
		if (guibutton.id == 100) {
			NoppesStringUtils.setClipboardContents(((GuiTextArea)get(3)).getText());
		}
		if (guibutton.id == 101) {
			((GuiTextArea)get(3)).setText(NoppesStringUtils.getClipboardContents());
		}
		if (guibutton.id == 102) {
			if(activeTab > 0){
				ScriptContainer container = handler.getScripts().get(activeTab - 1);
				container.script = "";
			}
			else{
				handler.clearConsole();
			}
			init();
		}
		if (guibutton.id == 103) {
			handler.setLanguage(guibutton.getMessage().getString());
		}
		if (guibutton.id == 104) {
			handler.setEnabled(guibutton.getValue() == 1);
		}
		if (guibutton.id == 105) {
            ConfirmScreen guiyesno = new ConfirmScreen((bo) -> {
				handler.getScripts().remove(activeTab - 1);
				activeTab = 0;
				setScreen(GuiScriptInterface.this);
			}, new TranslationTextComponent(""), new TranslationTextComponent("gui.deleteMessage"));
            setScreen(guiyesno);
		}
		if (guibutton.id == 106) {
			NoppesUtil.openFolder(ScriptController.Instance.dir);
		}
		if (guibutton.id == 107) {
			ScriptContainer container = handler.getScripts().get(activeTab - 1);
			if(container == null)
				handler.getScripts().add(container = new ScriptContainer(handler));
			setSubGui(new GuiScriptList(languages.get(handler.getLanguage()), container));
		}
		if (guibutton.id == 108) {
			ScriptContainer container = handler.getScripts().get(activeTab - 1);
			if(container != null){
				setScript();
			}
		}
	}
	
	private void setScript(){
		if(activeTab > 0){
			ScriptContainer container = handler.getScripts().get(activeTab - 1);
			if(container == null)
				handler.getScripts().add(container = new ScriptContainer(handler));
			String text = ((GuiTextArea)get(3)).getText();
			text = text.replace("\r\n", "\n");
			text = text.replace("\r", "\n");
			container.script = text;
		}
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		ListNBT data = compound.getList("Languages", 10);
		Map<String,List<String>> languages = new HashMap<String,List<String>>();
		for(int i = 0; i < data.size(); i++){
			CompoundNBT comp = data.getCompound(i);
			List<String> scripts = new ArrayList<String>();
			ListNBT list = comp.getList("Scripts", 8);
			for(int j = 0; j < list.size(); j++){
				scripts.add(list.getString(j));
			}
			languages.put(comp.getString("Language"), scripts);
		}
		this.languages = languages;
		this.methods = NBTTags.getStringList(compound.getList("Methods", 10));
		init();
	}

	@Override
	public void save() {
		setScript();
	}

	@Override
	public void textUpdate(String text) {
		ScriptContainer container = handler.getScripts().get(activeTab - 1);
		if(container != null)
			container.script = text;
	}

}
