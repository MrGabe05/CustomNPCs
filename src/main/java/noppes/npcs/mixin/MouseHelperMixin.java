package noppes.npcs.mixin;

import net.minecraft.client.MouseHelper;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(MouseHelper.class)
public interface MouseHelperMixin {

    @Accessor(value="activeButton")
    int getActiveButton();

    @Accessor(value="mouseGrabbed")
    void setGrabbed(boolean bo);

    @Accessor(value="xpos")
    void setX(double x);

    @Accessor(value="ypos")
    void setY(double y);
}
