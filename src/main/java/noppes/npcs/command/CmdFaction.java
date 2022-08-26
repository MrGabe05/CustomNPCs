package noppes.npcs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerFactionData;

import java.util.Collection;

public class CmdFaction {

    public static LiteralArgumentBuilder<CommandSource> register() {
        LiteralArgumentBuilder<CommandSource> command = Commands.literal("faction").requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("players", EntityArgument.players()).then(Commands.argument("faction", IntegerArgumentType.integer(0))
                .then(Commands.literal("add").then(Commands.argument("points", IntegerArgumentType.integer()).executes(context -> {
                    Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
                    if(players.isEmpty())
                        return 1;

                    Faction faction = FactionController.instance.factions.get(IntegerArgumentType.getInteger(context, "faction"));
                    if (faction == null){
                        throw new CommandException(new StringTextComponent("Unknown FactionID"));
                    }
                    int points = IntegerArgumentType.getInteger(context, "points");

                    for(ServerPlayerEntity player : players){
                        PlayerData data = PlayerData.get(player);
                        PlayerFactionData playerfactiondata = data.factionData;
                        playerfactiondata.increasePoints(player, faction.id, points);
                        data.save(true);
                    }
                    return 1;
                })))

                .then(Commands.literal("set").then(Commands.argument("points", IntegerArgumentType.integer()).executes(context -> {
                    Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
                    if(players.isEmpty())
                        return 1;

                    Faction faction = FactionController.instance.factions.get(IntegerArgumentType.getInteger(context, "faction"));
                    if (faction == null){
                        throw new CommandException(new StringTextComponent("Unknown FactionID"));
                    }
                    int points = IntegerArgumentType.getInteger(context, "points");

                    for(ServerPlayerEntity player : players){
                        PlayerData data = PlayerData.get(player);
                        PlayerFactionData playerfactiondata = data.factionData;
                        playerfactiondata.factionData.put(faction.id, points);
                        data.save(true);
                    }
                    return 1;
                })))

                .then(Commands.literal("reset").executes(context -> {
                    Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
                    if(players.isEmpty())
                        return 1;

                    Faction faction = FactionController.instance.factions.get(IntegerArgumentType.getInteger(context, "faction"));
                    if (faction == null){
                        throw new CommandException(new StringTextComponent("Unknown FactionID"));
                    }

                    for(ServerPlayerEntity player : players){
                        PlayerData data = PlayerData.get(player);
                        data.factionData.factionData.put(faction.id, faction.defaultPoints);
                        data.save(true);
                    }
                    return 1;
                }))

                .then(Commands.literal("drop").executes(context -> {
                    Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
                    if(players.isEmpty())
                        return 1;

                    Faction faction = FactionController.instance.factions.get(IntegerArgumentType.getInteger(context, "faction"));
                    if (faction == null){
                        throw new CommandException(new StringTextComponent("Unknown FactionID"));
                    }

                    for(ServerPlayerEntity player : players){
                        PlayerData data = PlayerData.get(player);
                        data.factionData.factionData.remove(faction.id);
                        data.save(true);
                    }
                    return 1;
                }))
        ));

        return command;
    }
}
