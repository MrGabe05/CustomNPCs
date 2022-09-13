package noppes.npcs.client.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketSchematicsTileBuild;
import noppes.npcs.packets.server.SPacketSchematicsTileGet;
import noppes.npcs.packets.server.SPacketSchematicsTileSave;
import noppes.npcs.packets.server.SPacketSchematicsTileSet;
import noppes.npcs.schematics.ISchematic;
import noppes.npcs.schematics.SchematicWrapper;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class GuiBlockBuilder extends GuiNPCInterface implements IGuiData, ICustomScrollListener, IScrollData, BooleanConsumer {
	private final BlockPos pos;
	private final TileBuilder tile;
	private GuiCustomScroll scroll;
	private ISchematic selected = null;
	
	public GuiBlockBuilder(BlockPos pos) {
		this.pos = pos;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
		tile = (TileBuilder) player.level.getBlockEntity(pos);
		Packets.sendServer(new SPacketSchematicsTileGet(pos));
	}
	
	@Override
	public void init(){
		super.init();

        if(scroll == null){
	        scroll = new GuiCustomScroll(this,0);
	        scroll.setSize(125, 208);
        }
        scroll.guiLeft = guiLeft + 4;
        scroll.guiTop = guiTop + 4;
        addScroll(scroll);
        
        if(selected != null){
        	int y = guiTop + 4;
        	int size = selected.getWidth() * selected.getHeight() * selected.getLength();
        	addButton(new GuiButtonYesNo(this, 3, guiLeft + 200, y, TileBuilder.DrawPos != null && tile.getBlockPos().equals(TileBuilder.DrawPos)));
        	addLabel(new GuiLabel(3, "schematic.preview", guiLeft + 130, y + 5));
        	
        	addLabel(new GuiLabel(0, I18n.get("schematic.width") + ": " + selected.getWidth(), guiLeft + 130, y += 21));
        	addLabel(new GuiLabel(1, I18n.get("schematic.length") + ": " + selected.getLength(), guiLeft + 130, y += 11));
        	addLabel(new GuiLabel(2, I18n.get("schematic.height") + ": " + selected.getHeight(), guiLeft + 130, y += 11));
        	

        	addButton(new GuiButtonYesNo(this, 4, guiLeft + 200, y += 14, tile.enabled));
        	addLabel(new GuiLabel(4, I18n.get("gui.enabled"), guiLeft + 130, y + 5));
        	
        	addButton(new GuiButtonYesNo(this, 7, guiLeft + 200, y += 22, tile.finished));
        	addLabel(new GuiLabel(7, I18n.get("gui.finished"), guiLeft + 130, y + 5));

        	addButton(new GuiButtonYesNo(this, 8, guiLeft + 200, y += 22, tile.started));
        	addLabel(new GuiLabel(8, I18n.get("gui.started"), guiLeft + 130, y + 5));
        	
        	addTextField(new GuiTextFieldNop(9, this, guiLeft + 200, y += 22, 50, 20, tile.yOffest + ""));
        	addLabel(new GuiLabel(9, I18n.get("gui.yoffset"), guiLeft + 130, y + 5));
        	getTextField(9).numbersOnly = true;
        	getTextField(9).setMinMaxDefault(-10, 10, 0);
        	
        	addButton(new GuiButtonNop(this, 5, guiLeft + 200, y += 22, 50, 20, new String[]{"0", "90", "180", "270"}, tile.rotation));
        	addLabel(new GuiLabel(5, I18n.get("movement.rotation"), guiLeft + 130, y + 5));
        	
        	addButton(new GuiButtonNop(this, 6, guiLeft + 130, y += 22, 120, 20, "availability.options"));
        	
        	addButton(new GuiButtonNop(this, 10, guiLeft + 130, y += 22, 120, 20, "schematic.instantBuild"));
        }
	}

    @Override
	public void buttonEvent(GuiButtonNop guibutton) {
    	if(guibutton.id == 3){
    		GuiButtonYesNo button = (GuiButtonYesNo) guibutton;
    		if(button.getBoolean()){
    			TileBuilder.SetDrawPos(pos);
				tile.setDrawSchematic(new SchematicWrapper(selected));
    		}
    		else{
    			TileBuilder.SetDrawPos(null);
				tile.setDrawSchematic(null);
    		}
    	}
    	if(guibutton.id == 4){
    		tile.enabled = ((GuiButtonYesNo) guibutton).getBoolean();
    	}
    	if(guibutton.id == 5){
    		tile.rotation = guibutton.getValue();
    		TileBuilder.Compiled = false;
    	}
    	if(guibutton.id == 6){
    		setSubGui(new SubGuiNpcAvailability(tile.availability));
    	}
    	if(guibutton.id == 7){
    		tile.finished = ((GuiButtonYesNo) guibutton).getBoolean();
    		Packets.sendServer(new SPacketSchematicsTileSet(pos, scroll.getSelected()));
    	}
    	if(guibutton.id == 8){
    		tile.started = ((GuiButtonYesNo) guibutton).getBoolean();
    	}
    	if(guibutton.id == 10){
    		save();
            ConfirmScreen guiyesno = new ConfirmScreen(this, StringTextComponent.EMPTY, new TranslationTextComponent("schematic.instantBuildText"));
            setScreen(guiyesno);
    	}
    }
    
	@Override
	public void save() {
		if(getTextField(9) != null){
			tile.yOffest = getTextField(9).getInteger();
		}
		Packets.sendServer(new SPacketSchematicsTileSave(pos, tile.writePartNBT(new CompoundNBT())));
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		if(compound.contains("Width")){
			List<BlockState> states = new ArrayList<BlockState>();
			ListNBT list = compound.getList("Data", 10);
			for(int i = 0; i < list.size(); i++){
				states.add(NBTUtil.readBlockState(list.getCompound(i)));
			}
			
			selected = new ISchematic() {
				
				@Override
				public short getWidth() {
					return compound.getShort("Width");
				}
				
				@Override
				public int getBlockEntitySize() {
					return 0;
				}
				
				@Override
				public CompoundNBT getBlockEntity(int i) {
					return null;
				}
				
				@Override
				public String getName() {
					return compound.getString("SchematicName");
				}
				
				@Override
				public short getLength() {
					return compound.getShort("Length");
				}
				
				@Override
				public short getHeight() {
					return compound.getShort("Height");
				}
				
				@Override
				public BlockState getBlockState(int i) {
					return states.get(i);
				}
				
				@Override
				public BlockState getBlockState(int x, int y, int z) {
					return getBlockState((y * getLength() + z) * getWidth() + x);
				}

				@Override
				public CompoundNBT getNBT() {
					// TODO Auto-generated method stub
					return null;
				}
			};
			
			if(TileBuilder.DrawPos != null && TileBuilder.DrawPos.equals(tile.getBlockPos())){
				SchematicWrapper wrapper = new SchematicWrapper(selected);
				wrapper.rotation = tile.rotation;
				tile.setDrawSchematic(wrapper);
			}
			scroll.setSelected(selected.getName());
			scroll.scrollTo(selected.getName());
		}
		else{
			tile.readPartNBT(compound);
		}
		init();
	}

	@Override
    public void accept(boolean flag)
    {
		if(flag){
			Packets.sendServer(new SPacketSchematicsTileBuild(pos));
	        close();
	        selected = null;
		}
		else {
			NoppesUtil.openGUI(player, this);
		}
    }
	
	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
		if(!scroll.hasSelected())
			return;
        if(selected != null)
        	getButton(3).setDisplay(0);
		TileBuilder.SetDrawPos(null);
		tile.setDrawSchematic(null);
		Packets.sendServer(new SPacketSchematicsTileSet(pos, scroll.getSelected()));
	}

	@Override
	public void setData(Vector<String> list, Map<String, Integer> data) {
		scroll.setList(list);
		if(selected != null)
			scroll.setSelected(selected.getName());
		init();
	}

	@Override
	public void setSelected(String selected) {
		
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}

}
