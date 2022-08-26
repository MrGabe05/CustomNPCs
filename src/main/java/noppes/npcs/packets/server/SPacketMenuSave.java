package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NBTTags;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketMenuSave extends PacketServerBasic {
    private EnumMenuType type;
    private CompoundNBT data;
    public SPacketMenuSave(EnumMenuType type, CompoundNBT data) {
        this.type = type;
        this.data = data;
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
        if(type == EnumMenuType.DISPLAY || type == EnumMenuType.MODEL){
            return CustomNpcsPermissions.NPC_DISPLAY;
        }
        if(type == EnumMenuType.STATS){
            return CustomNpcsPermissions.NPC_STATS;
        }
        if(type == EnumMenuType.INVENTORY){
            return CustomNpcsPermissions.NPC_INVENTORY;
        }
        if(type == EnumMenuType.AI){
            return CustomNpcsPermissions.NPC_AI;
        }
        if(type == EnumMenuType.ADVANCED || type == EnumMenuType.TRANSFORM || type == EnumMenuType.MARK){
            return CustomNpcsPermissions.NPC_ADVANCED;
        }
        if(type == EnumMenuType.MOVING_PATH){
            return CustomNpcsPermissions.TOOL_PATHER;
        }
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketMenuSave msg, PacketBuffer buf) {
        buf.writeEnum(msg.type);
        buf.writeNbt(msg.data);
    }

    public static SPacketMenuSave decode(PacketBuffer buf) {
        return new SPacketMenuSave(buf.readEnum(EnumMenuType.class), buf.readNbt());
    }

    @Override
    protected void handle() {
        if(type == EnumMenuType.DISPLAY){
            npc.display.readToNBT(data);
        }
        if(type == EnumMenuType.STATS){
            npc.stats.readToNBT(data);
        }
        if(type == EnumMenuType.INVENTORY){
            npc.inventory.load(data);
            npc.updateAI = true;
        }
        if(type == EnumMenuType.AI){
            npc.ais.readToNBT(data);
            npc.setHealth(npc.getMaxHealth());
            npc.updateAI = true;
        }
        if(type == EnumMenuType.ADVANCED){
            npc.advanced.readToNBT(data);
            npc.updateAI = true;
        }
        if(type == EnumMenuType.MODEL){
            ((EntityCustomNpc)npc).modelData.load(data);
        }
        if(type == EnumMenuType.TRANSFORM){
            boolean isValid = npc.transform.isValid();
            npc.transform.readOptions(data);
            if(isValid != npc.transform.isValid())
                npc.updateAI = true;
        }
        if(type == EnumMenuType.MOVING_PATH){
            npc.ais.setMovingPath(NBTTags.getIntegerArraySet(data.getList("MovingPathNew",10)));
        }
        if(type == EnumMenuType.MARK){
            MarkData mark = MarkData.get(npc);
            mark.setNBT(data);
            mark.syncClients();
        }
        npc.updateClient = true;
    }
}