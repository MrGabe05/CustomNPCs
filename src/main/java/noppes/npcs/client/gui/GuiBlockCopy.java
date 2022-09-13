package noppes.npcs.client.gui;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketSchematicsStore;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;

public class GuiBlockCopy extends GuiNPCInterface implements IGuiData, ITextfieldListener{
	private final BlockPos pos;
	private final TileCopy tile;
	
	public GuiBlockCopy(BlockPos pos) {
		this.pos = pos;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
		tile = (TileCopy) player.level.getBlockEntity(pos);

		Packets.sendServer(new SPacketTileEntityGet(pos));
	}
	
	@Override
	public void init(){
		super.init();
		int y = guiTop + 4;
		addTextField(new GuiTextFieldNop(0, this, guiLeft + 104, y, 50, 20, tile.height + ""));
		addLabel(new GuiLabel(0, "schematic.height", guiLeft + 5, y + 5));
		getTextField(0).numbersOnly = true;
		getTextField(0).setMinMaxDefault(0, 100, 10);
		
		addTextField(new GuiTextFieldNop(1, this, guiLeft + 104, y += 23, 50, 20, tile.width + ""));
		addLabel(new GuiLabel(1, "schematic.width", guiLeft + 5, y + 5));
		getTextField(1).numbersOnly = true;
		getTextField(1).setMinMaxDefault(0, 100, 10);
		
		addTextField(new GuiTextFieldNop(2, this, guiLeft + 104, y += 23, 50, 20, tile.length + ""));
		addLabel(new GuiLabel(2, "schematic.length", guiLeft + 5, y + 5));
		getTextField(2).numbersOnly = true;
		getTextField(2).setMinMaxDefault(0, 100, 10);

		addTextField(new GuiTextFieldNop(5, this, guiLeft + 104, y += 23, 100, 20, ""));
		addLabel(new GuiLabel(5, "gui.name", guiLeft + 5, y + 5));

		addButton(new GuiButtonNop(this, 0, guiLeft + 5, y += 30, 60, 20, "gui.save"));
		addButton(new GuiButtonNop(this, 1, guiLeft + 67, y, 60, 20, "gui.cancel"));
	}

    @Override
	public void buttonEvent(GuiButtonNop guibutton) {
    	if(guibutton.id == 0){
    		CompoundNBT compound = new CompoundNBT();
    		tile.save(compound);
    		Packets.sendServer(new SPacketSchematicsStore(getTextField(5).getValue(), compound));
    		close();
    	}
    	if(guibutton.id == 1){
    		close();
    	}
    }
    
	@Override
	public void save() {
		CompoundNBT compound = new CompoundNBT();
		tile.save(compound);
		Packets.sendServer(new SPacketTileEntitySave(compound));
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		tile.load(tile.getBlockState(), compound);
		init();
	}

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 0)
			tile.height = (short) textfield.getInteger();
		if(textfield.id == 1)
			tile.width = (short) textfield.getInteger();
		if(textfield.id == 2)
			tile.length = (short) textfield.getInteger();
	}
}
