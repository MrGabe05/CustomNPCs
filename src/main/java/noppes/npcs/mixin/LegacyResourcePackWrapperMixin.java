package noppes.npcs.mixin;

import net.minecraft.client.resources.LegacyResourcePackWrapper;
import net.minecraft.resources.IResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LegacyResourcePackWrapper.class)
public interface LegacyResourcePackWrapperMixin {

    @Accessor(value="source")
    IResourcePack getSource();
}
