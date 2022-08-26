package noppes.npcs.command;

import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.entity.EntityNPCInterface;

public class CmdClone {

    public static LiteralArgumentBuilder<CommandSource> register() {
        LiteralArgumentBuilder<CommandSource> command = Commands.literal("clone");

        command.then(Commands.literal("list").requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("tab", IntegerArgumentType.integer(0)).executes(context -> {
            int tab = IntegerArgumentType.getInteger(context, "tab");

            context.getSource().sendSuccess(new StringTextComponent("--- Stored NPCs --- (server side)"), false);
            for (String name : ServerCloneController.Instance.getClones(tab)) {
                context.getSource().sendSuccess(new StringTextComponent(name), false);
            }
            context.getSource().sendSuccess(new StringTextComponent("------------------------------------"), false);
            return 1;
        })));

        command.then(Commands.literal("add").requires((source) -> {
            return source.hasPermission(4);
        }).then(Commands.argument("npc", StringArgumentType.string()).then(Commands.argument("tab", IntegerArgumentType.integer(0)).executes(context -> {
            addClone(context, "");
            return 1;
        }).then(Commands.argument("name", StringArgumentType.string()).executes(context -> {
            addClone(context, StringArgumentType.getString(context, "name"));
            return 1;
        })))));

        command.then(Commands.literal("remove").requires((source) -> {
            return source.hasPermission(4);
        }).then(Commands.argument("npc", StringArgumentType.string()).then(Commands.argument("tab", IntegerArgumentType.integer(0)).executes(context -> {

            String nametodel = StringArgumentType.getString(context, "npc");
            int tab = IntegerArgumentType.getInteger(context, "tab");
            boolean deleted = false;
            for(String name : ServerCloneController.Instance.getClones(tab)){
                if(nametodel.equalsIgnoreCase(name)){
                    ServerCloneController.Instance.removeClone(name, tab);
                    deleted = true;
                    break;
                }
            }
            if (!deleted) {
                throw new CommandException(new TranslationTextComponent("Npc '%s' wasn't found", nametodel));
            }
            return 1;
        }))));


        command.then(Commands.literal("spawn").requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("npc", StringArgumentType.string()).then(Commands.argument("tab", IntegerArgumentType.integer(0)).executes(context -> {
            spawnClone(context, new BlockPos(context.getSource().getPosition()), "");
            return 1;
        }).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(context -> {
            spawnClone(context, BlockPosArgument.getOrLoadBlockPos(context, "pos"), "");
            return 1;
        }).then(Commands.argument("display_name", StringArgumentType.string()).executes(context -> {
            spawnClone(context, BlockPosArgument.getOrLoadBlockPos(context, "pos"), StringArgumentType.getString(context, "display_name"));
            return 1;
        }))))));


        command.then(Commands.literal("grid").requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("npc", StringArgumentType.string())
                .then(Commands.argument("tab", IntegerArgumentType.integer(0))
                        .then(Commands.argument("length", IntegerArgumentType.integer())
                                .then(Commands.argument("width", IntegerArgumentType.integer()).executes(context -> {
            int length = IntegerArgumentType.getInteger(context, "length");
            int width = IntegerArgumentType.getInteger(context, "width");
            for(int x = 0; x < length; x++){
                for(int z = 0; z < width; z++) {
                    spawnClone(context, new BlockPos(context.getSource().getPosition()).offset(length, 0, width), "");
                }
            }
            return 1;
        }).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(context -> {
            int length = IntegerArgumentType.getInteger(context, "length");
            int width = IntegerArgumentType.getInteger(context, "width");
            for(int x = 0; x < length; x++){
                for(int z = 0; z < width; z++) {
                    spawnClone(context, BlockPosArgument.getOrLoadBlockPos(context, "pos").offset(length, 0, width), "");
                }
            }
            return 1;
        }).then(Commands.argument("display_name", StringArgumentType.string()).executes(context -> {
            int length = IntegerArgumentType.getInteger(context, "length");
            int width = IntegerArgumentType.getInteger(context, "width");
            for(int x = 0; x < length; x++){
                for(int z = 0; z < width; z++) {
                    spawnClone(context, BlockPosArgument.getOrLoadBlockPos(context, "pos").offset(length, 0, width), StringArgumentType.getString(context, "display_name"));
                }
            }
            return 1;
        }))))))));

        return command;
    }

    private static void addClone(CommandContext<CommandSource> context, String newName){
        String name = StringArgumentType.getString(context, "npc");
        if(newName.isEmpty())
            newName = name;
        int tab = IntegerArgumentType.getInteger(context, "tab");
        List<EntityNPCInterface> list = CmdNoppes.getNpcsByName(context.getSource().getLevel(), name);
        if(list.isEmpty())
            return;
        EntityNPCInterface npc = list.get(0);
        CompoundNBT compound = new CompoundNBT();
        if(!npc.saveAsPassenger(compound))
            return;
        ServerCloneController.Instance.addClone(compound, newName, tab);
    }

    private static void spawnClone(CommandContext<CommandSource> context, BlockPos pos, String newName) {
        String name = StringArgumentType.getString(context, "npc").replaceAll("%", " ");

        int tab = IntegerArgumentType.getInteger(context, "tab");

        CompoundNBT compound = ServerCloneController.Instance.getCloneData(context.getSource(), name, tab);
        if(compound == null){
            throw new CommandException(new StringTextComponent("Unknown npc"));
        }

        if (pos == BlockPos.ZERO){//incase it was called from the console and not pos was given
            throw new CommandException(new StringTextComponent("Location needed"));
        }
        World world = context.getSource().getLevel();
        Entity entity = EntityType.create(compound, world).get();
        entity.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        if(entity instanceof EntityNPCInterface){
            EntityNPCInterface npc = (EntityNPCInterface) entity;
            npc.ais.setStartPos(pos);
            if(!newName.isEmpty())
                npc.display.setName(newName.replaceAll("%", " ")); // like name, newname must use % in place of space to keep a logical way
        }
        world.addFreshEntity(entity);
    }
}
