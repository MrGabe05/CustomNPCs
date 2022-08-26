package noppes.npcs.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.tiles.*;
import noppes.npcs.client.renderer.blocks.*;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = CustomNpcs.MODID, value = Dist.CLIENT)
public class CustomItemModels {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {

        ClientRegistry.bindTileEntityRenderer(CustomBlocks.tile_anvil, (dispatcher) -> new BlockCarpentryBenchRenderer(dispatcher) );
        ClientRegistry.bindTileEntityRenderer(CustomBlocks.tile_mailbox, (dispatcher) -> new BlockMailboxRenderer(dispatcher));
        ClientRegistry.bindTileEntityRenderer(CustomBlocks.tile_scripted, (dispatcher) -> new BlockScriptedRenderer(dispatcher));
        ClientRegistry.bindTileEntityRenderer(CustomBlocks.tile_scripteddoor, (dispatcher) -> new BlockDoorRenderer(dispatcher));
        ClientRegistry.bindTileEntityRenderer(CustomBlocks.tile_copy, (dispatcher) -> new BlockCopyRenderer(dispatcher));

    }
}
