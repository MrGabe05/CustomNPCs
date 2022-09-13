package noppes.npcs.client.gui.roles;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.GuiNpcMobSpawnerSelector;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.packets.server.SPacketNpcJobSpawnerSet;
import noppes.npcs.roles.JobSpawner;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;


public class GuiNpcSpawner extends GuiNPCInterface2 implements ITextfieldListener {
	private final JobSpawner job;
	
	private int slot = -1;
	
    public GuiNpcSpawner(EntityNPCInterface npc) {
    	super(npc);    	
    	job = (JobSpawner) npc.job;
    }

    @Override
    public void init() {
    	super.init();

    	int y = guiTop + 6;

    	this.addButton(new GuiButtonNop(this, 20, guiLeft + 25, y,20,20, "X"));
        addLabel(new GuiLabel(0, "1:", guiLeft + 4, y + 5));
    	this.addButton(new GuiButtonNop(this, 0, guiLeft + 50, y, job.getTitle(1)));

    	y += 23; 
    	this.addButton(new GuiButtonNop(this, 21, guiLeft + 25, y,20,20, "X"));
        addLabel(new GuiLabel(1, "2:", guiLeft + 4, y + 5));
    	this.addButton(new GuiButtonNop(this, 1, guiLeft + 50, y, job.getTitle(2)));

    	y += 23; 
    	this.addButton(new GuiButtonNop(this, 22, guiLeft + 25, y,20,20, "X"));
        addLabel(new GuiLabel(2, "3:", guiLeft + 4, y + 5));
    	this.addButton(new GuiButtonNop(this, 2, guiLeft + 50, y, job.getTitle(3)));

    	y += 23; 
    	this.addButton(new GuiButtonNop(this, 23, guiLeft + 25, y,20,20, "X"));
        addLabel(new GuiLabel(3, "4:", guiLeft + 4, y + 5));
    	this.addButton(new GuiButtonNop(this, 3, guiLeft + 50, y, job.getTitle(4)));

    	y += 23; 
    	this.addButton(new GuiButtonNop(this, 24, guiLeft + 25, y,20,20, "X"));
        addLabel(new GuiLabel(4, "5:", guiLeft + 4, y + 5));
    	this.addButton(new GuiButtonNop(this, 4, guiLeft + 50, y, job.getTitle(5)));

    	y += 23; 
    	this.addButton(new GuiButtonNop(this, 25, guiLeft + 25, y,20,20, "X"));
        addLabel(new GuiLabel(5, "6:", guiLeft + 4, y + 5));
    	this.addButton(new GuiButtonNop(this, 5, guiLeft + 50, y, job.getTitle(6)));

    	y += 23; 
        addLabel(new GuiLabel(6, "spawner.diesafter", guiLeft + 4, y + 5));
    	this.addButton(new GuiButtonNop(this, 26, guiLeft + 115, y,40,20, new String[]{"gui.yes","gui.no"}, job.doesntDie?1:0));

        addLabel(new GuiLabel(11, "spawner.despawn", guiLeft + 170, y + 5));
    	this.addButton(new GuiButtonNop(this, 11, guiLeft + 335, y,40,20, new String[]{"gui.no","gui.yes"}, job.despawnOnTargetLost?1:0));

    	y += 23; 
        addLabel(new GuiLabel(7,I18n.get("spawner.posoffset")+" X:", guiLeft + 4, y + 5));
    	addTextField(new GuiTextFieldNop(7,this,  guiLeft + 99, y, 24, 20, job.xOffset + ""));
    	getTextField(7).numbersOnly = true;
        getTextField(7).setMinMaxDefault(-9, 9, 0);
        addLabel(new GuiLabel(8,"Y:", guiLeft + 125, y + 5));
    	addTextField(new GuiTextFieldNop(8,this,  guiLeft + 135, y, 24, 20, job.yOffset + ""));
    	getTextField(8).numbersOnly = true;
        getTextField(8).setMinMaxDefault(-9, 9, 0);
        addLabel(new GuiLabel(9,"Z:", guiLeft + 161, y + 5));
    	addTextField(new GuiTextFieldNop(9,this,  guiLeft + 171, y, 24, 20, job.zOffset + ""));
    	getTextField(9).numbersOnly = true;
        getTextField(9).setMinMaxDefault(-9, 9, 0);  
        
    	y += 23; 
        addLabel(new GuiLabel(10, "spawner.type", guiLeft + 4, y + 5));
        addButton(new GuiButtonNop(this, 10, guiLeft + 80, y, 100, 20, new String[]{"spawner.one", "spawner.all", "spawner.random"}, job.spawnType));
    }

	@Override
    public void elementClicked(){
    }

	@Override
    public void buttonEvent(GuiButtonNop guibutton){
    	GuiButtonNop button = guibutton;
    	if(button.id >= 0 && button.id < 6){
    		slot = button.id + 1;
    		setSubGui(new GuiNpcMobSpawnerSelector());
    	}    
    	if(button.id >= 20 && button.id < 26){
			int slot = button.id - 19;
			job.remove(slot);
			init();
    	}
    	if(button.id == 26){
    		job.doesntDie = button.getValue() == 1;
    	}
    	if(button.id == 10){
    		job.spawnType = button.getValue();
    	}
    	if(button.id == 11){
    		job.despawnOnTargetLost = button.getValue() == 1;
    	}
    }

	@Override
	public void subGuiClosed(Screen gui) {
		GuiNpcMobSpawnerSelector selector = (GuiNpcMobSpawnerSelector) gui;
		String selected = selector.getSelected();
		if(selected != null) {
			job.setJobCompound(slot, selector.activeTab, selected);
			Packets.sendServer(new SPacketNpcJobSpawnerSet(selector.activeTab, selected, slot));
		}
		init();
	}

    @Override
	public void save() {
    	CompoundNBT compound = job.save(new CompoundNBT());
		Packets.sendServer(new SPacketNpcJobSave(compound));
	}

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 7){
			job.xOffset = textfield.getInteger();
		}
		if(textfield.id == 8){
			job.yOffset = textfield.getInteger();
		}
		if(textfield.id == 9){
			job.zOffset = textfield.getInteger();
		}
	}

}
