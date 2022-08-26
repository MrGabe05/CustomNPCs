package noppes.npcs.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.ModelRenderer;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.constants.EnumParts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelRenderer.class)
public class ModelRendererMixin {
    public ModelPartConfig cnpcconfig;

    @Inject(at = @At("HEAD"), method = "translateAndRotate(Lcom/mojang/blaze3d/matrix/MatrixStack;)V")
    private void translateAndRotatePre(MatrixStack mStack, CallbackInfo callbackInfo) {
        cnpcconfig = getCnpcconfig();
        if(cnpcconfig != null) {
            mStack.translate(cnpcconfig.transX, cnpcconfig.transY, cnpcconfig.transZ);
        }
    }

    @Inject(at = @At("TAIL"), method = "translateAndRotate(Lcom/mojang/blaze3d/matrix/MatrixStack;)V")
    private void translateAndRotatePost(MatrixStack mStack, CallbackInfo callbackInfo) {
        cnpcconfig = getCnpcconfig();
        if(cnpcconfig != null) {
            mStack.scale(cnpcconfig.scaleX, cnpcconfig.scaleY, cnpcconfig.scaleZ);
        }
    }

    private ModelPartConfig getCnpcconfig(){
        if(ClientProxy.data == null){
            return null;
        }
        ModelRenderer model = (ModelRenderer)(Object)this;
        if(model == ClientProxy.playerModel.body || model == ClientProxy.playerModel.jacket || model == ClientProxy.armorLayer.getOuter().body || model == ClientProxy.armorLayer.getInner().body){
            return ClientProxy.data.getPartConfig(EnumParts.BODY);
        }
        if(model == ClientProxy.playerModel.head || model == ClientProxy.playerModel.hat || model == ClientProxy.armorLayer.getOuter().head){
            return ClientProxy.data.getPartConfig(EnumParts.HEAD);
        }
        if(model == ClientProxy.playerModel.leftLeg || model == ClientProxy.playerModel.leftPants || model == ClientProxy.armorLayer.getOuter().leftLeg || model == ClientProxy.armorLayer.getInner().leftLeg ){
            return ClientProxy.data.getPartConfig(EnumParts.LEG_LEFT);
        }
        if(model == ClientProxy.playerModel.rightLeg || model == ClientProxy.playerModel.rightPants || model == ClientProxy.armorLayer.getOuter().rightLeg || model == ClientProxy.armorLayer.getInner().rightLeg){
            return ClientProxy.data.getPartConfig(EnumParts.LEG_RIGHT);
        }
        if(model == ClientProxy.playerModel.leftArm || model == ClientProxy.playerModel.leftSleeve || model == ClientProxy.armorLayer.getOuter().leftArm){
            return ClientProxy.data.getPartConfig(EnumParts.ARM_LEFT);
        }
        if(model == ClientProxy.playerModel.rightArm || model == ClientProxy.playerModel.rightSleeve || model == ClientProxy.armorLayer.getOuter().rightArm){
            return ClientProxy.data.getPartConfig(EnumParts.ARM_RIGHT);
        }
        return null;
    }
}