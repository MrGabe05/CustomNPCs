package noppes.npcs.controllers;

import com.google.common.util.concurrent.ListenableFutureTask;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.CustomItems;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class VisibilityController {
    public static VisibilityController instance = new VisibilityController();
    private Map<Integer, EntityNPCInterface> trackedEntityHashTable = new ConcurrentHashMap<Integer, EntityNPCInterface>();


    public VisibilityController(){
    	trackedEntityHashTable = new TreeMap<Integer, EntityNPCInterface>();
    }
    
    public void trackNpc(EntityNPCInterface npc) {
    	boolean hasOptions = npc.display.availability.hasOptions();
        if((hasOptions || npc.display.getVisible() != 0) && !trackedEntityHashTable.containsKey(npc.getId())) {
            trackedEntityHashTable.put(npc.getId(), npc);
        }
        if(!hasOptions && npc.display.getVisible() == 0 && trackedEntityHashTable.containsKey(npc.getId())) {
            trackedEntityHashTable.remove(npc.getId());
        }
    }

    public void onUpdate(ServerPlayerEntity player){
        for(Map.Entry<Integer, EntityNPCInterface> entry : trackedEntityHashTable.entrySet()){
            checkIsVisible(entry.getValue(), player);
        }
    }

    public static void checkIsVisible(EntityNPCInterface npc, ServerPlayerEntity playerMP){
        if(npc.display.isVisibleTo(playerMP) || playerMP.isSpectator() || playerMP.getMainHandItem().getItem() == CustomItems.wand){
            npc.setVisible(playerMP);
        }
        else{
            npc.setInvisible(playerMP);
        }
    }    
    

    public static void addValue(HashMap<Integer, ArrayList<EntityNPCInterface>> map, int id, EntityNPCInterface npc){
        if(!map.containsKey(id)){
            map.put(id, new ArrayList<EntityNPCInterface>());
        }
        ArrayList<EntityNPCInterface> npcs=map.get(id);
        if(!npcs.contains(npc)){
            npcs.add(npc);
            map.replace(id, npcs);
        }
    }
}
