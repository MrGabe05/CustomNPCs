package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.gui.GuiNpcMobSpawnerSelector;
import noppes.npcs.client.gui.SubGuiNpcBiomes;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.*;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GuiNpcNaturalSpawns extends GuiNPCInterface2 implements IGuiData, IScrollData, ITextfieldListener, ICustomScrollListener, ISliderListener {

	private GuiCustomScroll scroll;
	private Map<String, Integer> data = new HashMap<String, Integer>();

	private SpawnData spawn = new SpawnData();
	
	public GuiNpcNaturalSpawns(EntityNPCInterface npc) {
		super(npc);
    	Packets.sendServer(new SPacketNaturalSpawnGetAll());
	}
	
	@Override
	public void init(){
		super.init();
        if(scroll == null){
	        scroll = new GuiCustomScroll(this,0);
	        scroll.setSize(143, 208);
        }
        scroll.guiLeft = guiLeft + 214;
        scroll.guiTop = guiTop + 4;
        this.addScroll(scroll);

       	this.addButton(new GuiButtonNop(this, 1,guiLeft + 358, guiTop + 38, 58, 20, "gui.add"));
    	this.addButton(new GuiButtonNop(this, 2,guiLeft + 358, guiTop + 61, 58, 20, "gui.remove"));
    	
    	if(spawn.id >= 0)
    		showSpawn();
    	
	}
	
	private void showSpawn() {
		addLabel(new GuiLabel(1,"gui.title", guiLeft + 4, guiTop + 8));
		addTextField(new GuiTextFieldNop(1, this, guiLeft + 60, guiTop + 3, 140, 20, spawn.name));

		addLabel(new GuiLabel(3,"spawning.biomes", guiLeft + 4, guiTop + 30));
    	addButton(new GuiButtonNop(this, 3, guiLeft + 120, guiTop + 25, 50, 20, "selectServer.edit"));

        addSlider(new GuiSliderNop(this, 4, guiLeft + 4, guiTop + 47, 180, 20, (float)spawn.weight / 100));

        int y = guiTop + 70;
    	this.addButton(new GuiButtonNop(this, 25, guiLeft + 14, y,20,20, "X"));
        addLabel(new GuiLabel(5, "1:", guiLeft + 4, y + 5));
    	this.addButton(new GuiButtonNop(this, 5, guiLeft + 36, y, 170, 20, getTitle(1)));
    	
    	addLabel(new GuiLabel(26, "gui.type", guiLeft + 4, guiTop + 100));
    	addButton(new GuiButtonNop(this, 27, guiLeft + 70, guiTop + 93, 120, 20, new String[]{"spawner.any","spawner.dark","spawner.light"}, spawn.type));
	}
    private String getTitle(int slot) {
		if(spawn.data.containsKey(slot)){
			return spawn.data.get(slot).name;
		}
		
		return "gui.selectnpc";
	}

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
        if(id == 1){
        	save();
        	String name = I18n.get("gui.new");
        	while(data.containsKey(name))
        		name += "_";
        	
        	SpawnData spawn = new SpawnData();
        	spawn.name = name;
        	Packets.sendServer(new SPacketNaturalSpawnSave(spawn.writeNBT(new CompoundNBT())));
        	
        }
        if(id == 2){
        	if(data.containsKey(scroll.getSelected())) {
				Packets.sendServer(new SPacketNaturalSpawnRemove(spawn.id));
				spawn = new SpawnData();
        		scroll.clear();
        	}
        }
        if(id == 3){
        	setSubGui(new SubGuiNpcBiomes(spawn));
        }
        if(id == 5){
    		setSubGui(new GuiNpcMobSpawnerSelector());
        }
        if(id == 25){
    		spawn.data.remove(1);
    		init();
        }
        if(id == 27){
        	spawn.type = guibutton.getValue();
        }
    }


	@Override
	public void unFocused(GuiTextFieldNop guiNpcTextField) {
		String name = guiNpcTextField.getValue();
		if(name.isEmpty() || data.containsKey(name)){
			guiNpcTextField.setValue(spawn.name);
		}
		else{
			String old = spawn.name;
			data.remove(old);
			spawn.name = name;
			data.put(spawn.name, spawn.id);
			scroll.replace(old, spawn.name);
		}
	}
	
	@Override
	public void setData(Vector<String> list, Map<String, Integer> data) {
		String name = scroll.getSelected();
		this.data = data;
		scroll.setList(list);
		
		if(name != null)
			scroll.setSelected(name);
		init();
	}

	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll guiCustomScroll) {
		if(guiCustomScroll.id == 0){
			save();
			String selected = scroll.getSelected();
			spawn = new SpawnData();
			Packets.sendServer(new SPacketNaturalSpawnGet(data.get(selected)));
		}
	}

	@Override
	public void save() {
    	GuiTextFieldNop.unfocus();
		if(spawn.id >= 0)
			Packets.sendServer(new SPacketNaturalSpawnSave(spawn.writeNBT(new CompoundNBT())));
	}

	@Override
	public void setSelected(String selected) {
		
	}

	public void subGuiClosed(Screen gui) {
		if(gui instanceof GuiNpcMobSpawnerSelector){
			GuiNpcMobSpawnerSelector selector = (GuiNpcMobSpawnerSelector) gui;
			String selected = selector.getSelected();
			if(selected != null) {
				spawn.setClone(1, selector.activeTab, selected);
			}
			init();
		}
	}
	
	@Override
	public void setGuiData(CompoundNBT compound) {
		spawn.readNBT(compound);
		setSelected(spawn.name);
		init();
	}

	@Override
	public void mouseDragged(GuiSliderNop guiNpcSlider) {
		guiNpcSlider.setMessage(new TranslationTextComponent("spawning.weightedChance").append(": "  + (int) (guiNpcSlider.sliderValue * 100)));
	}

	@Override
	public void mousePressed(GuiSliderNop guiNpcSlider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(GuiSliderNop guiNpcSlider) {
		spawn.setWeight((int) (guiNpcSlider.sliderValue * 100));
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}

}
