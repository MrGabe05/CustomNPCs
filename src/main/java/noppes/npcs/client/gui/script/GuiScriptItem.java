package noppes.npcs.client.gui.script;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.CustomItems;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptGet;
import noppes.npcs.packets.server.SPacketScriptSave;

public class GuiScriptItem extends GuiScriptInterface {
	
	private ItemScriptedWrapper item;
		
	public GuiScriptItem(PlayerEntity player) {
		handler = item = new ItemScriptedWrapper(new ItemStack(CustomItems.scripted_item));
		Packets.sendServer(new SPacketScriptGet(2));
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		item.setMCNbt(compound);
		super.setGuiData(compound);
	}

	@Override
	public void save() {
		super.save();
        Packets.sendServer(new SPacketScriptSave(2, item.getMCNbt()));
	}
}
