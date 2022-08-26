package noppes.npcs.packets.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumPlayerData;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.data.*;
import noppes.npcs.packets.PacketServerBasic;

import java.io.File;

public class SPacketPlayerDataRemove extends PacketServerBasic {
    private EnumPlayerData type;
    private String name;
    private int id;
    public SPacketPlayerDataRemove(EnumPlayerData type, String name, int id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_PLAYERDATA;
    }

    public static void encode(SPacketPlayerDataRemove msg, PacketBuffer buf) {
        buf.writeEnum(msg.type);
        buf.writeUtf(msg.name);
        buf.writeInt(msg.id);
    }

    public static SPacketPlayerDataRemove decode(PacketBuffer buf) {
        return new SPacketPlayerDataRemove(buf.readEnum(EnumPlayerData.class), buf.readUtf(32767), buf.readInt());
    }

    @Override
    protected void handle() {
        if(name == null || name.isEmpty())
            return;
        PlayerEntity pl = player.getServer().getPlayerList().getPlayerByName(name);
        PlayerData playerdata = null;
        if(pl == null)
            playerdata = PlayerDataController.instance.getDataFromUsername(player.getServer(), name);
        else
            playerdata = PlayerData.get(pl);

        if(type == EnumPlayerData.Players){
            File file = new File(CustomNpcs.getWorldSaveDirectory("playerdata"), playerdata.uuid + ".json");
            if(file.exists())
                file.delete();
            if(pl != null){
                playerdata.setNBT(new CompoundNBT());
                SPacketPlayerDataGet.sendPlayerData(type, player, name);
                playerdata.save(true);
                return;
            }
            else {
                PlayerDataController.instance.nameUUIDs.remove(name);
            }
        }
        if(type == EnumPlayerData.Quest){
            PlayerQuestData data = playerdata.questData;
            data.activeQuests.remove(id);
            data.finishedQuests.remove(id);
            playerdata.save(true);
        }
        if(type == EnumPlayerData.Dialog){
            PlayerDialogData data = playerdata.dialogData;
            data.dialogsRead.remove(id);
            playerdata.save(true);
        }
        if(type == EnumPlayerData.Transport){
            PlayerTransportData data = playerdata.transportData;
            data.transports.remove(id);
            playerdata.save(true);
        }
        if(type == EnumPlayerData.Bank){
            PlayerBankData data = playerdata.bankData;
            data.banks.remove(id);
            playerdata.save(true);
        }
        if(type == EnumPlayerData.Factions){
            PlayerFactionData data = playerdata.factionData;
            data.factionData.remove(id);
            playerdata.save(true);
        }
        if(pl != null) {
            SyncController.syncPlayer((ServerPlayerEntity) pl);
        }
        SPacketPlayerDataGet.sendPlayerData(type, player, name);
    }

}