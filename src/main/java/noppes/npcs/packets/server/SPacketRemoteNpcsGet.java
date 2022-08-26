package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollSelected;

import java.text.DecimalFormat;
import java.util.HashMap;

public class SPacketRemoteNpcsGet extends PacketServerBasic {
    public SPacketRemoteNpcsGet() {

    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketRemoteNpcsGet msg, PacketBuffer buf) {

    }

    public static SPacketRemoteNpcsGet decode(PacketBuffer buf) {
        return new SPacketRemoteNpcsGet();
    }

    @Override
    protected void handle() {
        sendNearbyNpcs(player);
        Packets.send(player, new PacketGuiScrollSelected(CustomNpcs.FreezeNPCs?"Unfreeze Npcs":"Freeze Npcs"));
    }

    public static void sendNearbyNpcs(ServerPlayerEntity player) {
        HashMap<String,Integer> map = new HashMap<String,Integer>();
        for(Entity entity : ((ServerWorld)player.level).entitiesByUuid.values()) {
            if(entity instanceof EntityNPCInterface) {
                EntityNPCInterface npc = (EntityNPCInterface) entity;
                if(npc.removed)
                    continue;
                float distance = player.distanceTo(npc);
                DecimalFormat df = new DecimalFormat("#.#");
                String s = df.format(distance);
                if(distance < 10)
                    s = "0" + s;
                map.put(s + " : " + npc.display.getName(), npc.getId());
            }
        }

        NoppesUtilServer.sendScrollData(player, map);
    }
}