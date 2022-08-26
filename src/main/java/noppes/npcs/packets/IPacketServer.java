package noppes.npcs.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

public abstract class IPacketServer implements IPacket<IServerPlayNetHandler> {
    private static final Logger LOGGER = LogManager.getLogger();
    public ServerPlayerEntity player;
    public EntityNPCInterface npc;

    @Override
    public void handle(IServerPlayNetHandler handler) {
        enqueueWork(() -> {
            try {
                player = ((ServerPlayNetHandler) handler).player;
                npc = NoppesUtilServer.getEditingNpc(player);

                if (requiresNpc() && npc == null) {
                    return;
                }
                if (getPermission() != null && !CustomNpcsPermissions.hasPermission(player, getPermission())) {
                    return;
                }
                if (!toolAllowed(player.inventory.getSelected())) {
                    warn("tried to use custom npcs without a tool in hand, possibly a hacker");
                    return;
                }
                handle();
            } catch (Throwable e) {
                LogWriter.except(e);
                throw e;
            }
        });
    }

    public boolean requiresNpc(){
        return false;
    }

    public CustomNpcsPermissions.Permission getPermission(){
        return null;
    }

    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.wand;
    }

    public abstract void handle();

    private void warn(String warning){
        LOGGER.warn(player.getName().getString() + ": " + warning + " - " + this);
    }

    public CompletableFuture<Void> enqueueWork(Runnable runnable) {
        ThreadTaskExecutor<?> executor = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
        // Must check ourselves as Minecraft will sometimes delay tasks even when they are received on the client thread
        // Same logic as ThreadTaskExecutor#runImmediately without the join
        if (!executor.isSameThread()) {
            return executor.submitAsync(runnable); // Use the internal method so thread check isn't done twice
        } else {
            runnable.run();
            return CompletableFuture.completedFuture(null);
        }
    }
}
