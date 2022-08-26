package noppes.npcs.controllers;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.CustomNpcs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChunkController //implements LoadingCallback
{
	public static ChunkController instance = new ChunkController();

	private HashMap<Long, List<UUID>> loaded = new HashMap<>();

	public ChunkController(){
		instance = this;
	}

	public void clear(){
		loaded = new HashMap<>();
	}

	public void unload(ServerWorld world, UUID id, int xChunk, int zChunk) {
		long i = ChunkPos.asLong(xChunk, zChunk);
		List<UUID> list = loaded.get(i);
		if(list == null)
			return;
		list.remove(id);
		if(list.size() == 0){
			world.setChunkForced(xChunk,zChunk, false);
			loaded.remove(i);
		}
	}

	public void load(ServerWorld world, UUID id, int xChunk, int zChunk) {
		if(size() >= CustomNpcs.ChuckLoaders)
			return;
		long i = ChunkPos.asLong(xChunk, zChunk);
		List<UUID> list = loaded.get(i);
		if(list == null){
			loaded.put(i, list = new ArrayList<>());
		}
		list.add(id);
		if(list.size() == 1){
			world.setChunkForced(xChunk,zChunk, true);
		}
	}

	public int size() {
		return loaded.size();
	}
}
