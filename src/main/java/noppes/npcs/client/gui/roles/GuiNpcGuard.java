package noppes.npcs.client.gui.roles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.roles.JobGuard;

import java.util.ArrayList;
import java.util.Map;

public class GuiNpcGuard extends GuiNPCInterface2{	
	private final JobGuard role;
	private GuiCustomScroll scroll1;
	private GuiCustomScroll scroll2;
	
    public GuiNpcGuard(EntityNPCInterface npc){
    	super(npc);    	
    	role = (JobGuard) npc.job;
    }

	@Override
    public void init(){
    	super.init();
    	
    	this.addButton(new GuiButtonNop(this, 0, guiLeft + 10, guiTop + 4, 100, 20, "guard.animals"));
    	this.addButton(new GuiButtonNop(this, 1, guiLeft + 140, guiTop + 4, 100, 20, "guard.mobs"));
    	this.addButton(new GuiButtonNop(this, 2, guiLeft + 275, guiTop + 4, 100, 20, "guard.creepers"));
    	
	    if(scroll1 == null)
	    {
	      	scroll1 = new GuiCustomScroll(this,0);
	      	scroll1.setSize(175, 154);
	    }
	    scroll1.guiLeft = guiLeft + 4;
	    scroll1.guiTop = guiTop + 58;
	    this.addScroll(scroll1);
	    addLabel(new GuiLabel(11, "guard.availableTargets", guiLeft + 4, guiTop + 48));
	        
	    if(scroll2 == null){
	       	scroll2 = new GuiCustomScroll(this,1);
	      	scroll2.setSize(175, 154);
	    }
	    scroll2.guiLeft = guiLeft + 235;
	    scroll2.guiTop = guiTop + 58;
	    this.addScroll(scroll2);
	    addLabel(new GuiLabel(12, "guard.currentTargets", guiLeft + 235, guiTop + 48));

	    scroll1.setList(new ArrayList<>(EntityUtil.getAllEntities(npc.level, false).keySet()));
	    scroll2.setList(role.targets);
	        
	
	    addButton(new GuiButtonNop(this, 11, guiLeft + 180, guiTop + 80, 55, 20, ">"));
	    addButton(new GuiButtonNop(this, 12, guiLeft + 180, guiTop + 102, 55, 20, "<"));
	
	    addButton(new GuiButtonNop(this, 13, guiLeft + 180, guiTop + 130, 55, 20, ">>"));
	    addButton(new GuiButtonNop(this, 14, guiLeft + 180, guiTop + 152, 55, 20, "<<"));
    }

	@Override
    public void buttonEvent(GuiButtonNop guibutton){
    	GuiButtonNop button = guibutton;
        if(button.id == 0){
			for(Map.Entry<EntityType<? extends Entity>, Class> entry : EntityUtil.getAllEntitiesClasses(minecraft.level).entrySet()){
				EntityType<?> ent = entry.getKey();
    			Class<? extends Entity> cl = entry.getValue();
	        	String name = ent.getDescriptionId();
	        	if(AnimalEntity.class.isAssignableFrom(cl) && !role.targets.contains(name)){
					role.targets.add(name);
				}
        	}
			scroll1.clearSelection();
			scroll2.clearSelection();
			init();
        }
        if(button.id == 1){
			for(Map.Entry<EntityType<? extends Entity>, Class> entry : EntityUtil.getAllEntitiesClasses(minecraft.level).entrySet()){
				EntityType<?> ent = entry.getKey();
				Class<? extends Entity> cl = entry.getValue();
	        	String name = ent.getDescriptionId();
	        	if(MonsterEntity.class.isAssignableFrom(cl) && !CreeperEntity.class.isAssignableFrom(cl) && !role.targets.contains(name)){
					role.targets.add(name);
				}
        	}
			scroll1.clearSelection();
			scroll2.clearSelection();
			init();
        }
        if(button.id == 2){
			for(Map.Entry<EntityType<? extends Entity>, Class> entry : EntityUtil.getAllEntitiesClasses(minecraft.level).entrySet()){
				EntityType<?> ent = entry.getKey();
				Class<? extends Entity> cl = entry.getValue();
	        	String name = ent.getDescriptionId();
	        	if(CreeperEntity.class.isAssignableFrom(cl) && !role.targets.contains(name)) {
					role.targets.add(name);
				}
        	}
			scroll1.clearSelection();
			scroll2.clearSelection();
			init();
        }       

		if(button.id == 11){
			if(scroll1.hasSelected()){
				role.targets.add(scroll1.getSelected());
				scroll1.clearSelection();
				scroll2.clearSelection();
				init();
			}				
		}
		if(button.id == 12){
			if(scroll2.hasSelected()){
				role.targets.remove(scroll2.getSelected());
				scroll2.clearSelection();
				init();
			}				
		}
		if(button.id == 13){
	        role.targets = new ArrayList<>(EntityUtil.getAllEntities(npc.level, false).keySet());
			scroll1.clearSelection();
			scroll2.clearSelection();
			init();
		}
		if(button.id == 14){
			role.targets.clear();
			scroll1.clearSelection();
			scroll2.clearSelection();
			init();
		}
    }

	@Override
	public void save() {
		Packets.sendServer(new SPacketNpcJobSave(role.save(new CompoundNBT())));
	}

}
