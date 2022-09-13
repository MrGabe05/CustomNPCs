package noppes.npcs.shared.common;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class PacketBasic {

    public PlayerEntity player;
    public Supplier<NetworkEvent.Context> ctx;

    public static void handle(final PacketBasic msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            msg.ctx = ctx;
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> msg::handleClient);
        });
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient(){
        player = Minecraft.getInstance().player;
        handle();
    }

    public abstract void handle();
}
