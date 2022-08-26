package net.minecraft.world.server;

import net.minecraft.util.math.ChunkPos;

public class CNpcsChunkManagerHelper {

    public static boolean canSpawn(ChunkManager manager, ChunkPos pos){
        return !manager.noPlayersCloseForSpawning(pos) || manager.getDistanceManager().shouldForceTicks(pos.toLong());
    }
}
