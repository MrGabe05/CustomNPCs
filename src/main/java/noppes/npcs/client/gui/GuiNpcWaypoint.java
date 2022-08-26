package noppes.npcs.client.gui;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.blocks.tiles.TileWaypoint;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNpcWaypoint extends GuiNPCInterface implements IGuiData {

	private TileWaypoint tile;
	
    public GuiNpcWaypoint(BlockPos pos) {
		super();
		tile = (TileWaypoint) player.level.getBlockEntity(pos);
		Packets.sendServer(new SPacketTileEntityGet(pos));
    	imageWidth = 265;
	}
    
    @Override
	public void init(){
    	super.init();
    	if(tile == null) {
    		this.close();
    	}
    	
    	addLabel(new GuiLabel(0,"gui.name", guiLeft+ 1, guiTop + 76, 0xffffff));
    	addTextField(new GuiTextFieldNop(0, this,  guiLeft+ 60, guiTop + 71,200,20, tile.name));
    	
    	addLabel(new GuiLabel(1,"gui.range", guiLeft+ 1, guiTop + 97, 0xffffff));    	
    	addTextField(new GuiTextFieldNop(1, this,  guiLeft+ 60, guiTop + 92,200,20, tile.range + ""));
    	getTextField(1).numbersOnly = true;
    	getTextField(1).setMinMaxDefault(2, 60, 10);

        addButton(new GuiButtonNop(this, 0, guiLeft + 40, guiTop + 190,120,20, "Done"));
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
		if(id == 0)
			close();
	}
    
	@Override
	public void save() {
		tile.name = getTextField(0).getValue();
		tile.range = getTextField(1).getInteger();
		
		CompoundNBT compound = new CompoundNBT();
		tile.save(compound);
		Packets.sendServer(new SPacketTileEntitySave(compound));
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		tile.load(tile.getBlockState(), compound);
		init();
	}
}
