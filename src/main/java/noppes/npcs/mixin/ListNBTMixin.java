package noppes.npcs.mixin;

import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ListNBT.class)
public interface ListNBTMixin {

    @Accessor(value="list")
    List<INBT> getList();
}
