package noppes.npcs.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.client.controllers.ClientCloneController;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCloneList;
import noppes.npcs.packets.server.SPacketCloneRemove;
import noppes.npcs.packets.server.SPacketToolMobSpawner;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiMenuSideButton;
import noppes.npcs.shared.client.gui.components.GuiMenuTopButton;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

import java.util.ArrayList;
import java.util.List;

public class GuiNpcMobSpawner extends GuiNPCInterface implements IGuiData {
    
    private GuiCustomScroll scroll;
    private final BlockPos pos;
    
    private List<String> list;
    
    private static int showingClones = 0;
	
	private int activeTab =  1;
    
	public GuiNpcMobSpawner(BlockPos pos) {
		super();
        imageWidth = 256;

		this.pos = pos;
        
        setBackground("menubg.png");
	}

	@Override
    public void init() {
        super.init();
        guiTop += 10;
        
        if(scroll == null){
	        scroll = new GuiCustomScroll(this,0);
	        scroll.setSize(165, 210);
        }
        else
        	scroll.clear();
        scroll.guiLeft = guiLeft + 4;
        scroll.guiTop = guiTop + 4;
        addScroll(scroll);

        GuiMenuTopButton button;
        addTopButton(button = new GuiMenuTopButton(this, 3,guiLeft + 4, guiTop - 17, "spawner.clones"));
        button.active = showingClones == 0;
        addTopButton(button = new GuiMenuTopButton(this, 4, button, "spawner.entities"));
        button.active = showingClones == 1;
        addTopButton(button = new GuiMenuTopButton(this, 5, button, "gui.server"));
        button.active = showingClones == 2;
        
        addButton(new GuiButtonNop(this, 1, guiLeft + 170, guiTop + 6, 82, 20, "gui.spawn"));
        
        addButton(new GuiButtonNop(this, 2, guiLeft + 170, guiTop + 100, 82, 20, "spawner.mobspawner"));
        
        if(showingClones == 0 || showingClones == 2){
        	addSideButton(new GuiMenuSideButton(this,21,guiLeft - 69, this.guiTop + 2, 70,22, "Tab 1"));
        	addSideButton(new GuiMenuSideButton(this, 22,guiLeft - 69, this.guiTop + 23, 70,22, "Tab 2"));
        	addSideButton(new GuiMenuSideButton(this, 23,guiLeft - 69, this.guiTop + 44, 70,22, "Tab 3"));
        	addSideButton(new GuiMenuSideButton(this, 24,guiLeft - 69, this.guiTop + 65, 70,22, "Tab 4"));
        	addSideButton(new GuiMenuSideButton(this, 25,guiLeft - 69, this.guiTop + 86, 70,22, "Tab 5"));
        	addSideButton(new GuiMenuSideButton(this, 26,guiLeft - 69, this.guiTop + 107, 70,22, "Tab 6"));
        	addSideButton(new GuiMenuSideButton(this, 27,guiLeft - 69, this.guiTop + 128, 70,22, "Tab 7"));
        	addSideButton(new GuiMenuSideButton(this, 28,guiLeft - 69, this.guiTop + 149, 70,22, "Tab 8"));
        	addSideButton(new GuiMenuSideButton(this, 29,guiLeft - 69, this.guiTop + 170, 70,22, "Tab 9"));
        	
            addButton(new GuiButtonNop(this, 6, guiLeft + 170, guiTop + 30, 82, 20, "gui.remove"));
        	
        	getSideButton(20 + activeTab).active = true;
        	showClones();
        }
        else
        	showEntities();
    }

	private void showEntities() {
        this.list = new ArrayList<String>(EntityUtil.getAllEntities(Minecraft.getInstance().level, false).keySet());
        scroll.setList(list);
	}
	private void showClones() {
		if(showingClones == 2){
			Packets.sendServer(new SPacketCloneList(activeTab));
			return;
		}
        
        ArrayList<String> list = new ArrayList<String>();
        this.list = ClientCloneController.Instance.getClones(activeTab);
        scroll.setList(this.list);
	}

	private CompoundNBT getCompound(){
    	String sel = scroll.getSelected();
    	if(sel == null)
    		return null;
    	
    	if(showingClones == 0){
    		return ClientCloneController.Instance.getCloneData(player.createCommandSourceStack(), sel, activeTab);
    	}
    	else{
			ResourceLocation loc = EntityUtil.getAllEntities(Minecraft.getInstance().level, false).get(sel);
			EntityType type = ForgeRegistries.ENTITIES.getValue(loc);
			Entity entity = type.create(Minecraft.getInstance().level);
    		if(entity == null)
    			return null;
    		CompoundNBT compound = new CompoundNBT();
    		entity.saveAsPassenger(compound);
    		return compound;
    	}
	}
	
	@Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
    	if(id == 0){
    		close();
    	}
    	if(id == 1){
    		if(showingClones == 2){
    	    	String sel = scroll.getSelected();
    	    	if(sel == null)
    	    		return;
    	    	Packets.sendServer(new SPacketToolMobSpawner(false, pos, sel, activeTab));
	    		close();
    		}
    		else{
	    		CompoundNBT compound = getCompound();
	    		if(compound == null)
	    			return;
    			Packets.sendServer(new SPacketToolMobSpawner(false, pos, compound));
	    		close();
	    		
    		}
    	}
    	if(id == 2){
    		if(showingClones == 2){
    	    	String sel = scroll.getSelected();
    	    	if(sel == null)
    	    		return;
    	    	Packets.sendServer(new SPacketToolMobSpawner(true, pos, sel, activeTab));
		    	close();
    		}
    		else{
	    		CompoundNBT compound = getCompound();
	    		if(compound == null)
	    			return;
	    		Packets.sendServer(new SPacketToolMobSpawner(true, pos, compound));
		    	close();
	    		
    		}
    	}
    	if(id == 3){
    		showingClones = 0;
    		init();
    	}
    	if(id == 4){
    		showingClones = 1;
    		init();
    	}
    	if(id == 5){
    		showingClones = 2;
    		init();
    	}
    	if(id == 6){
    		if(scroll.getSelected() != null){
    			if(showingClones == 2){
    				Packets.sendServer(new SPacketCloneRemove(scroll.getSelected(), activeTab));
    				return;
    			}
    			ClientCloneController.Instance.removeClone(scroll.getSelected(), activeTab);
    			scroll.clearSelection();
        		init();
    		}
    	}
    	if(id > 20){
    		activeTab = id - 20;
    		init();
    	}
    }
	
	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setGuiData(CompoundNBT compound) {
		ListNBT nbtlist = compound.getList("List", 8);
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < nbtlist.size(); i++){
			list.add(nbtlist.getString(i));
		}
		this.list = list;
        scroll.setList(this.list);
	}

}
