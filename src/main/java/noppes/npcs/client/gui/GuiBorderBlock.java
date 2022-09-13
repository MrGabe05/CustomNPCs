package noppes.npcs.client.gui;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.blocks.tiles.TileBorder;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;

public class GuiBorderBlock extends GuiNPCInterface implements IGuiData{

	private final TileBorder tile;
	
    public GuiBorderBlock(BlockPos pos) {
		super();
		tile = (TileBorder) player.level.getBlockEntity(pos);
		
		Packets.sendServer(new SPacketTileEntityGet(pos));
	}

	@Override
	public void init(){
    	super.init();
    	
		this.addButton(new GuiButtonNop(this, 4, guiLeft + 40, guiTop + 40, 120, 20, "Availability Options"));
    	
    	addLabel(new GuiLabel(0,"Height", guiLeft+ 1, guiTop + 76, 0xffffff));    	
    	addTextField(new GuiTextFieldNop(0, this,  guiLeft+ 60, guiTop + 71,40,20, tile.height + ""));
    	getTextField(0).numbersOnly = true;
    	getTextField(0).setMinMaxDefault(0, 500, 6); 	

    	addLabel(new GuiLabel(1,"Message", guiLeft+ 1, guiTop + 100, 0xffffff));    	
    	addTextField(new GuiTextFieldNop(1, this,  guiLeft+ 60, guiTop + 95,200,20, tile.message));
    	
        addButton(new GuiButtonNop(this, 0, guiLeft + 40, guiTop + 190,120,20, "Done"));
    }

	@Override
	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
		if(id == 0)
			close();
		if (id == 4) {
			save();
			setSubGui(new SubGuiNpcAvailability(tile.availability));
		}
	}
	
	@Override
	public void save() {
		if(tile == null)
			return;
		tile.height = getTextField(0).getInteger();
		tile.message = getTextField(1).getValue();
		
		CompoundNBT compound = new CompoundNBT();
		tile.save(compound);
		Packets.sendServer(new SPacketTileEntitySave(compound));
	}
	
	@Override
	public void setGuiData(CompoundNBT compound) {
		tile.readExtraNBT(compound);
		init();
	}

}
