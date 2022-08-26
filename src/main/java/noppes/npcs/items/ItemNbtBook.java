package noppes.npcs.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import noppes.npcs.CustomTabs;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketGuiOpen;

public class ItemNbtBook extends Item{
	
	public ItemNbtBook() {
		super(new Item.Properties().stacksTo(1).tab(CustomTabs.tab));
	}

	public void blockEvent(PlayerInteractEvent.RightClickBlock event) {
		Packets.send((ServerPlayerEntity)event.getPlayer(), new PacketGuiOpen(EnumGuiType.NbtBook, event.getPos()));
		
		BlockState state = event.getWorld().getBlockState(event.getPos());
		CompoundNBT data = new CompoundNBT();
		TileEntity tile = event.getWorld().getBlockEntity(event.getPos());
		if(tile != null) {
			tile.save(data);
		}
		
		CompoundNBT compound = new CompoundNBT();
		compound.put("Data", data);
		Packets.send((ServerPlayerEntity)event.getPlayer(), new PacketGuiData(compound));
	}

	public void entityEvent(PlayerInteractEvent.EntityInteract event) {
		Packets.send((ServerPlayerEntity)event.getPlayer(), new PacketGuiOpen(EnumGuiType.NbtBook, BlockPos.ZERO));
		
		CompoundNBT data = new CompoundNBT();
		event.getTarget().saveAsPassenger(data);
		CompoundNBT compound = new CompoundNBT();
		compound.putInt("EntityId", event.getTarget().getId());
		compound.put("Data", data);
		Packets.send((ServerPlayerEntity)event.getPlayer(), new PacketGuiData(compound));
	}
}
