package noppes.npcs.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;

import java.util.List;

public class CmdNPC{


	public static final SuggestionProvider<CommandSource> VISIBLE = SuggestionProviders.register(new ResourceLocation("visible"), (context, builder) -> {
		return ISuggestionProvider.suggest(new String[]{"true", "false", "semi"}, builder);
	});

	public static LiteralArgumentBuilder<CommandSource> register() {
		LiteralArgumentBuilder<CommandSource> command = Commands.literal("npc").requires((source) -> source.hasPermission(CustomNpcs.NoppesCommandOpOnly ? 4 : 2))
			.then(Commands.argument("npc", StringArgumentType.string())
				.then(Commands.literal("home").then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(context -> {
					String name = StringArgumentType.getString(context, "npc");
					List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(context.getSource().getLevel(), name);
					if(!npcs.isEmpty()){
						npcs.get(0).ais.setStartPos(BlockPosArgument.getOrLoadBlockPos(context, "pos"));
					}
					return 1;
				})))
				.then(Commands.literal("visible").then(Commands.argument("visibility", StringArgumentType.word()).suggests(VISIBLE).executes(context -> {
					String name = StringArgumentType.getString(context, "npc");
					List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(context.getSource().getLevel(), name);
					String val = StringArgumentType.getString(context, "visibility");
					int vis = 0;
					if(val.equalsIgnoreCase("false")){
						vis = 1;
					}
					else if(val.equalsIgnoreCase("semi")){
						vis = 2;
					}

					for(EntityNPCInterface npc : npcs){
						npc.display.setVisible(vis);
					}
					return 1;
				})))
				.then(Commands.literal("delete").executes(context -> {
					String name = StringArgumentType.getString(context, "npc");
					List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(context.getSource().getLevel(), name);

					for(EntityNPCInterface npc : npcs){
						npc.delete();
					}
					return 1;
				}))
				.then(Commands.literal("owner")
					.executes(context -> {
						String name = StringArgumentType.getString(context, "npc");
						List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(context.getSource().getLevel(), name);

						for(EntityNPCInterface npc : npcs){
							LivingEntity owner = npc.getOwner();
							if(owner == null){
								context.getSource().sendSuccess(new StringTextComponent("No owner"), false);
							}
							else{
								context.getSource().sendSuccess(new StringTextComponent("Owner is: " + owner.getName()), false);
							}
						}
						return 1;
					}).then(Commands.argument("player", EntityArgument.player()).executes(context -> {
						String name = StringArgumentType.getString(context, "npc");
						List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(context.getSource().getLevel(), name);

						PlayerEntity player = EntityArgument.getPlayer(context, "player");
						for(EntityNPCInterface npc : npcs) {
							if(npc.role instanceof RoleFollower)
								((RoleFollower)npc.role).setOwner(player);

							if(npc.role instanceof RoleCompanion)
								((RoleCompanion)npc.role).setOwner(player);
						}
						return 1;
					}))
				)
				.then(Commands.literal("delete").then(Commands.argument("name", StringArgumentType.greedyString()).executes(context -> {
					List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(context.getSource().getLevel(), StringArgumentType.getString(context, "npc"));
					String name = StringArgumentType.getString(context, "name");
					for(EntityNPCInterface npc : npcs){
						npc.display.setName(name);
						npc.updateClient = true;
					}
					return 1;
				})))
				.then(Commands.literal("reset").executes(context -> {
					String name = StringArgumentType.getString(context, "npc");
					List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(context.getSource().getLevel(), name);

					for(EntityNPCInterface npc : npcs){
						npc.reset();
					}
					return 1;
				}))
				.then(Commands.literal("create").executes(context -> {
					String name = StringArgumentType.getString(context, "npc");
					World pw = context.getSource().getLevel();
					EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, pw);
					npc.display.setName(name);
					Vector3d pos = context.getSource().getPosition();
					npc.absMoveTo(pos.x, pos.y, pos.z, 0, 0);
					npc.ais.setStartPos(new BlockPos(pos));
					pw.addFreshEntity(npc);
					npc.setHealth(npc.getMaxHealth());
					return 1;
				}))
		);

		return command;
	}
}
