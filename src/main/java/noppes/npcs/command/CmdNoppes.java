package noppes.npcs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.CustomEntities;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class CmdNoppes {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("noppes").requires((p_198816_0_) -> {
                            return p_198816_0_.hasPermission(2);
                        })
                .then(CmdClone.register())
                .then(CmdConfig.register())
                .then(CmdDialog.register())
                .then(CmdFaction.register())
                .then(CmdMark.register())
                .then(CmdNPC.register())
                .then(CmdQuest.register())
                .then(CmdScene.register())
                .then(CmdSchematics.register())
                .then(CmdScript.register())
                .then(CmdSlay.register())
        );


    }

    public static List<EntityNPCInterface> getNpcsByName(ServerWorld level, String name) {
        return (List<EntityNPCInterface>)(List<?>)level.getEntities(CustomEntities.entityCustomNpc,  (npc) -> ((EntityNPCInterface)npc).display.getName().equalsIgnoreCase(name));
    }

    public static <T extends Entity> List<T> getEntities(EntityType<T> type, ServerWorld level) {
        return (List<T>)level.getEntities(type, (entity) -> true);
    }
}
