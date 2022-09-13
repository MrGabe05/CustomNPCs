package noppes.npcs.shared.client.gui.listeners;

import java.util.Map;
import java.util.Vector;

public interface IScrollData {
	void setData(Vector<String> list, Map<String, Integer> data);
	void setSelected(String selected);
}
