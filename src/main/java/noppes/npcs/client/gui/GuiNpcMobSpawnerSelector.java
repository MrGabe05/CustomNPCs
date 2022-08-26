package noppes.npcs.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCloneList;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiMenuSideButton;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNpcMobSpawnerSelector extends GuiBasic implements IGuiData {
    
    private GuiCustomScroll scroll;
    private List<String> list;

	public int activeTab =  1;
    
	public GuiNpcMobSpawnerSelector() {
		super();
        imageWidth = 256;
        setBackground("menubg.png");
	}
	
	
    public void init(){
        super.init();        
        if(scroll == null){
	        scroll = new GuiCustomScroll(this,0);
	        scroll.setSize(165, 210);
        }
        else
        	scroll.clear();
        scroll.guiLeft = guiLeft + 4;
        scroll.guiTop = guiTop + 4;
        addScroll(scroll);

//        GuiMenuTopButton button;
//        addTopButton(button = new GuiMenuTopButton(this, 3,guiLeft + 4, guiTop - 17, "spawner.clones"));
//        button.active = !isServer;
//        addTopButton(button = new GuiMenuTopButton(this, 4, button, "gui.server"));
//        button.active = isServer;

    	addButton(new GuiButtonNop(this, 0, guiLeft + 171, guiTop + 80, 80, 20, "gui.done"));
    	addButton(new GuiButtonNop(this, 1, guiLeft + 171, guiTop + 103, 80, 20, "gui.cancel"));
                
    	addSideButton(new GuiMenuSideButton(this, 21,guiLeft - 69, this.guiTop + 2, 70,22, "Tab 1"));
    	addSideButton(new GuiMenuSideButton(this, 22,guiLeft - 69, this.guiTop + 23, 70,22, "Tab 2"));
    	addSideButton(new GuiMenuSideButton(this, 23,guiLeft - 69, this.guiTop + 44, 70,22, "Tab 3"));
    	addSideButton(new GuiMenuSideButton(this, 24,guiLeft - 69, this.guiTop + 65, 70,22, "Tab 4"));
    	addSideButton(new GuiMenuSideButton(this, 25,guiLeft - 69, this.guiTop + 86, 70,22, "Tab 5"));
    	addSideButton(new GuiMenuSideButton(this, 26,guiLeft - 69, this.guiTop + 107, 70,22, "Tab 6"));
    	addSideButton(new GuiMenuSideButton(this, 27,guiLeft - 69, this.guiTop + 128, 70,22, "Tab 7"));
    	addSideButton(new GuiMenuSideButton(this, 28,guiLeft - 69, this.guiTop + 149, 70,22, "Tab 8"));
    	addSideButton(new GuiMenuSideButton(this, 29,guiLeft - 69, this.guiTop + 170, 70,22, "Tab 9"));
    	
    	getSideButton(20 + activeTab).active = true;
    	showClones();
    }
    
    public String getSelected(){
    	return scroll.getSelected();
    }
	private void showClones() {
		Packets.sendServer(new SPacketCloneList(activeTab));
	}

	@Override
	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
    	if(id == 0){
    		close();
    	}
    	if(id == 1){
    		scroll.clear();
    		close();
    	}
    	if(id > 20){
    		activeTab = id - 20;
    		init();
    	}
    }
    protected ListNBT newDoubleNBTList(double ... par1ArrayOfDouble)
    {
        ListNBT nbttaglist = new ListNBT();
        double[] adouble = par1ArrayOfDouble;
        int i = par1ArrayOfDouble.length;

        for (int j = 0; j < i; ++j)
        {
            double d1 = adouble[j];
            nbttaglist.add(DoubleNBT.valueOf(d1));
        }

        return nbttaglist;
    }
	
	@Override
	public void save() {
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		ListNBT nbtlist = compound.getList("List", 8);
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < nbtlist.size(); i++){
			list.add(nbtlist.getString(i));
		}
		this.list = list;
        scroll.setList(list);
	}

}
