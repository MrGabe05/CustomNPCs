package noppes.npcs.mixin;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import noppes.npcs.NPCSpawning;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {

    @Inject(at = @At("HEAD"), method = "spawnOriginalMobs", cancellable = false)
    private void spawnOriginalMobs(WorldGenRegion region, CallbackInfo ci) {
        int x = region.getCenterX();
        int z = region.getCenterZ();
        Biome biome = region.getBiome(new ChunkPos(x, z).getWorldPosition());
        NPCSpawning.performWorldGenSpawning(region.getLevel(), biome, x, z, region.getRandom());
    }
}
