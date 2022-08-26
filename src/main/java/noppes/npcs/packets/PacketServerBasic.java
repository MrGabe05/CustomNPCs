package noppes.npcs.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public abstract class PacketServerBasic {
    private static final Logger LOGGER = LogManager.getLogger();
    public ServerPlayerEntity player;
    public EntityNPCInterface npc;

    public boolean requiresNpc(){
        return false;
    }

    public CustomNpcsPermissions.Permission getPermission(){
        return null;
    }

    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.wand;
    }

    public static void handle(final PacketServerBasic msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            msg.player = ctx.get().getSender();
            msg.npc = NoppesUtilServer.getEditingNpc(msg.player);

            if (msg.requiresNpc() && msg.npc == null) {
                return;
            }
            if (msg.getPermission() != null && !CustomNpcsPermissions.hasPermission(msg.player, msg.getPermission())) {
                return;
            }
            if (!msg.toolAllowed(msg.player.inventory.getSelected())) {
                msg.warn("tried to use custom npcs without a tool in hand, possibly a hacker");
                return;
            }
            msg.handle();
        });
        ctx.get().setPacketHandled(true);
    }

    private void warn(String warning){
        LOGGER.warn(player.getName().getString() + ": " + warning + " - " + this);
    }

    protected abstract void handle();

}
