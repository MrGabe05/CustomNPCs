package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketNpcDialogsGet extends PacketServerBasic {
    public SPacketNpcDialogsGet() {

    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketNpcDialogsGet msg, PacketBuffer buf) {

    }

    public static SPacketNpcDialogsGet decode(PacketBuffer buf) {
        return new SPacketNpcDialogsGet();
    }

    @Override
    protected void handle() {
        for(int pos : npc.dialogs.keySet()){
            DialogOption option = npc.dialogs.get(pos);
            if(option == null || !option.hasDialog())
                continue;

            CompoundNBT compound = option.writeNBT();
            compound.putInt("Position", pos);
            Packets.send(player, new PacketGuiData(compound));

        }
    }
}