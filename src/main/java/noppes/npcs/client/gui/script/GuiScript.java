package noppes.npcs.client.gui.script;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataScript;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptGet;
import noppes.npcs.packets.server.SPacketScriptSave;

public class GuiScript extends GuiScriptInterface{	
	private final DataScript script;
		
	public GuiScript(EntityNPCInterface npc) {
		handler = script = npc.script;
        Packets.sendServer(new SPacketScriptGet(0));
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		script.load(compound);
		super.setGuiData(compound);
	}

	@Override
	public void save() {
		super.save();
        Packets.sendServer(new SPacketScriptSave(0, script.save(new CompoundNBT())));
	}
}
