package noppes.npcs.client.gui.model;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiSliderNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ISliderListener;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiCreationScale extends GuiCreationScreenInterface implements ISliderListener, ICustomScrollListener{
	private GuiCustomScroll scroll;
	private final List<EnumParts> data = new ArrayList<EnumParts>();
	
	private static EnumParts selected = EnumParts.HEAD;
	
	public GuiCreationScale(EntityNPCInterface npc){
		super(npc);
		active = 3;
		xOffset = 140;
	}
	
    @Override
    public void init() {
    	super.init();
    	if(scroll == null){
    		scroll = new GuiCustomScroll(this, 0);
    	}

		ArrayList list = new ArrayList<String>();
		EnumParts[] parts = {EnumParts.HEAD, EnumParts.BODY, EnumParts.ARM_LEFT, 
				EnumParts.ARM_RIGHT, EnumParts.LEG_LEFT, EnumParts.LEG_RIGHT};
		data.clear();
		for(EnumParts part : parts){
			if(part == EnumParts.ARM_RIGHT){
				ModelPartConfig config = playerdata.getPartConfig(EnumParts.ARM_LEFT);
				if(!config.notShared)
					continue;
			}
			if(part == EnumParts.LEG_RIGHT){
				ModelPartConfig config = playerdata.getPartConfig(EnumParts.LEG_LEFT);
				if(!config.notShared)
					continue;
			}
			data.add(part);
			list.add(I18n.get("part." + part.name));
		}
		scroll.setUnsortedList(list);
    	scroll.setSelected(I18n.get("part." + selected.name));
    	scroll.guiLeft = guiLeft;
    	scroll.guiTop = guiTop + 46;
    	scroll.setSize(100, imageHeight - 74);
    	
    	addScroll(scroll);
    	
    	ModelPartConfig config = playerdata.getPartConfig(selected);
		int y = guiTop + 65;
		addLabel(new GuiLabel(10, "scale.width", guiLeft + 102, y + 5, 0xFFFFFF));
		addSlider(new GuiSliderNop(this, 10, guiLeft + 150, y, 100, 20, config.scaleX - 0.5f));
		y += 22;
		addLabel(new GuiLabel(11, "scale.height", guiLeft + 102, y + 5, 0xFFFFFF));
		addSlider(new GuiSliderNop(this, 11, guiLeft + 150, y, 100, 20, config.scaleY - 0.5f));
		y += 22;
		addLabel(new GuiLabel(12, "scale.depth", guiLeft + 102, y + 5, 0xFFFFFF));
		addSlider(new GuiSliderNop(this, 12, guiLeft + 150, y, 100, 20, config.scaleZ - 0.5f));
		
		if(selected == EnumParts.ARM_LEFT || selected == EnumParts.LEG_LEFT){
			y += 22;
			addLabel(new GuiLabel(13, "scale.shared", guiLeft + 102, y + 5, 0xFFFFFF));
			addButton(new GuiButtonNop(this, 13, guiLeft + 150, y, 50, 20, new String[]{"gui.no","gui.yes"}, config.notShared?0:1));
		}
    }

    @Override
    public void buttonEvent(GuiButtonNop btn) {

    	if(btn.id == 13){
    		boolean bo = btn.getValue() == 0;
    		playerdata.getPartConfig(selected).notShared = bo;
    		init();
    	}
    }

	@Override
	public void mouseDragged(GuiSliderNop slider) {
		super.mouseDragged(slider);
		if(slider.id >= 10 && slider.id <= 12){
			int percent = (int) (50 + slider.sliderValue * 100);
			slider.setString(percent + "%");
			ModelPartConfig config = playerdata.getPartConfig(selected);
			
			if(slider.id == 10){
				config.scaleX = slider.sliderValue + 0.5f;
			}
			if(slider.id == 11){
				config.scaleY = slider.sliderValue + 0.5f;
			}
			if(slider.id == 12){
				config.scaleZ = slider.sliderValue + 0.5f;
			}
			updateTransate();
		}
	}
	private void updateTransate(){
		for(EnumParts part : EnumParts.values()){
			ModelPartConfig config = playerdata.getPartConfig(part);
			if(config == null)
				continue;
			if(part == EnumParts.HEAD){
				config.setTranslate(0, playerdata.getBodyY(), 0);
			}
			else if(part == EnumParts.ARM_LEFT){
				ModelPartConfig body = playerdata.getPartConfig(EnumParts.BODY);
				float x = (1 - body.scaleX) * 0.25f + (1 - config.scaleX) * 0.075f;
				float y = playerdata.getBodyY() + (1 - config.scaleY) * -0.1f;
				config.setTranslate(-x, y, 0);
				if(!config.notShared){
					ModelPartConfig arm = playerdata.getPartConfig(EnumParts.ARM_RIGHT);
					arm.copyValues(config);
				}
			}
			else if(part == EnumParts.ARM_RIGHT){
				ModelPartConfig body = playerdata.getPartConfig(EnumParts.BODY);
				float x = (1 - body.scaleX) * 0.25f + (1 - config.scaleX) * 0.075f;
				float y = playerdata.getBodyY() + (1 - config.scaleY) * -0.1f;
				config.setTranslate(x, y, 0);
			}
			else if(part == EnumParts.LEG_LEFT){
				config.setTranslate((config.scaleX) * 0.125f - 0.113f, playerdata.getLegsY(), 0);
				if(!config.notShared){
					ModelPartConfig leg = playerdata.getPartConfig(EnumParts.LEG_RIGHT);
					leg.copyValues(config);
				}
			}
			else if(part == EnumParts.LEG_RIGHT){
				config.setTranslate((1 - config.scaleX) * 0.125f, playerdata.getLegsY(), 0);
			}
			else if(part == EnumParts.BODY){
				config.setTranslate(0, playerdata.getBodyY(), 0);
			}
		}
	}

	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
		if(scroll.hasSelected()){
			selected = data.get(scroll.getSelectedIndex());
			init();
		}
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
