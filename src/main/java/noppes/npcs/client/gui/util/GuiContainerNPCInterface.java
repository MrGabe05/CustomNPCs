package noppes.npcs.client.gui.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiBasicContainer;

public abstract class GuiContainerNPCInterface<T extends Container> extends GuiBasicContainer<T> {
	public EntityNPCInterface npc;

	public GuiContainerNPCInterface(EntityNPCInterface npc, T cont, PlayerInventory inv, ITextComponent titleIn) {
		super(cont, inv, titleIn);
		this.npc = npc;
	}

	public void drawNpc(int x, int y) {
		this.wrapper.drawNpc(npc, x, y, 1, 0, guiLeft, guiTop);
	}
}
