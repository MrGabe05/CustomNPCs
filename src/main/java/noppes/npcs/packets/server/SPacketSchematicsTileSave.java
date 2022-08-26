package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketSchematicsTileSave extends PacketServerBasic {

    private BlockPos pos;
    private CompoundNBT data;

    public SPacketSchematicsTileSave(BlockPos pos, CompoundNBT data) {
        this.pos = pos;
        this.data = data;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
    }

    public static void encode(SPacketSchematicsTileSave msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeNbt(msg.data);
    }

    public static SPacketSchematicsTileSave decode(PacketBuffer buf) {
        return new SPacketSchematicsTileSave(buf.readBlockPos(), buf.readNbt());
    }

    @Override
    protected void handle() {
        TileBuilder tile = (TileBuilder) player.level.getBlockEntity(pos);
        if(tile != null) {
            tile.readPartNBT(data);
        }
    }

}