package noppes.npcs.client.gui.roles;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.roles.JobPuppet;
import noppes.npcs.roles.JobPuppet.PartConfig;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ISliderListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GuiNpcPuppet extends GuiNPCInterface implements ISliderListener, ICustomScrollListener {

	private Screen parent;
	private JobPuppet job;
	private String selectedName;
	private boolean isStart = true;
	
	public HashMap<String, PartConfig> data = new HashMap<String, PartConfig>();

	private GuiCustomScroll scroll;
	
	public GuiNpcPuppet(Screen parent, EntityCustomNpc npc){
		super(npc);
		this.parent = parent;
		imageHeight = 230;
		imageWidth = 400;
		job = (JobPuppet) npc.job;
	}

    @Override
    public void init() {
    	super.init();

		int y = guiTop;
		addButton(new GuiButtonNop(this, 30, guiLeft + 110, y += 14, 60, 20, new String[]{"gui.yes", "gui.no"}, job.whileStanding?0:1));
		addLabel(new GuiLabel(30, "puppet.standing", guiLeft + 10, y + 5, 0xFFFFFF));
		addButton(new GuiButtonNop(this, 31, guiLeft + 110, y += 22, 60, 20, new String[]{"gui.yes", "gui.no"}, job.whileMoving?0:1));
		addLabel(new GuiLabel(31, "puppet.walking", guiLeft + 10, y + 5, 0xFFFFFF));
		addButton(new GuiButtonNop(this, 32, guiLeft + 110, y += 22, 60, 20, new String[]{"gui.yes", "gui.no"}, job.whileAttacking?0:1));
		addLabel(new GuiLabel(32, "puppet.attacking", guiLeft + 10, y + 5, 0xFFFFFF));
		addButton(new GuiButtonNop(this, 33, guiLeft + 110, y += 22, 60, 20, new String[]{"gui.yes", "gui.no"}, job.animate?0:1));
		addLabel(new GuiLabel(33, "puppet.animation", guiLeft + 10, y + 5, 0xFFFFFF));
		
		if(job.animate){
			addButton(new GuiButtonBiDirectional(this,34, guiLeft + 240, y, 60, 20, new String[]{"1", "2", "3", "4", "5", "6", "7", "8"}, job.animationSpeed));
			addLabel(new GuiLabel(34, "stats.speed", guiLeft + 190, y + 5, 0xFFFFFF));
		}
		
		y += 34;

		HashMap<String, PartConfig> data = new HashMap<String, PartConfig>();
		if(isStart){
			data.put("model.head", job.head);
			data.put("model.body", job.body);
			data.put("model.larm", job.larm);
			data.put("model.rarm", job.rarm);
			data.put("model.lleg", job.lleg);
			data.put("model.rleg", job.rleg);
		}
		else{
			data.put("model.head", job.head2);
			data.put("model.body", job.body2);
			data.put("model.larm", job.larm2);
			data.put("model.rarm", job.rarm2);
			data.put("model.lleg", job.lleg2);
			data.put("model.rleg", job.rleg2);
		}
		
		this.data = data;
    	
    	if(scroll == null){
    		scroll = new GuiCustomScroll(this, 0);
    	}
		scroll.setList(new ArrayList<String>(data.keySet()));
    	scroll.guiLeft = guiLeft + 10;
    	scroll.guiTop = y;
    	scroll.setSize(80, 100);    	
    	addScroll(scroll);
    	
		if(selectedName != null){
			scroll.setSelected(selectedName);
			drawSlider(y, data.get(selectedName));
		}
		
		addButton(new GuiButtonNop(this, 66, guiLeft + imageWidth - 22, guiTop,  20, 20, "X"));
		
		if(job.animate){
			addButton(new GuiButtonNop(this, 67, guiLeft + 10, y + 110,  70, 20, "gui.start"));
			addButton(new GuiButtonNop(this, 68, guiLeft + 90, y + 110,  70, 20, "gui.end"));

			getButton(67).active = !isStart;
			getButton(68).active = isStart;
		}
    }
    
    private void drawSlider(int y, PartConfig config){
		addButton(new GuiButtonNop(this, 29, guiLeft + 140, y , 80, 20, new String[]{"gui.enabled","gui.disabled"}, config.disabled?1:0));
		y += 22;
		addLabel(new GuiLabel(10, "X", guiLeft + 100, y + 5, 0xFFFFFF));
		addSlider(new GuiSliderNop(this, 10, guiLeft + 120, y, (config.rotationX + 1) / 2));
		y += 22;
		addLabel(new GuiLabel(11, "Y", guiLeft + 100, y + 5, 0xFFFFFF));
		addSlider(new GuiSliderNop(this, 11, guiLeft + 120, y, (config.rotationY + 1) / 2));
		y += 22;
		addLabel(new GuiLabel(12, "Z", guiLeft + 100, y + 5, 0xFFFFFF));
		addSlider(new GuiSliderNop(this, 12, guiLeft + 120, y, (config.rotationZ + 1) / 2));
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
		drawNpc(320, 200);
    }

    @Override
    public void buttonEvent(GuiButtonNop btn) {

    	if(!(btn instanceof GuiButtonNop))
    		return;
    	
    	GuiButtonNop button = (GuiButtonNop) btn;
    	if(btn.id == 29){
    		data.get(selectedName).disabled = button.getValue() == 1;
    	}
    	if(btn.id == 30){
    		job.whileStanding = button.getValue() == 0;
    	}
    	if(btn.id == 31){
    		job.whileMoving = button.getValue() == 0;
    	}
    	if(btn.id == 32){
    		job.whileAttacking = button.getValue() == 0;
    	}
    	if(btn.id == 33){
    		job.animate = button.getValue() == 0;
    		isStart = true;
    		init();
    	}
    	if(btn.id == 34){
    		job.animationSpeed = button.getValue();
    	}
    	if(btn.id == 66){
    		close();
    	}
    	if(btn.id == 67){
    		isStart = true;
    		init();
    	}
    	if(btn.id == 68){
    		isStart = false;
    		init();
    	}
    }

    @Override
    public void close(){
        this.minecraft.setScreen(parent);
		Packets.sendServer(new SPacketNpcJobSave(job.save(new CompoundNBT())));
    }

	@Override
	public void mouseDragged(GuiSliderNop slider) {
		int percent = (int) ((slider.sliderValue) * 360);
		slider.setString(percent + "%");
		PartConfig part = data.get(selectedName);
		if(slider.id == 10)
			part.rotationX = (slider.sliderValue - 0.5f) * 2;
		if(slider.id == 11)
			part.rotationY = (slider.sliderValue - 0.5f) * 2;
		if(slider.id == 12)
			part.rotationZ = (slider.sliderValue - 0.5f) * 2;
		npc.refreshDimensions();
	}

	@Override
	public void mousePressed(GuiSliderNop slider) {
		
	}

	@Override
	public void mouseReleased(GuiSliderNop slider) {
	}

	@Override
	public void save() {
		
	}

	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll guiCustomScroll) {
		selectedName = guiCustomScroll.getSelected();
		init();
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
