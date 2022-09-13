package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketSchematicsTileSet extends PacketServerBasic {

    private final BlockPos pos;
    private final String name;

    public SPacketSchematicsTileSet(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
    }

    public static void encode(SPacketSchematicsTileSet msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeUtf(msg.name);
    }

    public static SPacketSchematicsTileSet decode(PacketBuffer buf) {
        return new SPacketSchematicsTileSet(buf.readBlockPos(), buf.readUtf(32767));
    }

    @Override
    protected void handle() {
        TileBuilder tile = (TileBuilder) player.level.getBlockEntity(pos);
        tile.setSchematic(SchematicController.Instance.load(name));

        if(tile.hasSchematic()){
            Packets.send(player, new PacketGuiData(tile.getSchematic().getNBTSmall()));
        }
    }

}