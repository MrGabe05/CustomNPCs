package noppes.npcs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityDialogNpc;

import java.util.Collection;

public class CmdDialog {

    public static LiteralArgumentBuilder<CommandSource> register() {
        LiteralArgumentBuilder<CommandSource> command = Commands.literal("dialog");

        command.then(Commands.literal("reload").requires((source) -> {
            return source.hasPermission(4);
        }).executes(context -> {
            new DialogController().load();
            SyncController.syncAllDialogs();
            return 1;
        }));

        command.then(Commands.literal("read").requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("players", EntityArgument.players()).then(Commands.argument("dialog", IntegerArgumentType.integer(0))
                .executes(context -> {
                    Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
                    if(players.isEmpty())
                        return 1;

                    Dialog dialog = DialogController.instance.dialogs.get(IntegerArgumentType.getInteger(context, "dialog"));
                    if (dialog == null){
                        throw new CommandException(new StringTextComponent("Unknown DialogID"));
                    }
                    for(ServerPlayerEntity player : players){
                        PlayerData data = PlayerData.get(player);
                        if(!data.dialogData.dialogsRead.contains(dialog.id)){
                            data.dialogData.dialogsRead.add(dialog.id);
                            data.save(true);
                        }
                    }
                    return 1;
                }))));

        command.then(Commands.literal("unread").requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("players", EntityArgument.players()).then(Commands.argument("dialog", IntegerArgumentType.integer(0))
                .executes(context -> {
                    Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
                    if(players.isEmpty())
                        return 1;

                    Dialog dialog = DialogController.instance.dialogs.get(IntegerArgumentType.getInteger(context, "dialog"));
                    if (dialog == null){
                        throw new CommandException(new StringTextComponent("Unknown DialogID"));
                    }
                    for(ServerPlayerEntity player : players){
                        PlayerData data = PlayerData.get(player);
                        if(data.dialogData.dialogsRead.contains(dialog.id)){
                            data.dialogData.dialogsRead.remove(dialog.id);
                            data.save(true);
                        }
                    }
                    return 1;
                }))));

        command.then(Commands.literal("show").requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("players", EntityArgument.players()).then(Commands.argument("dialog", IntegerArgumentType.integer(0))
                .then(Commands.argument("name", StringArgumentType.string()).executes(context -> {
                    Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players");
                    if(players.isEmpty())
                        return 1;

                    Dialog dialog = DialogController.instance.dialogs.get(IntegerArgumentType.getInteger(context, "dialog"));
                    if (dialog == null){
                        throw new CommandException(new StringTextComponent("Unknown DialogID"));
                    }

                    EntityDialogNpc npc = new EntityDialogNpc(context.getSource().getLevel());
                    DialogOption option = new DialogOption();
                    option.dialogId = dialog.id;
                    option.title = dialog.title;
                    npc.dialogs.put(0, option);
                    npc.display.setName(StringArgumentType.getString(context, "name"));

                    for(ServerPlayerEntity player : players){
                        EntityUtil.Copy(player, npc);
                        NoppesUtilServer.openDialog(player, npc, dialog);
                    }
                    return 1;
                })))));

        return command;
    }
}
