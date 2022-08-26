package noppes.npcs.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(VineBlock.class)
public class VineBlockMixin {

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    private void setupAnimPre(BlockState state, ServerWorld world, BlockPos pos, Random r, CallbackInfo ci) {
        if(!CustomNpcs.VineGrowthEnabled) {
            ci.cancel();
        }
    }
}
