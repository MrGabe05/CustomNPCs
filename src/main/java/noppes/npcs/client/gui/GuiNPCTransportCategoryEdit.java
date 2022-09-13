package noppes.npcs.client.gui;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTransportCategoriesGet;
import noppes.npcs.packets.server.SPacketTransportCategorySave;

public class GuiNPCTransportCategoryEdit extends GuiNPCInterface
{
	private final Screen parent;
	private final String name;
	private final int id;
    public GuiNPCTransportCategoryEdit(EntityNPCInterface npc,Screen parent, String name, int id)
    {
    	super(npc);
    	this.parent = parent;
    	this.name = name;
    	this.id = id;
    	title = "Npc Transport Category";
    }

    @Override
    public void init() {
        super.init();
    	this.addTextField(new GuiTextFieldNop(1, this, width / 2 - 40, 100, 140, 20, name));
        addLabel(new GuiLabel(1,"Title:", width / 2 - 100+4, 105, 0xffffff));
        
    	this.addButton(new GuiButtonNop(this, 2, width / 2 - 100, 210,98, 20, "gui.back"));
    	this.addButton(new GuiButtonNop(this, 3, width / 2 +2, 210,98, 20, "Save"));
    }

	@Override
	public void buttonEvent(GuiButtonNop guibutton)
    {
		int id = guibutton.id;
        if(id == 2)
        {
        	NoppesUtil.openGUI(player, parent);
    		Packets.sendServer(new SPacketTransportCategoriesGet());
        }
        if(id == 3)
        {
        	save();
        	NoppesUtil.openGUI(player, parent);
        	Packets.sendServer(new SPacketTransportCategoriesGet());
        }
    }

	@Override
	public void save() {
		String name = getTextField(1).getValue();
		if(name.trim().isEmpty())
			return;
		Packets.sendServer(new SPacketTransportCategorySave(id, name));
	}
//	@Override
//    public void drawDefaultBackground()
//    {
//		drawBackground(0);
//    }

}
