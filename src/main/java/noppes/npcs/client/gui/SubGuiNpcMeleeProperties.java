package noppes.npcs.client.gui;

import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.entity.data.DataMelee;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import java.util.ArrayList;
import java.util.List;

public class SubGuiNpcMeleeProperties extends GuiBasic implements ITextfieldListener {
	private DataMelee stats;
	private final static String[] potionNames;

	static {
		List<String> list = new ArrayList<String>();
		list.add("gui.none");
		for(int i = 1; i < 33; i++){
			list.add(PotionEffectType.getMCType(i).getDescriptionId());
		}
		list.add("block.minecraft.fire");
		potionNames = list.toArray(new String[list.size()]);
	}


    public SubGuiNpcMeleeProperties(DataMelee stats){
    	this.stats = stats;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
    }

	@Override
    public void init(){
        super.init();
        addLabel(new GuiLabel(1,"stats.meleestrength", guiLeft + 5, guiTop + 15));
        addTextField(new GuiTextFieldNop(1,this,  guiLeft + 85, guiTop + 10, 50, 18, stats.getStrength()+""));
        getTextField(1).numbersOnly = true;
        getTextField(1).setMinMaxDefault(0, Integer.MAX_VALUE, 5);
        addLabel(new GuiLabel(2,"stats.meleerange", guiLeft + 5, guiTop + 45));
        addTextField(new GuiTextFieldNop(2,this,  guiLeft + 85, guiTop + 40, 50, 18, stats.getRange()+""));
        getTextField(2).numbersOnly = true;
        getTextField(2).setMinMaxDefault(1, 30, 2);
        addLabel(new GuiLabel(3,"stats.meleespeed", guiLeft + 5, guiTop + 75));
        addTextField(new GuiTextFieldNop(3,this,  guiLeft + 85, guiTop + 70, 50, 18, stats.getDelay()+""));
        getTextField(3).numbersOnly = true;
        getTextField(3).setMinMaxDefault(1, 1000, 20);

        addLabel(new GuiLabel(4,"enchantment.minecraft.knockback", guiLeft + 5, guiTop + 105));
		addTextField(new GuiTextFieldNop(4,this,  guiLeft + 85, guiTop + 100, 50, 18, stats.getKnockback() + ""));
		getTextField(4).numbersOnly = true;
		getTextField(4).setMinMaxDefault(0, 4, 0);
        addLabel(new GuiLabel(5,"stats.meleeeffect", guiLeft + 5, guiTop + 135));

		int effect = stats.getEffectType();
		if(effect == PotionEffectType.FIRE){
			effect = potionNames.length - 1;
		}
    	addButton(new GuiButtonBiDirectional(this,5,guiLeft + 85, guiTop + 130, 100, 20, potionNames, effect));
    	if(stats.getEffectType() != PotionEffectType.NONE) {
    		addLabel(new GuiLabel(6,"gui.time", guiLeft + 5, guiTop + 165));
    		addTextField(new GuiTextFieldNop(6,this,  guiLeft + 85, guiTop + 160, 50, 18, stats.getEffectTime() + ""));
    		getTextField(6).numbersOnly = true;
    		getTextField(6).setMinMaxDefault(1, 99999, 5);
    		if(stats.getEffectType() != PotionEffectType.FIRE) {
    			addLabel(new GuiLabel(7,"stats.amplify", guiLeft + 5, guiTop + 195));
    			addButton(new GuiButtonBiDirectional(this,7,guiLeft + 85, guiTop + 190, 52, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"} ,stats.getEffectStrength()));
    		}
    	}
    	addButton(new GuiButtonNop(this, 66, guiLeft + 164, guiTop + 192, 90, 20, "gui.done"));
    }

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 1){
			stats.setStrength(textfield.getInteger());
		}
		else if(textfield.id == 2){
			stats.setRange(textfield.getInteger());
		}
		else if(textfield.id == 3){
			stats.setDelay(textfield.getInteger());
		}
		else if(textfield.id == 4){
			stats.setKnockback(textfield.getInteger());
		}
		else if(textfield.id == 6){
			stats.setEffect(stats.getEffectType(), stats.getEffectStrength(), textfield.getInteger());
		}
	}
    
	@Override
	public void buttonEvent(GuiButtonNop button){
		if(button.id == 5){
			int effect = button.getValue();
			if(effect == potionNames.length - 1){
				effect = PotionEffectType.FIRE;
			}
			stats.setEffect(effect, stats.getEffectStrength(), stats.getEffectTime());
			init();
        }
		if(button.id == 7){
			stats.setEffect(stats.getEffectType(), button.getValue(), stats.getEffectTime());
        }
        if(button.id == 66){
        	close();
        }
    }

}
