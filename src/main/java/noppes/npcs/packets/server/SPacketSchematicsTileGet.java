package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketGuiScrollList;

import java.util.Vector;

public class SPacketSchematicsTileGet extends PacketServerBasic {

    private final BlockPos pos;

    public SPacketSchematicsTileGet(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
    }

    public static void encode(SPacketSchematicsTileGet msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static SPacketSchematicsTileGet decode(PacketBuffer buf) {
        return new SPacketSchematicsTileGet(buf.readBlockPos());
    }

    @Override
    protected void handle() {
        TileBuilder tile = (TileBuilder) player.level.getBlockEntity(pos);
        if(tile == null)
            return;
        Packets.send(player, new PacketGuiData(tile.writePartNBT(new CompoundNBT())));
        Packets.send(player, new PacketGuiScrollList(new Vector(SchematicController.Instance.list())));

        if(tile.hasSchematic())
            Packets.send(player, new PacketGuiData(tile.getSchematic().getNBTSmall()));
    }

}