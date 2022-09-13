package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketMenuGet extends PacketServerBasic {
    private final EnumMenuType type;
    public SPacketMenuGet(EnumMenuType type) {
        this.type = type;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        if(type == EnumMenuType.MOVING_PATH)
            return item.getItem() == CustomItems.moving;
        return item.getItem() == CustomItems.wand;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketMenuGet msg, PacketBuffer buf) {
        buf.writeEnum(msg.type);
    }

    public static SPacketMenuGet decode(PacketBuffer buf) {
        return new SPacketMenuGet(buf.readEnum(EnumMenuType.class));
    }

    @Override
    protected void handle() {
        CompoundNBT data = new CompoundNBT();
        if(type == EnumMenuType.DISPLAY){
            npc.display.save(data);
        }
        if(type == EnumMenuType.STATS){
            npc.stats.save(data);
        }
        if(type == EnumMenuType.INVENTORY){
            npc.inventory.save(data);
        }
        if(type == EnumMenuType.AI || type == EnumMenuType.MOVING_PATH){
            npc.ais.save(data);
        }
        if(type == EnumMenuType.ADVANCED){
            npc.advanced.save(data);
        }
        if(type == EnumMenuType.TRANSFORM){
            npc.transform.writeOptions(data);
        }

        Packets.send(player, new PacketGuiData(data));
    }
}