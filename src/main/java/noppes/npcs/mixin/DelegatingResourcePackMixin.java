package noppes.npcs.mixin;

import net.minecraft.resources.IResourcePack;
import net.minecraftforge.fml.packs.DelegatingResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(DelegatingResourcePack.class)
public interface DelegatingResourcePackMixin {

    @Accessor(value="namespacesAssets")
    Map<String, List<IResourcePack>> getAssetPacks();
}
