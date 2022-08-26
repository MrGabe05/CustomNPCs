package noppes.npcs.client.gui.roles;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.roles.JobHealer;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiNpcHealer extends GuiNPCInterface2 {
	private JobHealer job;
	private GuiCustomScroll scroll1;
	private GuiCustomScroll scroll2;
	private HashMap<String, Integer> potions;
	private HashMap<String, String> displays;
	private int potency = 0; //dummy value
	
    public GuiNpcHealer(EntityNPCInterface npc){
    	super(npc);    	
    	job = (JobHealer) npc.job;
    	potions = new HashMap<String, Integer>();
    	displays = new HashMap<String, String>();
		Registry<Effect> r = Registry.MOB_EFFECT;
		for(ResourceLocation rl : r.keySet()){
			potions.put(rl.toString(), r.getId(r.get(rl)));
		}
    }

	@Override
    public void init() {
    	super.init();

        addLabel(new GuiLabel(1,"beacon.range", guiLeft + 10, guiTop + 9));
        addTextField(new GuiTextFieldNop(1,this, guiLeft+80, guiTop + 4, 40, 20, job.range + ""));
        getTextField(1).numbersOnly = true;
        getTextField(1).setMinMaxDefault(1, 64, 16);
        
        addLabel(new GuiLabel(4,"stats.speed", guiLeft + 140, guiTop + 9));
        addTextField(new GuiTextFieldNop(4,this, guiLeft+220, guiTop + 4, 40, 20, potency + ""));
        getTextField(4).numbersOnly = true;
        getTextField(4).setMinMaxDefault(10, Integer.MAX_VALUE, 20);
        
        addLabel(new GuiLabel(3,"beacon.affect", guiLeft + 10, guiTop + 31));
        addButton(new GuiButtonNop(this, 3,guiLeft + 56, guiTop + 26, 80, 20, new String[]{"faction.friendly", "faction.unfriendly", "spawner.all"} ,job.type));

        addLabel(new GuiLabel(2,"beacon.potency", guiLeft + 140, guiTop + 31));
        addTextField(new GuiTextFieldNop(2,this, guiLeft+220, guiTop + 26, 40, 20, potency + ""));
        getTextField(2).numbersOnly = true;
        getTextField(2).setMinMaxDefault(0, 3, 0);
        
        if(scroll1 == null)
	    {
	      	scroll1 = new GuiCustomScroll(this,0);
	      	scroll1.setSize(175, 154);
	    }
	    scroll1.guiLeft = guiLeft + 4;
	    scroll1.guiTop = guiTop + 58;
	    this.addScroll(scroll1);
	    addLabel(new GuiLabel(11, "beacon.availableEffects", guiLeft + 4, guiTop + 48));
	        
	    if(scroll2 == null){
	       	scroll2 = new GuiCustomScroll(this,1);
	      	scroll2.setSize(175, 154);
	    }
	    scroll2.guiLeft = guiLeft + 235;
	    scroll2.guiTop = guiTop + 58;
	    this.addScroll(scroll2);
	    addLabel(new GuiLabel(12, "beacon.currentEffects", guiLeft + 235, guiTop + 48));
	        
	    List<String> all = new ArrayList<String>();
	    for(String names : potions.keySet())
	    {
	     	if (!job.effects.containsKey(potions.get(names))) all.add(names);
	     	else displays.put(I18n.get(names, new Object[0]) + " " + I18n.get("enchantment.level." + (job.effects.get(potions.get(names)) + 1), new Object[0]), names);
	    }
	    scroll1.setList(all);
	    List<String>applied = new ArrayList<String>(displays.keySet());
	    scroll2.setList(applied);
	
	    addButton(new GuiButtonNop(this, 11, guiLeft + 180, guiTop + 80, 55, 20, ">"));
	    addButton(new GuiButtonNop(this, 12, guiLeft + 180, guiTop + 102, 55, 20, "<"));
	
	    addButton(new GuiButtonNop(this, 13, guiLeft + 180, guiTop + 130, 55, 20, ">>"));
	    addButton(new GuiButtonNop(this, 14, guiLeft + 180, guiTop + 152, 55, 20, "<<"));
        
    }

    @Override
    public void elementClicked(){
    	
    }

	@Override
    public void buttonEvent(GuiButtonNop guibutton){
    	GuiButtonNop button = (GuiButtonNop) guibutton;

    	if (button.id == 3) {
			job.type = (byte) button.getValue();
		}
		if(button.id == 11){
			if(scroll1.hasSelected()){
				job.effects.put(potions.get(scroll1.getSelected()), getTextField(2).getInteger());
				scroll1.clearSelection();
				scroll2.clearSelection();
				init();
			}				
		}
		if(button.id == 12){
			if(scroll2.hasSelected()){
				job.effects.remove(potions.get(displays.remove(scroll2.getSelected())));
				scroll1.clearSelection();
				scroll2.clearSelection();
				init();
			}				
		}
		if(button.id == 13){
			job.effects.clear();
			
			List<String> all = new ArrayList<String>();

			Registry<Effect> r = Registry.MOB_EFFECT;
			for(ResourceLocation rl : r.keySet()){
	     		job.effects.put(r.getId(r.get(rl)), potency);
	    	}
			scroll1.clearSelection();
			scroll2.clearSelection();
			init();
		}
		if(button.id == 14){
			job.effects.clear();
			displays.clear();
			scroll1.clearSelection();
			scroll2.clearSelection();
			init();
		}
    }

    @Override
	public void save() {
    	job.range = getTextField(1).getInteger();
    	potency = getTextField(2).getInteger();
    	job.speed = getTextField(4).getInteger();
		Packets.sendServer(new SPacketNpcJobSave(job.save(new CompoundNBT())));
	}


}
