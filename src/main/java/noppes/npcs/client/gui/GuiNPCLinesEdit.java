package noppes.npcs.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.Lines;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

import java.util.HashMap;

public class GuiNPCLinesEdit extends GuiNPCInterface2 implements IGuiData {
	private final Lines lines;
	private int selectedId = -1;
	private GuiSoundSelection gui;
	
	public GuiNPCLinesEdit(EntityNPCInterface npc, Lines lines){
    	super(npc);
    	this.lines = lines;
		Packets.sendServer(new SPacketMenuGet(EnumMenuType.ADVANCED));
    }

	@Override
    public void init(){
        super.init();
        for(int i = 0; i < 8; i++){
        	String text = "";
        	String sound = "";
        	if(lines.lines.containsKey(i)){
        		Line line = lines.lines.get(i);
        		text = line.getText();
        		sound = line.getSound();
        	}
        	addTextField(new GuiTextFieldNop(i,this,  guiLeft + 4, guiTop + 4 + i * 24, 200, 20,text));
        	addTextField(new GuiTextFieldNop(i + 8,this,  guiLeft + 208, guiTop + 4 + i * 24, 146, 20,sound));
        	addButton(new GuiButtonNop(this, i, guiLeft + 358, guiTop + 4 + i * 24, 60, 20, "mco.template.button.select"));
        }
        
    }

	@Override
	public void buttonEvent(GuiButtonNop guibutton){
    	GuiButtonNop button = guibutton;
    	selectedId = button.id + 8;
    	setSubGui(new GuiSoundSelection(getTextField(selectedId).getValue()));
    }

	@Override
	public void setGuiData(CompoundNBT compound) {
		npc.advanced.readToNBT(compound);
		init();
	}
	
	private void saveLines(){
		HashMap<Integer,Line> lines = new HashMap<Integer,Line>();
    	for(int i = 0; i < 8; i++){
    		GuiTextFieldNop tf = getTextField(i);
    		GuiTextFieldNop tf2 = getTextField(i + 8);
    		if(!tf.isEmpty() || !tf2.isEmpty()){
        		Line line = new Line();
    			line.setText(tf.getValue());
    			line.setSound(tf2.getValue());
        		lines.put(i, line);
    		}
    		
    	}
    	this.lines.lines = lines;
	}
	
	public void save() {
		saveLines();
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.ADVANCED, npc.advanced.save(new CompoundNBT())));
	}

	@Override
	public void subGuiClosed(Screen subgui) {
		GuiSoundSelection gss = (GuiSoundSelection) subgui;
		if(gss.selectedResource != null) {
			getTextField(selectedId).setValue(gss.selectedResource.toString());
			saveLines();
			init();
		}
	}


}
