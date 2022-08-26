package noppes.npcs.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.NpcBlockHelper;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {

    @Inject(at = @At("HEAD"), method = "isRandomlyTicking", cancellable = true)
    private void setupAnimPre(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if(!CustomNpcs.LeavesDecayEnabled) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
