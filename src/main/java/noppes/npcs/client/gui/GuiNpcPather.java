package noppes.npcs.client.gui;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.NBTTags;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataAI;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiNpcPather extends GuiNPCInterface implements IGuiData{
    
    private GuiCustomScroll scroll;
    private HashMap<String, Integer> data = new HashMap<String, Integer>();
    private DataAI ai;
    
	public GuiNpcPather(EntityNPCInterface npc) {
		super();
		drawDefaultBackground = false;
        imageWidth = 176;
        title = "Npc Pather";
        setBackground("smallbg.png");
        ai = npc.ais;
		Packets.sendServer(new SPacketMenuGet(EnumMenuType.MOVING_PATH));
	}
	
	@Override
    public void init(){
        super.init();
        scroll = new GuiCustomScroll(this,0);
        scroll.setSize(160, 164);
        List<String> list = new ArrayList<String>();
        for(int[] arr: ai.getMovingPath()){
        	list.add("x:" + arr[0] + " y:" + arr[1] + " z:" + arr[2]);
        }
        scroll.setUnsortedList(list);
        scroll.guiLeft = guiLeft + 7;
        scroll.guiTop = guiTop + 12;
        
        addScroll(scroll);
        this.addButton(new GuiButtonNop(this, 0, guiLeft + 6, guiTop + 178,52,20, "gui.down"));
        this.addButton(new GuiButtonNop(this, 1, guiLeft + 62, guiTop + 178,52,20, "gui.up"));
        this.addButton(new GuiButtonNop(this, 2, guiLeft + 118, guiTop + 178,52,20, "selectWorld.deleteButton"));
    }

	@Override
	public void buttonEvent(GuiButtonNop guibutton) {
    	if(!scroll.hasSelected())
    		return;
    	
    	int id = guibutton.id;
    	if(id == 0){
    		List<int[]> list = ai.getMovingPath();
    		int selected = scroll.getSelectedIndex();
    		if(list.size() <= selected + 1)
    			return;
    		int[] a = list.get(selected);
    		int[] b = list.get(selected + 1);
    		list.set(selected, b);
    		list.set(selected + 1, a);
    		ai.setMovingPath(list);
    		init();
    		scroll.setSelectedIndex(selected + 1);
    	}
    	if(id == 1){
        	if(scroll.getSelectedIndex() - 1 < 0)
        		return;
    		List<int[]> list = ai.getMovingPath();
    		int selected = scroll.getSelectedIndex();
    		int[] a = list.get(selected);
    		int[] b = list.get(selected - 1);
    		list.set(selected, b);
    		list.set(selected - 1, a);
    		ai.setMovingPath(list);
    		init();
			scroll.setSelectedIndex(selected - 1);
    	}
    	if(id == 2){
    		List<int[]> list = ai.getMovingPath();
    		if(list.size() <= 1)
    			return;
    		list.remove(scroll.getSelectedIndex());
    		ai.setMovingPath(list);
    		init();
    	}
    }

    @Override
    public boolean mouseClicked(double i, double j, int k) {
    	scroll.mouseClicked(i, j, k);
    	return super.mouseClicked(i, j, k);
    }
    
	@Override
	public void save() {
		CompoundNBT compound = new CompoundNBT();
		compound.put("MovingPathNew", NBTTags.nbtIntegerArraySet(ai.getMovingPath()));
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.MOVING_PATH, compound));
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		ai.readToNBT(compound);
		init();
	}

}
