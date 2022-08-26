package noppes.npcs.controllers;

import java.util.List;
import java.util.Map;

import net.minecraftforge.eventbus.api.Event;
import noppes.npcs.constants.EnumScriptType;

public interface IScriptHandler {
	
	public void runScript(EnumScriptType type, Event event);

	public boolean isClient();
	
	public boolean getEnabled();
	
	public void setEnabled(boolean bo);
	
	public String getLanguage();
	
	public void setLanguage(String lang);
	
	public List<ScriptContainer> getScripts();
	
	public String noticeString();

	public Map<Long, String> getConsoleText();

	public void clearConsole();
}
