package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketTileEntityGet extends PacketServerBasic {

    private final BlockPos pos;

    public SPacketTileEntityGet(BlockPos pos) {
        this.pos = pos;
    }
    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketTileEntityGet msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static SPacketTileEntityGet decode(PacketBuffer buf) {
        return new SPacketTileEntityGet(buf.readBlockPos());
    }

    @Override
    protected void handle() {
        TileEntity tile = player.level.getBlockEntity(pos);
        CompoundNBT compound = new CompoundNBT();
        tile.save(compound);
        Packets.send(player, new PacketGuiData(compound));
    }

}