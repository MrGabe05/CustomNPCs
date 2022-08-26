package noppes.npcs.command;

import java.util.Map.Entry;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.entity.data.DataScenes.SceneState;

public class CmdScene {


	public static LiteralArgumentBuilder<CommandSource> register() {
		LiteralArgumentBuilder<CommandSource> command = Commands.literal("scene").requires((source) -> {
			return source.hasPermission(2);
		}).then(Commands.literal("time")
				.executes(context -> {
					context.getSource().sendSuccess(new StringTextComponent("Active scenes:"), false);
					for(Entry<String, SceneState> entry : DataScenes.StartedScenes.entrySet())
						context.getSource().sendSuccess(new TranslationTextComponent("Scene %s time is %s", entry.getKey(), entry.getValue().ticks), false);
					return 1;
				}).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes(context -> {
					int ticks = IntegerArgumentType.getInteger(context, "time");
					for(SceneState state : DataScenes.StartedScenes.values())
						state.ticks = ticks;
					return 1;
				}).then(Commands.argument("name", StringArgumentType.string()).executes(context -> {
					String name = StringArgumentType.getString(context, "name");
					SceneState state = DataScenes.StartedScenes.get(name.toLowerCase());
					if(state == null)
						throw new CommandException(new TranslationTextComponent("Unknown scene name %s", name));
					state.ticks = IntegerArgumentType.getInteger(context, "time");
					context.getSource().sendSuccess(new TranslationTextComponent("Scene %s set to %s", name, state.ticks), false);
					return 1;
				})))
		).then(Commands.literal("reset")
				.executes(context -> {
					DataScenes.Reset(context.getSource(), null);
					return 1;
				}).then(Commands.argument("name", StringArgumentType.string()).executes(context -> {
					DataScenes.Reset(context.getSource(), StringArgumentType.getString(context, "name"));
					return 1;
				}))
		).then(Commands.literal("start").then(Commands.argument("name", StringArgumentType.string()).executes(context -> {
			DataScenes.Start(context.getSource(), StringArgumentType.getString(context, "name"));
			return 1;
		})))
		.then(Commands.literal("pause")
				.executes(context -> {
					DataScenes.Pause(context.getSource(), null);
					return 1;
				}).then(Commands.argument("name", StringArgumentType.string()).executes(context -> {
					DataScenes.Pause(context.getSource(), StringArgumentType.getString(context, "name"));
					return 1;
				}))
		)
				;
		return command;
	}
}
