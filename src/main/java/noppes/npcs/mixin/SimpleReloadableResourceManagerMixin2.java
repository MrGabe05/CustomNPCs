package noppes.npcs.mixin;

import net.minecraft.resources.FolderPack;
import net.minecraft.resources.SimpleReloadableResourceManager;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleReloadableResourceManager.class)
public class SimpleReloadableResourceManagerMixin2 {

//    @Inject(at = @At("RETURN"), method = "createFullReload", cancellable = false)
//    private void setupAnimPre(Executor e, Executor e2, CompletableFuture<Unit> c, List<IResourcePack> list, CallbackInfoReturnable<IAsyncReloader> ci) {
//        SimpleReloadableResourceManager manager = (SimpleReloadableResourceManager)(Object)this;
//        manager.add(new FolderPack(CustomNpcs.Dir));
//        //Minecraft.getInstance().submitAsync(() -> manager.add(new FolderPack(CustomNpcs.Dir)));
//    }

    @Inject(at = @At("TAIL"), method = "clear", cancellable = false)
    private void clear(CallbackInfo ci) {
        if(CustomNpcs.Dir != null){
            SimpleReloadableResourceManager manager = (SimpleReloadableResourceManager)(Object)this;
            manager.add(new FolderPack(CustomNpcs.Dir));
        }
        //Minecraft.getInstance().submitAsync(() -> manager.add(new FolderPack(CustomNpcs.Dir)));
    }
}
