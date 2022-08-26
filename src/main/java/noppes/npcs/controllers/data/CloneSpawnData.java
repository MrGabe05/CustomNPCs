package noppes.npcs.controllers.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.controllers.ServerCloneController;

import java.util.HashMap;
import java.util.Map;

public class CloneSpawnData {
    public int tab;
    public String name;

    private long lastLoaded;
    private CompoundNBT compound;

    public CloneSpawnData(int tab, String name){
        this.name = name;
        this.tab = tab;
    }

    public CompoundNBT getCompound(){
        if(lastLoaded < ServerCloneController.Instance.lastLoaded){
            compound = ServerCloneController.Instance.getCloneData(null, name, tab);
            lastLoaded = ServerCloneController.Instance.lastLoaded;
        }
        return compound;
    }

    public static Map<Integer, CloneSpawnData> load(ListNBT list){
        Map<Integer, CloneSpawnData> data = new HashMap<>();
        for(int i = 0; i < list.size(); i++){
            CompoundNBT c = list.getCompound(i);
            int tab = c.getInt("tab");
            String name = c.getString("name");
            if(ServerCloneController.Instance == null || ServerCloneController.Instance.hasClone(tab, name)){
                data.put(c.getInt("slot"), new CloneSpawnData(tab, name));
            }
        }
        return data;
    }

    public static ListNBT save(Map<Integer, CloneSpawnData> data){
        ListNBT list = new ListNBT();
        for(Map.Entry<Integer, CloneSpawnData> entry : data.entrySet()){
            if(ServerCloneController.Instance != null && !ServerCloneController.Instance.hasClone(entry.getValue().tab, entry.getValue().name)){
                continue;
            }
            CompoundNBT c = new CompoundNBT();
            c.putInt("slot", entry.getKey());
            c.putInt("tab", entry.getValue().tab);
            c.putString("name", entry.getValue().name);
            list.add(c);
        }
        return list;
    }
}
