package noppes.npcs.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.model.animation.AnimationHandler;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobPuppet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableModel.class)
public class AgeableModelMixin<T extends EntityNPCInterface> {

    private boolean isCanceled = false;
    @Inject(at = @At("HEAD"), method = "renderToBuffer", cancellable = true)
    private void renderToBuffer(MatrixStack stack, IVertexBuilder builder, int light, int overlay, float r, float g, float b, float a, CallbackInfo callbackInfo) {
        if(!isCanceled && RenderNPCInterface.currentNpc != null && RenderNPCInterface.currentNpc.display.getTint() < 0xFFFFFF){
            isCanceled = true;
            int color = RenderNPCInterface.currentNpc.display.getTint();
            r = (color >> 16 & 255) / 255f;
            g = (color >> 8  & 255) / 255f;
            b = (color & 255) / 255f;
            AgeableModel model = (AgeableModel)(Object)this;
            model.renderToBuffer(stack, builder, light, overlay, r, g, b, a);
            callbackInfo.cancel();
            return;
        }
        isCanceled = false;
    }
}