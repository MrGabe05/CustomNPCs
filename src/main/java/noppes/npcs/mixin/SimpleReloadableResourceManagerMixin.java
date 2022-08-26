package noppes.npcs.mixin;

import net.minecraft.nbt.INBT;
import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(SimpleReloadableResourceManager.class)
public interface SimpleReloadableResourceManagerMixin {

    @Accessor(value="namespacedPacks")
    Map<String, FallbackResourceManager> getPacks();
}
