package noppes.npcs.client.gui.script;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptGet;
import noppes.npcs.packets.server.SPacketScriptSave;

public class GuiScriptDoor extends GuiScriptInterface {
	private TileScriptedDoor script;
		
	public GuiScriptDoor(BlockPos pos) {
		handler = script = (TileScriptedDoor) player.level.getBlockEntity(pos);
		Packets.sendServer(new SPacketScriptGet(5));
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		script.setNBT(compound);
		super.setGuiData(compound);
	}

	@Override
	public void save() {
		super.save();
		BlockPos pos = script.getBlockPos();
        Packets.sendServer(new SPacketScriptSave(5, script.getNBT(new CompoundNBT())));
	}
}
