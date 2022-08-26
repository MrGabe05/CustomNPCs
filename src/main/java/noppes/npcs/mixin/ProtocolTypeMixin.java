package noppes.npcs.mixin;

import net.minecraft.network.IPacket;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.ProtocolType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ProtocolType.class)
public interface ProtocolTypeMixin {

    @Accessor(value="flows")
    Map<PacketDirection, ? extends Object> getFows();

    @Accessor(value="PROTOCOL_BY_PACKET")
    Map<Class<? extends IPacket<?>>, ProtocolType> getProtocols();
}
