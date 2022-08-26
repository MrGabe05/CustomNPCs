package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNaturalSpawnSave extends PacketServerBasic
{
    private CompoundNBT data;
    public SPacketNaturalSpawnSave(CompoundNBT data) {
        this.data = data;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_NATURALSPAWN;
    }

    public static void encode(SPacketNaturalSpawnSave msg, PacketBuffer buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketNaturalSpawnSave decode(PacketBuffer buf) {
        return new SPacketNaturalSpawnSave(buf.readNbt());
    }

    @Override
    protected void handle() {
        SpawnData sdata = new SpawnData();
        sdata.readNBT(data);
        SpawnController.instance.saveSpawnData(sdata);
        NoppesUtilServer.sendScrollData(player, SpawnController.instance.getScroll());
    }
}