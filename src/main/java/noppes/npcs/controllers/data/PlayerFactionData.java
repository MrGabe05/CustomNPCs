package noppes.npcs.controllers.data;

import java.util.HashMap;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.controllers.FactionController;

public class PlayerFactionData {
	public HashMap<Integer,Integer> factionData = new HashMap<Integer,Integer>();
	
	public void loadNBTData(CompoundNBT compound) {
		HashMap<Integer,Integer> factionData = new HashMap<Integer,Integer>();
		if(compound == null)
			return;
        ListNBT list = compound.getList("FactionData", 10);
        if(list == null){
        	return;
        }

        for(int i = 0; i < list.size(); i++)
        {
            CompoundNBT nbttagcompound = list.getCompound(i);
            factionData.put(nbttagcompound.getInt("Faction"),nbttagcompound.getInt("Points"));
        }
        this.factionData = factionData;
	}

	public void saveNBTData(CompoundNBT compound) {
		ListNBT list = new ListNBT();
		for(int faction : factionData.keySet()){
			CompoundNBT nbttagcompound = new CompoundNBT();
			nbttagcompound.putInt("Faction", faction);
			nbttagcompound.putInt("Points", factionData.get(faction));
			list.add(nbttagcompound);
		}
		
		compound.put("FactionData", list);
	}

	public int getFactionPoints(PlayerEntity player, int factionId) {
		Faction faction = FactionController.instance.getFaction(factionId);
		if(faction == null)
			return 0;
		if(!factionData.containsKey(factionId)){
			if(player.level.isClientSide) {
				factionData.put(factionId, faction.defaultPoints);
				return faction.defaultPoints;
			}
			PlayerScriptData handler = PlayerData.get(player).scriptData;
			PlayerWrapper wrapper = (PlayerWrapper) NpcAPI.Instance().getIEntity(player);
			
			PlayerEvent.FactionUpdateEvent event = new PlayerEvent.FactionUpdateEvent(wrapper, faction, faction.defaultPoints, true);
			EventHooks.OnPlayerFactionChange(handler, event);
			factionData.put(factionId, event.points);
			PlayerData data = PlayerData.get(player);
			data.updateClient = true;
		}
		return factionData.get(factionId);
	}

	public void increasePoints(PlayerEntity player, int factionId, int points) {
		Faction faction = FactionController.instance.getFaction(factionId);
		if(faction == null || player == null || player.level.isClientSide)
			return;
		
		PlayerScriptData handler = PlayerData.get(player).scriptData;
		PlayerWrapper wrapper = (PlayerWrapper) NpcAPI.Instance().getIEntity(player);
		if(!factionData.containsKey(factionId)){
			PlayerEvent.FactionUpdateEvent event = new PlayerEvent.FactionUpdateEvent(wrapper, faction, faction.defaultPoints, true);
			EventHooks.OnPlayerFactionChange(handler, event);
			factionData.put(factionId, event.points);
		}
		PlayerEvent.FactionUpdateEvent event = new PlayerEvent.FactionUpdateEvent(wrapper, faction, points, false);
		EventHooks.OnPlayerFactionChange(handler, event);
		factionData.put(factionId, factionData.get(factionId) + points);
	}
	
	public CompoundNBT getPlayerGuiData(){
		CompoundNBT compound = new CompoundNBT();
		saveNBTData(compound);
		
		ListNBT list = new ListNBT();
		for(int id : factionData.keySet()){
			Faction faction = FactionController.instance.getFaction(id);
			if(faction == null || faction.hideFaction)
				continue;
			CompoundNBT com = new CompoundNBT();
			faction.writeNBT(com);
			list.add(com);
		}
		compound.put("FactionList", list);
		
		return compound;
	}

}
