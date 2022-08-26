package noppes.npcs.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketAchievement;
import noppes.npcs.packets.client.PacketChat;

import java.util.Collection;

public class CmdQuest{

    public static LiteralArgumentBuilder<CommandSource> register() {
        LiteralArgumentBuilder<CommandSource> command = Commands.literal("quest");

        command.then(Commands.literal("start").then(Commands.argument("players", EntityArgument.players()).requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("quest", IntegerArgumentType.integer(0)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
            if(players.isEmpty())
                return 1;

            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger(context, "quest"));
            if (quest == null){
                throw new CommandException(new StringTextComponent("Unknown QuestID"));
            }
            for(ServerPlayerEntity player : players){
                PlayerData data = PlayerData.get(player);
                QuestData questdata = new QuestData(quest);
                data.questData.activeQuests.put(quest.id, questdata);
                data.save(true);
                Packets.send(player, new PacketAchievement(new TranslationTextComponent("quest.newquest"), new TranslationTextComponent(quest.title), 2));
                Packets.send(player, new PacketChat(new TranslationTextComponent("quest.newquest").append(":").append(new TranslationTextComponent(quest.title))));
            }
            return 1;
        }))));

        command.then(Commands.literal("finish").then(Commands.argument("players", EntityArgument.players()).requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("quest", IntegerArgumentType.integer(0)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
            if(players.isEmpty())
                return 1;

            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger(context, "quest"));
            if (quest == null){
                throw new CommandException(new StringTextComponent("Unknown QuestID"));
            }
            for(ServerPlayerEntity player : players){
                PlayerData data = PlayerData.get(player);
                data.questData.finishedQuests.put(quest.id, System.currentTimeMillis());
                data.save(true);
            }
            return 1;
        }))));

        command.then(Commands.literal("stop").then(Commands.argument("players", EntityArgument.players()).requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("quest", IntegerArgumentType.integer(0)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
            if(players.isEmpty())
                return 1;

            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger(context, "quest"));
            if (quest == null){
                throw new CommandException(new StringTextComponent("Unknown QuestID"));
            }
            for(ServerPlayerEntity player : players){
                PlayerData data = PlayerData.get(player);
                data.questData.activeQuests.remove(quest.id);
                data.save(true);
            }
            return 1;
        }))));

        command.then(Commands.literal("remove").then(Commands.argument("players", EntityArgument.players()).requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("quest", IntegerArgumentType.integer(0)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
            if(players.isEmpty())
                return 1;

            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger(context, "quest"));
            if (quest == null){
                throw new CommandException(new StringTextComponent("Unknown QuestID"));
            }
            for(ServerPlayerEntity player : players){
                PlayerData data = PlayerData.get(player);
                data.questData.activeQuests.remove(quest.id);
                data.questData.finishedQuests.remove(quest.id);
                data.save(true);
            }
            return 1;
        }))));

        command.then(Commands.literal("objective").then(Commands.argument("players", EntityArgument.players()).requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("quest", IntegerArgumentType.integer(0)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
            if(players.isEmpty())
                return 1;

            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger(context, "quest"));
            if (quest == null){
                throw new CommandException(new StringTextComponent("Unknown QuestID"));
            }
            for(ServerPlayerEntity player : players){
                PlayerData data = PlayerData.get(player);
                if(data.questData.activeQuests.containsKey(quest.id)) {
                    IQuestObjective[] objectives = quest.questInterface.getObjectives(player);
                    for(IQuestObjective ob : objectives) {
                        player.sendMessage(ob.getMCText(), Util.NIL_UUID);
                    }
                }
            }
            return 1;
        }).then(Commands.argument("objective", IntegerArgumentType.integer(0, 3)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
            if(players.isEmpty())
                return 1;

            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger(context, "quest"));
            if (quest == null){
                throw new CommandException(new StringTextComponent("Unknown QuestID"));
            }

            int objective = IntegerArgumentType.getInteger(context, "objective");
            for(ServerPlayerEntity player : players){
                PlayerData data = PlayerData.get(player);
                if(data.questData.activeQuests.containsKey(quest.id)) {
                    IQuestObjective[] objectives = quest.questInterface.getObjectives(player);
                    if(objective < objectives.length){
                        player.sendMessage(objectives[objective].getMCText(), Util.NIL_UUID);
                    }
                }
            }

            return 1;
        }).then(Commands.argument("value", IntegerArgumentType.integer()).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
            if(players.isEmpty())
                return 1;

            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger(context, "quest"));
            if (quest == null){
                throw new CommandException(new StringTextComponent("Unknown QuestID"));
            }

            int objective = IntegerArgumentType.getInteger(context, "objective");
            int value = IntegerArgumentType.getInteger(context, "value");
            for(ServerPlayerEntity player : players){
                PlayerData data = PlayerData.get(player);
                if(data.questData.activeQuests.containsKey(quest.id)) {
                    IQuestObjective[] objectives = quest.questInterface.getObjectives(player);
                    if(objective < objectives.length){
                        objectives[objective].setProgress(value);
                    }
                }
            }

            return 1;
        }))))));

        command.requires((source) -> {
            return source.hasPermission(4);
        }).then(Commands.literal("reload").executes(context -> {
            new QuestController().load();
            SyncController.syncAllQuests();
            return 1;
        }));

        return command;
    }
}













