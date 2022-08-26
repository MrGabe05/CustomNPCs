package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumPlayerData;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.data.*;
import noppes.npcs.packets.PacketServerBasic;

import java.util.HashMap;
import java.util.Map;

public class SPacketPlayerDataGet extends PacketServerBasic {
    private EnumPlayerData type;
    private String name;
    public SPacketPlayerDataGet(EnumPlayerData type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketPlayerDataGet msg, PacketBuffer buf) {
        buf.writeEnum(msg.type);
        buf.writeUtf(msg.name);
    }

    public static SPacketPlayerDataGet decode(PacketBuffer buf) {
        return new SPacketPlayerDataGet(buf.readEnum(EnumPlayerData.class), buf.readUtf(32767));
    }

    @Override
    protected void handle() {
        sendPlayerData(type, player, name);
    }

    public static void sendPlayerData(EnumPlayerData type, ServerPlayerEntity player, String name) {
        Map<String,Integer> map = new HashMap<String,Integer>();

        if(type == EnumPlayerData.Players){
            for(String username : PlayerDataController.instance.nameUUIDs.keySet()){
                map.put(username, 0);
            }
            for(String username : player.getServer().getPlayerList().getPlayerNamesArray()){
                map.put(username, 0);
            }
        }
        else{
            PlayerData playerdata = PlayerDataController.instance.getDataFromUsername(player.getServer(), name);
            if(type == EnumPlayerData.Dialog){
                PlayerDialogData data = playerdata.dialogData;

                for(int questId : data.dialogsRead){
                    Dialog dialog = DialogController.instance.dialogs.get(questId);
                    if(dialog == null)
                        continue;
                    map.put(dialog.category.title + ": " + dialog.title,questId);
                }
            }
            else if(type == EnumPlayerData.Quest){
                PlayerQuestData data = playerdata.questData;

                for(int questId : data.activeQuests.keySet()){
                    Quest quest = QuestController.instance.quests.get(questId);
                    if(quest == null)
                        continue;
                    map.put(quest.category.title + ": " + quest.title + "(Active quest)",questId);
                }
                for(int questId : data.finishedQuests.keySet()){
                    Quest quest = QuestController.instance.quests.get(questId);
                    if(quest == null)
                        continue;
                    map.put(quest.category.title + ": " + quest.title + "(Finished quest)",questId);
                }
            }
            else if(type == EnumPlayerData.Transport){
                PlayerTransportData data = playerdata.transportData;

                for(int questId : data.transports){
                    TransportLocation location = TransportController.getInstance().getTransport(questId);
                    if(location == null)
                        continue;
                    map.put(location.category.title + ": " + location.name,questId);
                }
            }
            else if(type == EnumPlayerData.Bank){
                PlayerBankData data = playerdata.bankData;

                for(int bankId : data.banks.keySet()){
                    Bank bank = BankController.getInstance().banks.get(bankId);
                    if(bank == null)
                        continue;
                    map.put(bank.name,bankId);
                }
            }
            else if(type == EnumPlayerData.Factions){
                PlayerFactionData data = playerdata.factionData;
                for(int factionId : data.factionData.keySet()){
                    Faction faction = FactionController.instance.factions.get(factionId);
                    if(faction == null)
                        continue;
                    map.put(faction.name + "(" + data.getFactionPoints(player, factionId) + ")" ,factionId);
                }
            }
        }

        NoppesUtilServer.sendScrollData(player, map);
    }
}