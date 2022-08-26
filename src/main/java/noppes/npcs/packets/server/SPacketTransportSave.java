package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.roles.RoleTransporter;

public class SPacketTransportSave extends PacketServerBasic {
    private int category;
    private CompoundNBT data;

    public SPacketTransportSave(int category, CompoundNBT data) {
        this.data = data;
        this.category = category;
    }
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketTransportSave msg, PacketBuffer buf) {
        buf.writeInt(msg.category);
        buf.writeNbt(msg.data);
    }

    public static SPacketTransportSave decode(PacketBuffer buf) {
        return new SPacketTransportSave(buf.readInt(), buf.readNbt());
    }

    @Override
    protected void handle() {
        TransportLocation location = TransportController.getInstance().saveLocation(category, data, player, npc);
        if(location != null){
            if(npc.role.getType() != RoleType.TRANSPORTER)
                return;
            RoleTransporter role = (RoleTransporter) npc.role;
            role.setTransport(location);
        }
    }
}