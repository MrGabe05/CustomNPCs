package noppes.npcs.client.gui.script;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptGet;
import noppes.npcs.packets.server.SPacketScriptSave;

public class GuiScriptPlayers extends GuiScriptInterface{	
	private PlayerScriptData script = new PlayerScriptData(null);;
		
	public GuiScriptPlayers() {
		handler = script;
		Packets.sendServer(new SPacketScriptGet(4));
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		script.load(compound);
		super.setGuiData(compound);
	}

	@Override
	public void save() {
		super.save();
        Packets.sendServer(new SPacketScriptSave(4, script.save(new CompoundNBT())));
	}
}
