package noppes.npcs.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import noppes.npcs.controllers.data.MarkData;

import java.util.Collection;

public class CmdMark {

	public static LiteralArgumentBuilder<CommandSource> register() {
		LiteralArgumentBuilder<CommandSource> command = Commands.literal("mark").requires((source) -> {
			return source.hasPermission(2);
		});

		command.then(Commands.argument("clear", EntityArgument.entities())
				.executes(context -> {
					Collection<? extends Entity> entities = EntityArgument.getEntities(context, "entities");

					for(Entity entity : entities){
						if(!(entity instanceof LivingEntity))
							continue;
						MarkData data = MarkData.get((LivingEntity) entity);
						data.marks.clear();
						data.syncClients();
					}
					return 1;
				}));

		command.then(Commands.argument("entities", EntityArgument.entities()).then(Commands.argument("type", IntegerArgumentType.integer(0))
			.executes(context -> {
				Collection<? extends Entity> entities = EntityArgument.getEntities(context, "entities");
				if(entities.isEmpty())
					return 1;
				int type = IntegerArgumentType.getInteger(context, "type");

				for(Entity entity : entities){
					if(!(entity instanceof LivingEntity))
						continue;
					MarkData data = MarkData.get((LivingEntity) entity);
					data.marks.clear();
					data.addMark(type, 0xFFFFFF);
				}
				return 1;
			}).then(Commands.argument("color", StringArgumentType.word())).executes(context -> {
					Collection<? extends Entity> entities = EntityArgument.getEntities(context, "entities");
					if(entities.isEmpty())
						return 1;
					int type = IntegerArgumentType.getInteger(context, "type");

					int color = 0xFFFFFF;
					try{
						color = Integer.parseInt(StringArgumentType.getString(context, "color"), 16);
					}
					catch(Exception e){}
					for(Entity entity : entities){
						if(!(entity instanceof LivingEntity))
							continue;
						MarkData data = MarkData.get((LivingEntity) entity);
						data.marks.clear();
						data.addMark(type, color);
					}
					return 1;
				})));

		return command;
	}
}
