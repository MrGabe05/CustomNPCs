package noppes.npcs.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(IceBlock.class)
public class IceBlockMixin {

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    private void setupAnimPre(BlockState state, ServerWorld world, BlockPos pos, Random r, CallbackInfo ci) {
        if(!CustomNpcs.IceMeltsEnabled) {
            ci.cancel();
        }
    }
}
