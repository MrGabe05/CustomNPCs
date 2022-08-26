package noppes.npcs.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.event.WorldEvent;
import noppes.npcs.controllers.ScriptController;


public class CmdScript{
	public static LiteralArgumentBuilder<CommandSource> register() {
		LiteralArgumentBuilder<CommandSource> command = Commands.literal("script").requires((source) -> source.hasPermission(CustomNpcs.NoppesCommandOpOnly ? 4 : 2))
			.then(Commands.literal("reload").executes(context -> {
				ScriptController.Instance.loadCategories();

				if(ScriptController.Instance.loadPlayerScripts())
					context.getSource().sendSuccess(new StringTextComponent("Reload player scripts succesfully"), false);
				else
					context.getSource().sendSuccess(new StringTextComponent("Failed reloading player scripts"), false);

				if(ScriptController.Instance.loadForgeScripts())
					context.getSource().sendSuccess(new StringTextComponent("Reload forge scripts succesfully"), false);
				else
					context.getSource().sendSuccess(new StringTextComponent("Failed reloading forge scripts"), false);

				if(ScriptController.Instance.loadStoredData())
					context.getSource().sendSuccess(new StringTextComponent("Reload stored data succesfully"), false);
				else
					context.getSource().sendSuccess(new StringTextComponent("Failed reloading stored data"), false);
				return 1;
			}))
			.then(Commands.literal("trigger").then(Commands.argument("id", IntegerArgumentType.integer(0)).executes(context -> {
				IWorld level = NpcAPI.Instance().getIWorld(context.getSource().getLevel());
				Vector3d bpos = context.getSource().getPosition();
				IPos pos = NpcAPI.Instance().getIPos(bpos.x, bpos.y, bpos.z);
				int id = IntegerArgumentType.getInteger(context, "id");
				IEntity e = NpcAPI.Instance().getIEntity(context.getSource().getEntity());
				EventHooks.onScriptTriggerEvent(id, level, pos, e, new String[0]);
				return 1;
			}).then(Commands.argument("args", StringArgumentType.greedyString()).executes(context -> {
				IWorld level = NpcAPI.Instance().getIWorld(context.getSource().getLevel());
				Vector3d bpos = context.getSource().getPosition();
				IPos pos = NpcAPI.Instance().getIPos(bpos.x, bpos.y, bpos.z);
				IEntity e = NpcAPI.Instance().getIEntity(context.getSource().getEntity());
				int id = IntegerArgumentType.getInteger(context, "id");
				EventHooks.onScriptTriggerEvent(id, level, pos, e, StringArgumentType.getString(context, "args").split(" "));
				return 1;
			}))));
		return command;
	}
}
