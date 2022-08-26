package noppes.npcs.shared.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUHashMap<K, V> extends LinkedHashMap<K, V>  {
    private final int maxSize;

    public LRUHashMap(int size) {
		super(size, 0.75f, true);
        this.maxSize = size;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    	return size() > maxSize;
    }
}
