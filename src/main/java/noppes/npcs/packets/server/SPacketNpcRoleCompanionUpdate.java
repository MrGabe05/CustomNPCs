package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.constants.EnumCompanionStage;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.roles.RoleCompanion;

public class SPacketNpcRoleCompanionUpdate extends PacketServerBasic {
    private final EnumCompanionStage stage;

    public SPacketNpcRoleCompanionUpdate(EnumCompanionStage stage) {
        this.stage = stage;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcRoleCompanionUpdate msg, PacketBuffer buf) {
        buf.writeEnum(msg.stage);
    }

    public static SPacketNpcRoleCompanionUpdate decode(PacketBuffer buf) {
        return new SPacketNpcRoleCompanionUpdate(buf.readEnum(EnumCompanionStage.class));
    }

    @Override
    protected void handle() {
        if(npc.role.getType() != RoleType.COMPANION)
            return;
        ((RoleCompanion)npc.role).matureTo(stage);
        npc.updateClient = true;
    }
}