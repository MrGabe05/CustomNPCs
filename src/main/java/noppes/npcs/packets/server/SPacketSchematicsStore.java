package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketSchematicsStore extends PacketServerBasic {

    private String name;
    private CompoundNBT data;

    public SPacketSchematicsStore(String name, CompoundNBT data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.copy_item;
    }

    public static void encode(SPacketSchematicsStore msg, PacketBuffer buf) {
        buf.writeUtf(msg.name);
        buf.writeNbt(msg.data);
    }

    public static SPacketSchematicsStore decode(PacketBuffer buf) {
        return new SPacketSchematicsStore(buf.readUtf(32767), buf.readNbt());
    }

    @Override
    protected void handle() {
        TileCopy tile = (TileCopy) SPacketTileEntitySave.saveTileEntity(player, data);
        if(tile == null || name.isEmpty())
            return;
        SchematicController.Instance.save(player.createCommandSourceStack(), name, tile.getBlockPos(), tile.height, tile.width, tile.length);
    }

}