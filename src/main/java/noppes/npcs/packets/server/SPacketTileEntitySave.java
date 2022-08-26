package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketTileEntitySave extends PacketServerBasic {

    private CompoundNBT data;

    public SPacketTileEntitySave(CompoundNBT data) {
        this.data = data;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.border_item ||
                item.getItem() == CustomBlocks.copy_item || item.getItem() == CustomBlocks.redstone_item ||
                item.getItem() == CustomBlocks.scripted_item || item.getItem() == CustomBlocks.waypoint_item;
    }

    public static void encode(SPacketTileEntitySave msg, PacketBuffer buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketTileEntitySave decode(PacketBuffer buf) {
        return new SPacketTileEntitySave(buf.readNbt());
    }

    @Override
    protected void handle() {
        saveTileEntity(player, data);
    }

    public static TileEntity saveTileEntity(ServerPlayerEntity player, CompoundNBT compound){
        int x = compound.getInt("x");
        int y = compound.getInt("y");
        int z = compound.getInt("z");
        TileEntity tile = player.level.getBlockEntity(new BlockPos(x, y, z));
        if(tile != null)
            tile.load(tile.getBlockState(), compound);
        return tile;
    }

}