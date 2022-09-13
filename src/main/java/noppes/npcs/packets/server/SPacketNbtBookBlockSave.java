package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNbtBookBlockSave extends PacketServerBasic {
    private final BlockPos pos;
    private final CompoundNBT data;

    public SPacketNbtBookBlockSave(BlockPos pos, CompoundNBT data) {
        this.pos = pos;
        this.data = data;
    }

    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.nbt_book;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.TOOL_NBTBOOK;
    }

    public static void encode(SPacketNbtBookBlockSave msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeNbt(msg.data);
    }

    public static SPacketNbtBookBlockSave decode(PacketBuffer buf) {
        return new SPacketNbtBookBlockSave(buf.readBlockPos(), buf.readNbt());
    }

    @Override
    protected void handle() {
        TileEntity tile = player.level.getBlockEntity(pos);
        if(tile != null) {
            tile.load(tile.getBlockState(), data);
            tile.setChanged();
        }
    }


}