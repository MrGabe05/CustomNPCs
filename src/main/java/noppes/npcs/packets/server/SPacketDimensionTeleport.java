package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.DimensionType;
import net.minecraft.world.gen.Heightmap;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomTeleporter;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketDimensionTeleport extends PacketServerBasic {
    private final ResourceLocation id;

    public SPacketDimensionTeleport(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.teleporter;
    }

    public static void encode(SPacketDimensionTeleport msg, PacketBuffer buf) {
        buf.writeResourceLocation(msg.id);
    }

    public static SPacketDimensionTeleport decode(PacketBuffer buf) {
        return new SPacketDimensionTeleport(buf.readResourceLocation());
    }

    @Override
    protected void handle() {
        RegistryKey<World> dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, id);
        ServerWorld level = player.getServer().getLevel(dimension);
        BlockPos coords = level.getSharedSpawnPos();
        if(coords == null){
            coords = level.getSharedSpawnPos();
            if(!level.isEmptyBlock(coords)){
                coords = level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, coords);
            }
            else{
                while(level.isEmptyBlock(coords) && coords.getY() > 0){
                    coords = coords.below();
                }
                if(coords.getY() == 0)
                    coords = level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, coords);
            }
        }
        teleportPlayer(player, coords.getX(), coords.getY(), coords.getZ(), dimension);
    }

    public static void teleportPlayer(ServerPlayerEntity player, double x, double y, double z, RegistryKey<World> dimension){
        if(player.level.dimension != dimension){
            MinecraftServer server = player.getServer();
            ServerWorld wor = server.getLevel(dimension);
            if(wor == null){
                player.sendMessage(new StringTextComponent("Broken transporter. Dimenion does not exist"), Util.NIL_UUID);
                return;
            }
            player.moveTo(x, y, z, player.yRot, player.xRot);
            player.changeDimension(wor, new CustomTeleporter(wor, new Vector3d(x, y, z), player.yRot, player.xRot));
            //player.connection.teleport(x, y, z, player.yRot, player.xRot);

//            if(!wor.players.contains(player))
//                wor.addFreshEntity(player);
        }
        else{
            player.connection.teleport(x, y, z, player.yRot, player.xRot);
        }

        //player.level.tickEntity(player, false);
    }

}