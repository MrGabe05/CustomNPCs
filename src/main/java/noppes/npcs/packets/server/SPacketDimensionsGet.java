package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.packets.PacketServerBasic;

import java.util.HashMap;

public class SPacketDimensionsGet extends PacketServerBasic {


    public SPacketDimensionsGet() {

    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.teleporter;
    }

    public static void encode(SPacketDimensionsGet msg, PacketBuffer buf) {

    }

    public static SPacketDimensionsGet decode(PacketBuffer buf) {
        return new SPacketDimensionsGet();
    }

    @Override
    protected void handle() {
        HashMap<String,Integer> map = new HashMap<String,Integer>();

        for(RegistryKey<World> key : CustomNpcs.Server.levelKeys()){
            map.put(key.location().toString(), 0);
        }
        NoppesUtilServer.sendScrollData(player, map);
    }

}