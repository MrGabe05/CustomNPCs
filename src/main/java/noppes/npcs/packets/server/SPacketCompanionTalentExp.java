package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.roles.RoleCompanion;

public class SPacketCompanionTalentExp extends PacketServerBasic {
    private final EnumCompanionTalent talent;
    private final int exp;

    public SPacketCompanionTalentExp(EnumCompanionTalent talent, int exp) {
        this.talent = talent;
        this.exp = exp;
    }
    @Override
    public boolean requiresNpc(){
        return true;
    }

    public static void encode(SPacketCompanionTalentExp msg, PacketBuffer buf) {
        buf.writeEnum(msg.talent);
        buf.writeInt(msg.exp);
    }

    public static SPacketCompanionTalentExp decode(PacketBuffer buf) {
        return new SPacketCompanionTalentExp(buf.readEnum(EnumCompanionTalent.class), buf.readInt());
    }

    @Override
    protected void handle() {
        if(npc.role.getType() != RoleType.COMPANION || player != npc.getOwner())
            return;
        RoleCompanion role = (RoleCompanion) npc.role;
        if(exp <= 0 || !role.canAddExp(-exp))
            return;
        role.addExp(-exp);
        role.addTalentExp(talent, exp);
    }
}