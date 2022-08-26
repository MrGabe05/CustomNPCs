package noppes.npcs.client.gui;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.resources.I18n;
import noppes.npcs.blocks.tiles.TileRedstoneBlock;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;

public class GuiNpcRedstoneBlock extends GuiNPCInterface implements IGuiData{

	private TileRedstoneBlock tile;
	
    public GuiNpcRedstoneBlock(BlockPos pos) {
		super();
		tile = (TileRedstoneBlock) player.level.getBlockEntity(pos);
		Packets.sendServer(new SPacketTileEntityGet(pos));
	}
    
    @Override
	public void init(){
    	super.init();

		this.addButton(new GuiButtonNop(this, 4, guiLeft + 40, guiTop + 20, 120, 20, "availability.options"));

    	addLabel(new GuiLabel(11,"gui.detailed", guiLeft+ 40, guiTop + 47, 0xffffff)); 
		this.addButton(new GuiButtonNop(this, 1, guiLeft + 110, guiTop + 42, 50, 20, new String[]{"gui.no", "gui.yes"},tile.isDetailed?1:0));
    	
		if(tile.isDetailed){
	    	addLabel(new GuiLabel(0,I18n.get("bard.ondistance") + " X:", guiLeft+ 1, guiTop + 76, 0xffffff));
	    	addTextField(new GuiTextFieldNop(0, this,  guiLeft+ 80, guiTop + 71,30,20, tile.onRangeX + ""));
	    	getTextField(0).numbersOnly = true;
	    	getTextField(0).setMinMaxDefault(0, 50, 6); 	
	    	addLabel(new GuiLabel(1,"Y:", guiLeft+ 113, guiTop + 76, 0xffffff)); 
	    	addTextField(new GuiTextFieldNop(1, this,  guiLeft+ 122, guiTop + 71,30,20, tile.onRangeY + ""));
	    	getTextField(1).numbersOnly = true;
	    	getTextField(1).setMinMaxDefault(0, 50, 6); 
	    	addLabel(new GuiLabel(2,"Z:", guiLeft+ 155, guiTop + 76, 0xffffff)); 
	    	addTextField(new GuiTextFieldNop(2, this,  guiLeft+ 164, guiTop + 71,30,20, tile.onRangeZ + ""));
	    	getTextField(2).numbersOnly = true;
	    	getTextField(2).setMinMaxDefault(0, 50, 6);
	    	
	    	addLabel(new GuiLabel(3,I18n.get("bard.offdistance") + " X:", guiLeft - 3, guiTop + 99, 0xffffff));
	    	addTextField(new GuiTextFieldNop(3, this,  guiLeft+ 80, guiTop + 94,30,20, tile.offRangeX + ""));
	    	getTextField(3).numbersOnly = true;
	    	getTextField(3).setMinMaxDefault(0, 50, 10);  	
	    	addLabel(new GuiLabel(4,"Y:", guiLeft+ 113, guiTop + 99, 0xffffff)); 
	    	addTextField(new GuiTextFieldNop(4, this,  guiLeft+ 122, guiTop + 94,30,20, tile.offRangeY + ""));
	    	getTextField(4).numbersOnly = true;
	    	getTextField(4).setMinMaxDefault(0, 50, 10);  	
	    	addLabel(new GuiLabel(5,"Z:", guiLeft+ 155, guiTop + 99, 0xffffff)); 
	    	addTextField(new GuiTextFieldNop(5, this,  guiLeft+ 164, guiTop + 94,30,20, tile.offRangeZ + ""));
	    	getTextField(5).numbersOnly = true;
	    	getTextField(5).setMinMaxDefault(0, 50, 10);
		}
		else{
	    	addLabel(new GuiLabel(0,"bard.ondistance", guiLeft+ 1, guiTop + 76, 0xffffff));    	
	    	addTextField(new GuiTextFieldNop(0, this,  guiLeft+ 80, guiTop + 71,30,20, tile.onRange + ""));
	    	getTextField(0).numbersOnly = true;
	    	getTextField(0).setMinMaxDefault(0, 50, 6); 	
	    	
	    	addLabel(new GuiLabel(3,"bard.offdistance", guiLeft - 3, guiTop + 99, 0xffffff));    	
	    	addTextField(new GuiTextFieldNop(3, this,  guiLeft+ 80, guiTop + 94,30,20, tile.offRange + ""));
	    	getTextField(3).numbersOnly = true;
	    	getTextField(3).setMinMaxDefault(0, 50, 10);  
		}
        addButton(new GuiButtonNop(this, 0, guiLeft + 40, guiTop + 190,120,20, "Done"));
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
		if(id == 0)
			close();
		if(id == 1){
			tile.isDetailed = ((GuiButtonNop)guibutton).getValue() == 1;
			init();
		}
		if (id == 4) {
			save();
			setSubGui(new SubGuiNpcAvailability(tile.availability));
		}
	}
	@Override
	public void save() {
		if(tile == null)
			return;
		if(tile.isDetailed){
			tile.onRangeX = getTextField(0).getInteger();
			tile.onRangeY = getTextField(1).getInteger();
			tile.onRangeZ = getTextField(2).getInteger();

			tile.offRangeX = getTextField(3).getInteger();
			tile.offRangeY = getTextField(4).getInteger();
			tile.offRangeZ = getTextField(5).getInteger();
			
			if(tile.onRangeX > tile.offRangeX)
				tile.offRangeX = tile.onRangeX;
			if(tile.onRangeY > tile.offRangeY)
				tile.offRangeY = tile.onRangeY;
			if(tile.onRangeZ > tile.offRangeZ)
				tile.offRangeZ = tile.onRangeZ;
		}
		else{
			tile.onRange = getTextField(0).getInteger();
			tile.offRange = getTextField(3).getInteger();
			if(tile.onRange > tile.offRange)
				tile.offRange = tile.onRange;
		}
		
		CompoundNBT compound = new CompoundNBT();
		tile.save(compound);
		compound.remove("BlockActivated");
		Packets.sendServer(new SPacketTileEntitySave(compound));
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		tile.load(tile.getBlockState(), compound);
		init();
	}

}
