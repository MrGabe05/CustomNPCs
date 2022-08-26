package noppes.npcs.roles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.controllers.ChunkController;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.List;

public class JobChunkLoader extends JobInterface{
	
	private List<ChunkPos> chunks = new ArrayList<ChunkPos>();
	private int ticks = 20;
	private long playerLastSeen = -1;

	public JobChunkLoader(EntityNPCInterface npc) {
		super(npc);
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putLong("ChunkPlayerLastSeen", playerLastSeen);
		return compound;
	}

	@Override
	public void load(CompoundNBT compound) {
		playerLastSeen = compound.getLong("ChunkPlayerLastSeen");
	}

	@Override
	public boolean aiShouldExecute() {
		ticks--;
		if(ticks > 0)
			return false;
		ticks = 20;
		
		List players = npc.level.getEntitiesOfClass(PlayerEntity.class, npc.getBoundingBox().inflate(48, 48, 48));
		if(!players.isEmpty())
			playerLastSeen = System.currentTimeMillis();

		if(playerLastSeen < 0){
			return false;
		}
		//unload after 10 min
		if(System.currentTimeMillis() > playerLastSeen + 600000){
			ChunkController.instance.unload((ServerWorld) npc.level, npc.getUUID(), npc.xChunk, npc.zChunk);
			chunks.clear();
			playerLastSeen = -1;
			return false;
		}
		double x = npc.getX() / 16;
		double z = npc.getZ() / 16;

		List<ChunkPos> list = new ArrayList<ChunkPos>();
		list.add(new ChunkPos(MathHelper.floor(x), MathHelper.floor(z)));
		list.add(new ChunkPos(MathHelper.ceil(x), MathHelper.ceil(z)));
		list.add(new ChunkPos(MathHelper.floor(x), MathHelper.ceil(z)));
		list.add(new ChunkPos(MathHelper.ceil(x), MathHelper.floor(z)));

		for(ChunkPos chunk : list){
			if(!chunks.contains(chunk)){
				ChunkController.instance.load((ServerWorld) npc.level, npc.getUUID(), chunk.x, chunk.z);
			}
			chunks.remove(chunk);
		}

		for(ChunkPos chunk : chunks){
			ChunkController.instance.unload((ServerWorld) npc.level, npc.getUUID(), chunk.x, chunk.z);
		}

		this.chunks = list;
		return false;
	}
	
	@Override
	public boolean aiContinueExecute() {
		return false;
	}

	@Override
	public void reset() {
		if(npc.level instanceof ServerWorld){
			ChunkController.instance.unload((ServerWorld) npc.level, npc.getUUID(), npc.xChunk, npc.zChunk);
			chunks.clear();
			playerLastSeen = 0;
		}
	}
	public void delete() {
	}

	@Override
	public int getType() {
		return JobType.CHUNKLOADER;
	}
}
