package noppes.npcs.client.gui.model;

import net.minecraft.client.resources.I18n;
import noppes.npcs.ModelEyeData;
import noppes.npcs.ModelPartData;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiCreationParts extends GuiCreationScreenInterface implements ITextfieldListener, ICustomScrollListener {
	private GuiCustomScroll scroll;
	
	private GuiPart[] parts = {
			new GuiPart(EnumParts.EARS).setTypes(new String[]{"gui.none", "gui.normal", "ears.bunny"}), 
			new GuiPartHorns(), 
			new GuiPartHair(), 
			new GuiPart(EnumParts.MOHAWK).setTypes(new String[]{"gui.none", "1", "2"}).noPlayerOptions(), 
			new GuiPartSnout(), 
			new GuiPartBeard(), 
			new GuiPart(EnumParts.FIN).setTypes(new String[]{"gui.none", "fin.shark", "fin.reptile"}), 
			new GuiPart(EnumParts.BREASTS).setTypes(new String[]{"gui.none", "1", "2", "3"}).noPlayerOptions(), 
			new GuiPartWings(), 
			new GuiPartClaws(), 
			new GuiPart(EnumParts.SKIRT).setTypes(new String[]{"gui.none", "gui.normal"}), 
			new GuiPartLegs(), 
			new GuiPartTail(), 
			new GuiPartEyes(),
			new GuiPartParticles(), 
		};

	private static int selected = 0;

	public GuiCreationParts(EntityNPCInterface npc){
		super(npc);
		active = 2;
		closeOnEsc = false;

        Arrays.sort(parts, (o1, o2) -> {
            String s1 = I18n.get("part." + o1.part.name);
            String s2 = I18n.get("part." + o2.part.name);
            return s1.compareToIgnoreCase(s2);
		});
		
	}
	
    @Override
    public void init() {
    	super.init();
    	if(entity != null){
    		openGui(new GuiCreationExtra(npc));
    		return;
    	}
    	
    	if(scroll == null){
    		List<String> list = new ArrayList<String>();
    		for(GuiPart part : parts)
    			list.add(I18n.get("part." + part.part.name));
    		scroll = new GuiCustomScroll(this, 0);
    		scroll.setUnsortedList(list);
    	}
    	scroll.guiLeft = guiLeft;
    	scroll.guiTop = guiTop + 46;
    	scroll.setSize(100, imageHeight - 74);
    	
    	addScroll(scroll);
    	

    	if(parts[selected] != null){
    		scroll.setSelected(I18n.get("part." + parts[selected].part.name));
    		parts[selected].init();
    	}
    }

    @Override
    public void buttonEvent(GuiButtonNop btn) {

    	if(parts[selected] != null){
    		parts[selected].buttonEvent(btn);
    	}
    }
    
	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 23){
			
		}
	}

	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
		if(scroll.hasSelected()){
			selected = scroll.getSelectedIndex();
			init();
		}
	}
	class GuiPart{
		EnumParts part;
		private int paterns = 0;
		protected String[] types = {"gui.none"};
		protected ModelPartData data;
		protected boolean hasPlayerOption = true;
		protected boolean noPlayerTypes = false;
		protected boolean canBeDeleted = true;
		
		public GuiPart(EnumParts part){
			this.part = part;
			data = playerdata.getPartData(part);
		}
		
		public int init(){
			data = playerdata.getPartData(part);
			int y = guiTop + 50;
			if(data == null || !data.playerTexture || !noPlayerTypes){
				GuiCreationParts.this.addLabel(new GuiLabel(20, "gui.type", guiLeft + 102, y + 5, 0xFFFFFF));
				GuiCreationParts.this.addButton(new GuiButtonBiDirectional(GuiCreationParts.this,20, guiLeft + 145, y, 100, 20, types, data == null?0:data.type + 1));
				y += 25;
			}
			if(data != null && hasPlayerOption){
				GuiCreationParts.this.addLabel(new GuiLabel(21, "gui.playerskin", guiLeft + 102, y + 5, 0xFFFFFF));
				GuiCreationParts.this.addButton(new GuiButtonYesNo(GuiCreationParts.this, 21, guiLeft + 170, y, data.playerTexture));
				 y += 25;
			}
			if(data != null && !data.playerTexture){
				GuiCreationParts.this.addLabel(new GuiLabel(23, "gui.color", guiLeft + 102, y + 5, 0xFFFFFF));
				GuiCreationParts.this.addButton(new GuiColorButton(GuiCreationParts.this, 23, guiLeft + 170, y, data.color));
				 y += 25;
			}
			return y;
		}
		
	    public void buttonEvent(GuiButtonNop btn) {
	    	if(btn.id == 20){
	    		int i = ((GuiButtonNop)btn).getValue();
	    		if(i == 0 && canBeDeleted)
	    			playerdata.removePart(part);
	    		else{
	    			data = playerdata.getOrCreatePart(part);
	    			data.pattern = 0;
					data.color = 0xFFFFFF;
					data.playerTexture = false;
	    			data.setType(i - 1);
	    		}
	    		GuiCreationParts.this.init();
	    	}
	    	if(btn.id == 22){
    			data.pattern = (byte) ((GuiButtonNop)btn).getValue();
	    	}
	    	if(btn.id == 21){
	    		data.playerTexture = ((GuiButtonYesNo)btn).getBoolean();
	    		GuiCreationParts.this.init();
	    	}
	    	if(btn.id == 23){
                setSubGui(new GuiModelColor(GuiCreationParts.this, data.color, color -> data.color = color));
	    	}
	    }
	    public GuiPart noPlayerOptions(){
	    	hasPlayerOption = false;
	    	return this;
	    }
	    
	    public GuiPart noPlayerTypes(){
	    	noPlayerTypes = true;
	    	return this;
	    }
		
		public GuiPart setTypes(String[] types){
			this.types = types;
			return this;
		}
	}
	class GuiPartTail extends GuiPart{
		public GuiPartTail() {
			super(EnumParts.TAIL);
			types = new String[]{"gui.none", "part.tail", "tail.dragon", 
					"tail.horse", "tail.squirrel", "tail.fin", "tail.rodent", "tail.bird", "tail.fox"};
		}

		@Override
		public int init(){
			data = playerdata.getPartData(part);
			hasPlayerOption = data != null && (data.type == 0 || data.type == 1 || data.type == 6 || data.type == 7);
			int y = super.init();
			if(data != null && data.type == 0){
				GuiCreationParts.this.addLabel(new GuiLabel(22, "gui.pattern", guiLeft + 102, y + 5, 0xFFFFFF));
				GuiCreationParts.this.addButton(new GuiButtonBiDirectional(GuiCreationParts.this,22, guiLeft + 145, y, 100, 20, new String[]{"1","2"}, data.pattern));
			}
			return y;
		}
	}
	
	class GuiPartHorns extends GuiPart{
		public GuiPartHorns() {
			super(EnumParts.HORNS);
			types = new String[]{"gui.none", "horns.bull", "horns.antlers", "horns.antenna"};
		}

		@Override
		public int init(){
			int y = super.init();
			if(data != null && data.type == 2){
				GuiCreationParts.this.addLabel(new GuiLabel(22, "gui.pattern", guiLeft + 102, y + 5, 0xFFFFFF));
				GuiCreationParts.this.addButton(new GuiButtonBiDirectional(GuiCreationParts.this,22, guiLeft + 145, y, 100, 20, new String[]{"1","2"}, data.pattern));
			}
			return y;
		}
	}
	class GuiPartHair extends GuiPart{
		public GuiPartHair() {
			super(EnumParts.HAIR);
			types = new String[]{"gui.none", "1", "2", "3", "4"};
			noPlayerTypes();
		}
	}
	class GuiPartSnout extends GuiPart{
		public GuiPartSnout() {
			super(EnumParts.SNOUT);
			types = new String[]{"gui.none", "snout.small", "snout.medium", "snout.large", "snout.bunny", "snout.beak"};
		}
	}
	class GuiPartBeard extends GuiPart{
		public GuiPartBeard() {
			super(EnumParts.BEARD);
			types = new String[]{"gui.none", "1", "2", "3", "4"};
			noPlayerTypes();
		}
	}
	class GuiPartEyes extends GuiPart{
	    private ModelEyeData eyes;
		public GuiPartEyes() {
			super(EnumParts.EYES);
			types = new String[]{"gui.none", "1", "2"};
			noPlayerOptions();
			canBeDeleted = false;
			eyes = (ModelEyeData)this.data;
		}

		@Override
		public int init(){
			int y = super.init();
			if(data != null && eyes.isEnabled()){
				GuiCreationParts.this.addButton(new GuiButtonBiDirectional(GuiCreationParts.this,22, guiLeft + 145, y, 100, 20, new String[]{"gui.both", "gui.left", "gui.right"}, data.pattern));
				GuiCreationParts.this.addLabel(new GuiLabel(22, "gui.draw", guiLeft + 102, y + 5, 0xFFFFFF));
								
				GuiCreationParts.this.addButton(new GuiButtonBiDirectional(GuiCreationParts.this,37, guiLeft + 145, y += 25, 100, 20, new String[]{I18n.get("gui.up") + "x2", "gui.up", "gui.normal", "gui.up"}, eyes.eyePos + 1));
				GuiCreationParts.this.addLabel(new GuiLabel(37, "gui.position", guiLeft + 102, y + 5, 0xFFFFFF));
				
				GuiCreationParts.this.addButton(new GuiButtonYesNo(GuiCreationParts.this, 34, guiLeft + 145, y += 25, eyes.glint));
				GuiCreationParts.this.addLabel(new GuiLabel(34, "eye.glint", guiLeft + 102, y + 5, 0xFFFFFF));
				
				GuiCreationParts.this.addButton(new GuiColorButton(GuiCreationParts.this, 35, guiLeft + 170, y += 25, eyes.browColor));
				GuiCreationParts.this.addLabel(new GuiLabel(35, "eye.brow", guiLeft + 102, y + 5, 0xFFFFFF));				
				GuiCreationParts.this.addButton(new GuiButtonBiDirectional(GuiCreationParts.this,38, guiLeft + 225, y, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8"}, eyes.browThickness));
				
				GuiCreationParts.this.addButton(new GuiColorButton(GuiCreationParts.this, 36, guiLeft + 170, y += 25, eyes.skinColor));
				GuiCreationParts.this.addLabel(new GuiLabel(36, "eye.lid", guiLeft + 102, y + 5, 0xFFFFFF));
			}
			return y;
		}

		@Override
	    public void buttonEvent(GuiButtonNop btn) {
	    	if(btn.id == 34){
	    		eyes.glint = ((GuiButtonYesNo)btn).getBoolean();
	    	}
	    	if(btn.id == 35){
                setSubGui(new GuiModelColor(GuiCreationParts.this, eyes.browColor, color -> eyes.browColor = color));
	    	}
	    	if(btn.id == 36){
                setSubGui(new GuiModelColor(GuiCreationParts.this, eyes.skinColor, color -> eyes.skinColor = color));
	    	}
	    	if(btn.id == 37){
	    		eyes.eyePos = ((GuiButtonBiDirectional)btn).getValue() - 1;
	    	}
	    	if(btn.id == 38){
	    		eyes.browThickness = ((GuiButtonBiDirectional)btn).getValue();
	    	}
			super.buttonEvent(btn);

		}
	}
	class GuiPartWings extends GuiPart{
		public GuiPartWings() {
			super(EnumParts.WINGS);
			setTypes(new String[]{"gui.none", "1", "2", "3"});
		}
		@Override
		public int init(){
			int y = super.init();
			if(data == null)
				return y;
			//GuiCreationParts.this.addLabel(new GuiLabel(24, I18n.get("part.wings") + "/" + I18n.get("item.customnpcs.elytra.name"), guiLeft + 102, y + 5, 0xFFFFFF));
			//GuiCreationParts.this.addButton(new GuiButtonBiDirectional(this,34, guiLeft + 185, y, 100, 20, new String[]{"gui.both","part.wings","item.customnpcs.elytra.name"}, data.pattern));
			return y;
		}

		@Override
	    public void buttonEvent(GuiButtonNop btn) {
	    	if(btn.id == 34){
	    		//playerdata.wingMode = ((GuiButtonBiDirectional)btn).getValue();
	    	}
			super.buttonEvent(btn);
		}
	}
	class GuiPartClaws extends GuiPart{
		public GuiPartClaws() {
			super(EnumParts.CLAWS);
			types = new String[]{"gui.none", "gui.show"};
		}
		
		@Override
		public int init(){
			int y = super.init();
			if(data == null)
				return y;
			GuiCreationParts.this.addLabel(new GuiLabel(22, "gui.pattern", guiLeft + 102, y + 5, 0xFFFFFF));
			GuiCreationParts.this.addButton(new GuiButtonBiDirectional(GuiCreationParts.this,22, guiLeft + 145, y, 100, 20, new String[]{"gui.both","gui.left","gui.right"}, data.pattern));
			return y;
		}
	}
	class GuiPartParticles extends GuiPart{
		public GuiPartParticles() {
			super(EnumParts.PARTICLES);
			types = new String[]{"gui.none", "1", "2"};
		}
		
		@Override
		public int init(){
			int y = super.init();
			if(data == null)
				return y;
			return y;
		}
	}
	class GuiPartLegs extends GuiPart{
		public GuiPartLegs() {
			super(EnumParts.LEGS);
			types = new String[]{"gui.none", "gui.normal", "legs.naga", "legs.spider", 
					"legs.horse", "legs.mermaid", "legs.digitigrade"};
			canBeDeleted = false;
		}
		@Override
		public int init(){
			hasPlayerOption = data.type == 1 || data.type == 5;

			return super.init();
		}

		@Override
	    public void buttonEvent(GuiButtonNop btn) {
			if(btn.id == 20){
				int i = ((GuiButtonNop)btn).getValue();
				if(i <= 1)
					data.playerTexture = true;
				else
					data.playerTexture = false;
			}
			if(btn.id == 22){
				data.pattern = (byte) ((GuiButtonNop)btn).getValue();
			}
			super.buttonEvent(btn);
	    }
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
