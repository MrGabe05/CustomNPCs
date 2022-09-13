package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.roles.RoleTrader;

public class SPacketNpcMarketSet extends PacketServerBasic {
    private final String market;
    private final boolean save;

    public SPacketNpcMarketSet(String market, boolean save) {
        this.market = market;
        this.save = save;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcMarketSet msg, PacketBuffer buf) {
        buf.writeUtf(msg.market);
        buf.writeBoolean(msg.save);
    }

    public static SPacketNpcMarketSet decode(PacketBuffer buf) {
        return new SPacketNpcMarketSet(buf.readUtf(32767), buf.readBoolean());
    }

    @Override
    protected void handle() {
        if(npc.role instanceof RoleTrader){
            if(save) {
                RoleTrader.save((RoleTrader) npc.role, market);
            }
            else {
                RoleTrader.setMarket(npc, market);
            }
        }
    }
}