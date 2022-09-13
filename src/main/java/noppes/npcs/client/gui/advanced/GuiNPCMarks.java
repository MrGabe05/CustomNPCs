package noppes.npcs.client.gui.advanced;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.api.constants.MarkType;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.SubGuiNpcAvailability;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.controllers.data.MarkData.Mark;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiButtonBiDirectional;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class GuiNPCMarks extends GuiNPCInterface2 {
	private final String[] marks = new String[]{"gui.none", "mark.question", "mark.exclamation", "mark.pointer", "mark.skull", "mark.cross", "mark.star"};
	private final MarkData data;
	private Mark selectedMark;
	
    public GuiNPCMarks(EntityNPCInterface npc){
    	super(npc);
    	this.data = MarkData.get(npc);
    }

    @Override
    public void init(){
        super.init();
        int y = guiTop + 14;
        for(int i = 0; i < data.marks.size(); i++){
        	Mark mark = data.marks.get(i);
           	this.addButton(new GuiButtonBiDirectional(this,1 + i * 10, guiLeft + 6, y, 120, 20, marks, mark.type));
           	
        	String color = Integer.toHexString(mark.color);
        	while(color.length() < 6)
        		color = "0" + color;
        	addButton(new GuiButtonNop(this, 2 + i * 10, guiLeft + 128, y, 60, 20, color));
        	getButton(2 + i * 10).setFGColor(mark.color);
        	
        	this.addButton(new GuiButtonNop(this, 3 + i * 10, guiLeft + 190, y, 120, 20, "availability.options"));
        	this.addButton(new GuiButtonNop(this, 4 + i * 10, guiLeft + 312, y, 40, 20, "X"));
        	y += 22;
        }
        
        if(data.marks.size() < 9){
        	this.addButton(new GuiButtonNop(this, 101 , guiLeft + 6, y + 2, 60, 20, "gui.add"));
        }        
    }

    @Override
	public void buttonEvent(GuiButtonNop button){
    	if(button.id < 90){
    		selectedMark = data.marks.get(button.id / 10);
	        if(button.id % 10 == 1){
	        	selectedMark.type = button.getValue();
	        }
	        if(button.id % 10 == 2){
	        	this.setSubGui(new SubGuiColorSelector(selectedMark.color));
	        }
	        if(button.id % 10 == 3){
	        	setSubGui(new SubGuiNpcAvailability(selectedMark.availability));
	        }
	        if(button.id % 10 == 4){
	        	data.marks.remove(selectedMark);
	        	init();
	        }
    	}
        if(button.id == 101){
        	data.addMark(MarkType.NONE);
    		init();
        }        
    }

	@Override
	public void subGuiClosed(Screen subgui) {
		if(subgui instanceof SubGuiColorSelector){
			selectedMark.color = ((SubGuiColorSelector)subgui).color;
	    	init();
		}
	}

	@Override
	public void save() {
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.MARK, data.getNBT()));
	}
	
}
