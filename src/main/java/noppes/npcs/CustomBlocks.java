package noppes.npcs;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.ObjectHolder;
import noppes.npcs.blocks.*;
import noppes.npcs.blocks.tiles.*;
import noppes.npcs.containers.ContainerCarpentryBench;
import noppes.npcs.items.ItemNpcBlock;
import noppes.npcs.items.ItemScriptedDoor;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid=CustomNpcs.MODID)
@ObjectHolder(CustomNpcs.MODID)
public class CustomBlocks {

    @ObjectHolder("npcredstoneblock")
    public static Block redstone;

    @ObjectHolder("npcredstoneblock")
    public static Item redstone_item;

    @ObjectHolder("npcmailbox")
    public static Block mailbox;

    @ObjectHolder("npcmailbox2")
    public static Block mailbox2;

    @ObjectHolder("npcmailbox3")
    public static Block mailbox3;

    @ObjectHolder("npcwaypoint")
    public static Block waypoint;

    @ObjectHolder("npcwaypoint")
    public static Item waypoint_item;

    @ObjectHolder("npcborder")
    public static Block border;

    @ObjectHolder("npcborder")
    public static Item border_item;

    @ObjectHolder("npcscripted")
    public static Block scripted;

    @ObjectHolder("npcscripted")
    public static Item scripted_item;

    @ObjectHolder("npcscripteddoor")
    public static Block scripted_door;

    @ObjectHolder("npcscripteddoortool")
    public static Item scripted_door_item;

    @ObjectHolder("npcbuilderblock")
    public static Block builder;

    @ObjectHolder("npcbuilderblock")
    public static Item builder_item;

    @ObjectHolder("npccopyblock")
    public static Block copy;

    @ObjectHolder("npccopyblock")
    public static Item copy_item;

    @ObjectHolder("npccarpentybench")
    public static Block carpenty;

    @ObjectHolder("tileblockanvil")
    public static TileEntityType<TileBlockAnvil> tile_anvil;
    @ObjectHolder("tilenpcborder")
    public static TileEntityType tile_border;
    @ObjectHolder("tilenpcbuilder")
    public static TileEntityType tile_builder;
    @ObjectHolder("tilenpccopy")
    public static TileEntityType tile_copy;
    @ObjectHolder("tilemailbox")
    public static TileEntityType<TileMailbox> tile_mailbox;
    @ObjectHolder("tileredstoneblock")
    public static TileEntityType tile_redstoneblock;
    @ObjectHolder("tilenpcscripted")
    public static TileEntityType tile_scripted;
    @ObjectHolder("tilenpcscripteddoor")
    public static TileEntityType tile_scripteddoor;
    @ObjectHolder("tilewaypoint")
    public static TileEntityType tile_waypoint;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new BlockNpcRedstone().setRegistryName(CustomNpcs.MODID, "npcredstoneblock"),
                new BlockMailbox(0).setRegistryName(CustomNpcs.MODID, "npcmailbox"),
                new BlockMailbox(1).setRegistryName(CustomNpcs.MODID, "npcmailbox2"),
                new BlockMailbox(2).setRegistryName(CustomNpcs.MODID, "npcmailbox3"),
                new BlockWaypoint().setRegistryName(CustomNpcs.MODID, "npcwaypoint"),
                new BlockBorder().setRegistryName(CustomNpcs.MODID, "npcborder"),
                new BlockScripted().setRegistryName(CustomNpcs.MODID, "npcscripted"),
                new BlockScriptedDoor().setRegistryName(CustomNpcs.MODID, "npcscripteddoor"),
                new BlockBuilder().setRegistryName(CustomNpcs.MODID, "npcbuilderblock"),
                new BlockCopy().setRegistryName(CustomNpcs.MODID, "npccopyblock"),
                new BlockCarpentryBench().setRegistryName(CustomNpcs.MODID, "npccarpentybench"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                createItem(redstone),
                createItem(mailbox),
                createItem(mailbox2),
                createItem(mailbox3),
                createItem(waypoint),
                createItem(border),
                createItem(scripted),
                new ItemScriptedDoor(scripted_door).setRegistryName(CustomNpcs.MODID, "npcscripteddoortool"),
                createItem(builder),
                createItem(copy),
                createItem(carpenty)
        );
    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                createTile("tileblockanvil", TileBlockAnvil::new, CustomBlocks.carpenty),
                createTile("tilenpcborder", TileBorder::new, CustomBlocks.border),
                createTile("tilenpcbuilder", TileBuilder::new, CustomBlocks.builder),
                createTile("tilenpccopy", TileCopy::new, CustomBlocks.copy),
                createTile("tilemailbox", TileMailbox::new, CustomBlocks.mailbox, CustomBlocks.mailbox2, CustomBlocks.mailbox3),
                createTile("tileredstoneblock", TileRedstoneBlock::new, CustomBlocks.redstone),
                createTile("tilenpcscripted", TileScripted::new, CustomBlocks.scripted),
                createTile("tilenpcscripteddoor", TileScriptedDoor::new, CustomBlocks.scripted_door),
                createTile("tilewaypoint", TileWaypoint::new, CustomBlocks.waypoint)
        );
    }

    private static TileEntityType<?> createTile(String key, Supplier<? extends TileEntity> factoryIn, Block... blocks){
        TileEntityType.Builder<TileEntity> builder = TileEntityType.Builder.of(factoryIn, blocks);
        return builder.build(Util.fetchChoiceType(TypeReferences.BLOCK_ENTITY, key)).setRegistryName(key);
    }

    public static Item createItem(Block block){
        Item item = new ItemNpcBlock(block, CustomNpcs.proxy.getItemProperties());
        item.setRegistryName(block.getRegistryName());
        return item;
    }
}