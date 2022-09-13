package noppes.npcs.client.gui;

import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.entity.data.DataRanged;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import java.util.ArrayList;
import java.util.List;

public class SubGuiNpcProjectiles extends GuiBasic implements ITextfieldListener {
	private final DataRanged stats;
	private final static String[] potionNames;
	private final String[] trailNames = new String[]{"gui.none", "Smoke", "Portal", "Redstone", "Lightning", "LargeSmoke", "Magic", "Enchant"};

	static {
		List<String> list = new ArrayList<String>();
		list.add("gui.none");
		for(int i = 1; i < 33; i++){
			list.add(PotionEffectType.getMCType(i).getDescriptionId());
		}
		list.add("block.minecraft.fire");
		potionNames = list.toArray(new String[list.size()]);
	}

    public SubGuiNpcProjectiles(DataRanged stats){
    	this.stats = stats;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

    @Override
    public void init(){
        super.init();
        addLabel(new GuiLabel(1,"effect.minecraft.strength", guiLeft + 5, guiTop + 15));
        addTextField(new GuiTextFieldNop(1,this,  guiLeft + 45, guiTop + 10, 50, 18, stats.getStrength()+""));
        getTextField(1).numbersOnly = true;
        getTextField(1).setMinMaxDefault(0, Integer.MAX_VALUE, 5);
        addLabel(new GuiLabel(2,"enchantment.minecraft.knockback", guiLeft + 110, guiTop + 15));
        addTextField(new GuiTextFieldNop(2,this,  guiLeft + 150, guiTop + 10, 50, 18, stats.getKnockback()+""));
        getTextField(2).numbersOnly = true;
        getTextField(2).setMinMaxDefault(0, 3, 0);
        addLabel(new GuiLabel(3,"stats.size", guiLeft + 5, guiTop + 45));
        addTextField(new GuiTextFieldNop(3,this,  guiLeft + 45, guiTop + 40, 50, 18, stats.getSize()+""));
        getTextField(3).numbersOnly = true;
        getTextField(3).setMinMaxDefault(5, 20, 10);
        addLabel(new GuiLabel(4,"stats.speed", guiLeft + 5, guiTop + 75));
        addTextField(new GuiTextFieldNop(4,this,  guiLeft + 45, guiTop + 70, 50, 18, stats.getSpeed()+""));
        getTextField(4).numbersOnly = true;
        getTextField(4).setMinMaxDefault(1, 50, 10);
        
        addLabel(new GuiLabel(5,"stats.hasgravity", guiLeft + 5, guiTop + 105));
    	addButton(new GuiButtonNop(this, 0, guiLeft + 60, guiTop + 100, 60, 20, new String[]{"gui.no", "gui.yes"} ,stats.getHasGravity() ? 1:0));
    	if(!stats.getHasGravity()) {
        	addButton(new GuiButtonNop(this, 1, guiLeft + 140, guiTop + 100, 60, 20, new String[]{"gui.constant", "gui.accelerate"} ,stats.getAccelerate() ? 1:0));
    	}
    	addLabel(new GuiLabel(6,"stats.explosive", guiLeft + 5, guiTop + 135));
        addButton(new GuiButtonNop(this, 3, guiLeft + 60, guiTop + 130, 60, 20, new String[]{"gui.none", "gui.small", "gui.medium", "gui.large"} ,stats.getExplodeSize() % 4));

		int effect = stats.getEffectType();
		if(effect == PotionEffectType.FIRE){
			effect = potionNames.length - 1;
		}
    	addLabel(new GuiLabel(7,"stats.rangedeffect", guiLeft + 5, guiTop + 165));
    	addButton(new GuiButtonBiDirectional(this, 4,guiLeft + 40, guiTop + 160, 100, 20, potionNames, effect));
    	if(stats.getEffectType() != PotionEffectType.NONE) {
    		addTextField(new GuiTextFieldNop(5,this,  guiLeft + 140, guiTop + 160, 60, 18, stats.getEffectTime() + ""));
    		getTextField(5).numbersOnly = true;
    		getTextField(5).setMinMaxDefault(1, 99999, 5);
    		if(stats.getEffectType() != PotionEffectType.FIRE) {
    			addButton(new GuiButtonNop(this, 10, guiLeft + 210, guiTop + 160, 40, 20, new String[]{"stats.regular", "stats.amplified"} ,stats.getEffectStrength() % 2));
    		}
    	}
    	
    	addLabel(new GuiLabel(8,"stats.trail", guiLeft + 5, guiTop + 195));
    	addButton(new GuiButtonNop(this, 5, guiLeft + 60, guiTop + 190, 60, 20, trailNames, stats.getParticle()));
    	
    	addButton(new GuiButtonNop(this, 7, guiLeft + 220, guiTop + 10, 30, 20, new String[]{"2D", "3D"} ,stats.getRender3D() ? 1:0));
    	if (stats.getRender3D()) {
    		addLabel(new GuiLabel(10,"stats.spin", guiLeft + 160, guiTop + 45));
    		addButton(new GuiButtonNop(this, 8, guiLeft + 220, guiTop + 40, 30, 20, new String[]{"gui.no", "gui.yes"} ,stats.getSpins() ? 1:0));
    		addLabel(new GuiLabel(11,"stats.stick", guiLeft + 160, guiTop + 75));
    		addButton(new GuiButtonNop(this, 9, guiLeft + 220, guiTop + 70, 30, 20, new String[]{"gui.no", "gui.yes"} ,stats.getSticks() ? 1:0));
    	}
    	addButton(new GuiButtonNop(this, 6, guiLeft + 140, guiTop + 190, 60, 20, new String[]{"stats.noglow", "stats.glows"} ,stats.getGlows() ? 1:0));
    	addButton(new GuiButtonNop(this, 66, guiLeft + 210, guiTop + 190, 40, 20, "gui.done"));
    }

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 1){
			stats.setStrength(textfield.getInteger());
		}
		else if(textfield.id == 2){
			stats.setKnockback(textfield.getInteger());
		}
		else if(textfield.id == 3){
			stats.setSize(textfield.getInteger());
		}
		else if(textfield.id == 4){
			stats.setSpeed(textfield.getInteger());
		}
		else if(textfield.id == 5){
			stats.setEffect(stats.getEffectType(), stats.getEffectStrength(), textfield.getInteger());
		}
	}
    
	@Override
	public void buttonEvent(GuiButtonNop guibutton){
		GuiButtonNop button = guibutton;
		if(button.id == 0){
			stats.setHasGravity(button.getValue() == 1);
			init();
        }
		if(button.id == 1){
			stats.setAccelerate(button.getValue() == 1);
        }
		if(button.id == 3){
			stats.setExplodeSize(button.getValue());
        }
		if(button.id == 4){
			int effect = button.getValue();
			if(effect == potionNames.length - 1){
				effect = PotionEffectType.FIRE;
			}
			stats.setEffect(effect, stats.getEffectStrength(), stats.getEffectTime());
			init();
        }
		if(button.id == 5){
			stats.setParticle(button.getValue());
        }
		if(button.id == 6){
			stats.setGlows(button.getValue() == 1);
        }
		if(button.id == 7){
			stats.setRender3D(button.getValue() == 1);
			init();
        }
		if(button.id == 8){
			stats.setSpins(button.getValue() == 1);
        }
		if(button.id == 9){
			stats.setSticks(button.getValue() == 1);
        }
		if(button.id == 10){
			stats.setEffect(stats.getEffectType(), button.getValue(), stats.getEffectTime());
        }
        if(button.id == 66){
        	close();
        }
    }
}
