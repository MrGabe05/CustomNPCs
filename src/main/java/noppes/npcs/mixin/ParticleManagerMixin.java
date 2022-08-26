package noppes.npcs.mixin;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ParticleManager.class)
public interface ParticleManagerMixin {


    @Accessor(value="spriteSets")
    Map<ResourceLocation, IAnimatedSprite> getPacks();

}
