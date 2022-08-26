package noppes.npcs.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.schematics.SchematicWrapper;

import java.util.ArrayList;
import java.util.List;

public class CmdSchematics {
	public static final List<String> names = new ArrayList<>();

	public static final SuggestionProvider<CommandSource> SCHEMAS = SuggestionProviders.register(new ResourceLocation("schemas"), (context, builder) -> {
		return ISuggestionProvider.suggest(names.stream(), builder);
	});

	public static final SuggestionProvider<CommandSource> ROTATION = SuggestionProviders.register(new ResourceLocation("rotation"), (context, builder) -> {
		return ISuggestionProvider.suggest(new String[]{"0", "90", "180", "270"}, builder);
	});

	
	public static LiteralArgumentBuilder<CommandSource> register() {
		LiteralArgumentBuilder<CommandSource> command = Commands.literal("schema").requires((source) -> source.hasPermission(4))
			.then(Commands.literal("build").then(Commands.argument("name", StringArgumentType.word()).suggests(SCHEMAS)
				.then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands.argument("rotation", StringArgumentType.word()).suggests(ROTATION).executes(context -> {

					String name = StringArgumentType.getString(context, "name");
					BlockPos pos = BlockPosArgument.getOrLoadBlockPos(context, "pos");
					int rotation = Integer.parseInt(StringArgumentType.getString(context, "rotation"));

					SchematicWrapper schem = SchematicController.Instance.load(name);
					schem.init(pos, context.getSource().getLevel(), rotation);
					SchematicController.Instance.build(schem, context.getSource());
					return 1;
				}))))
		).then(Commands.literal("stop").executes(context -> {
			SchematicController.Instance.stop(context.getSource());
			return 1;
		})).then(Commands.literal("info").executes(context -> {
			SchematicController.Instance.info(context.getSource());
			return 1;
		})).then(Commands.literal("list").executes(context -> {
			List<String> list = SchematicController.Instance.list();
			if(list.isEmpty()){
				context.getSource().sendSuccess(new TranslationTextComponent("No schemas available"), false);
				return 1;
			}
			String s = "";
			for(String file : list){
				s += file + ", ";
			}
			context.getSource().sendSuccess(new TranslationTextComponent(s), false);
			return 1;
		}));

		return command;
	}
}
