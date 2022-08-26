package noppes.npcs.shared.client.gui.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public interface IScrollData {
	public void setData(Vector<String> list, Map<String,Integer> data);
	public void setSelected(String selected);
}
