package noppes.npcs.mixin;

import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingRenderer.class)
public class LivingRendererMixin<T extends EntityCustomNpc, M extends BipedModel<T>> {

    @Inject(at = @At("HEAD"), method = "addLayer")
    private void spawnOriginalMobs(LayerRenderer<T, M> layer, CallbackInfoReturnable<Boolean> cir) {
        LivingRenderer renderer = (LivingRenderer)(Object)this;
        if(renderer instanceof RenderCustomNpc){
            ((RenderCustomNpc)renderer).npclayers.add(layer);
        }
    }
}
