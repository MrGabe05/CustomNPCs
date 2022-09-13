package noppes.npcs.client.gui.model;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.NPCRendererHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityFakeLiving;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonBiDirectional;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

import java.lang.reflect.Method;
import java.util.*;

public class GuiCreationExtra extends GuiCreationScreenInterface implements ICustomScrollListener {

	private final String[] ignoredTags = {"CanBreakDoors", "Bred", "PlayerCreated", "HasReproduced"};
	private final String[] booleanTags = {};
	
	private GuiCustomScroll scroll;
	private Map<String, GuiType> data = new HashMap<String, GuiType>();
	
	private GuiType selected;
	
	public GuiCreationExtra(EntityNPCInterface npc){
		super(npc);
		active = 2;
	}

    @Override
    public void init() {
    	super.init();
    	if(entity == null){
    		openGui(new GuiCreationParts(npc));
    		return;
    	}
		
    	if(scroll == null){
    		data = getData(entity);
    		scroll = new GuiCustomScroll(this, 0);
    		List<String> list = new ArrayList<String>(data.keySet()); 
    		scroll.setList(list);
    		if(list.isEmpty())
    			return;
    		scroll.setSelected(list.get(0));
    	}
    	selected = data.get(scroll.getSelected());
    	if(selected == null)
    		return;
    	scroll.guiLeft = guiLeft;
    	scroll.guiTop = guiTop + 46;
    	scroll.setSize(100, imageHeight - 74);
    	addScroll(scroll);
    	selected.init();
    }
    
    public Map<String, GuiType> getData(LivingEntity entity){
    	Map<String, GuiType> data = new HashMap<String, GuiType>();
		CompoundNBT compound = getExtras(entity);
		Set<String> keys = compound.getAllKeys();
		for(String name : keys){
			if(isIgnored(name))
				continue;
			INBT base = compound.get(name);
			if(name.equals("Age")){
				data.put("Child", new GuiTypeBoolean("Child", entity.isBaby()));
			}
			else if(name.equals("Color") && base.getId() == 1){
				data.put("Color", new GuiTypeByte("Color", compound.getByte("Color")));				
			}
			else if(base.getId() == 1){
				byte b = ((ByteNBT)base).getAsByte();
				if(b != 0 && b != 1)
					continue;
				if(playerdata.extra.contains(name))
					b = playerdata.extra.getByte(name);
				data.put(name, new GuiTypeBoolean(name, b == 1));
			}
		}
		if(PixelmonHelper.isPixelmon(entity)){
			data.put("Model", new GuiTypePixelmon("Model"));
		}
		if(entity.getEncodeId().equals("tgvstyle.Dog")){
			data.put("Breed", new GuiTypeDoggyStyle("Breed"));
		}
		return data;
    }

	private boolean isIgnored(String tag){
		for(String s : ignoredTags)
			if(s.equals(tag))
				return true;
		return false;
	}
	
	private void updateTexture(){
		LivingEntity entity = playerdata.getEntity(npc);
		EntityRenderer render = minecraft.getEntityRenderDispatcher().getRenderer(entity);
		npc.display.setSkinTexture(NPCRendererHelper.getTexture(render,entity));
	}

	private CompoundNBT getExtras(LivingEntity entity) {
		CompoundNBT fake = new CompoundNBT();
		new EntityFakeLiving(entity.level).addAdditionalSaveData(fake);
		
		CompoundNBT compound = new CompoundNBT();
		try {
			entity.addAdditionalSaveData(compound);
		}
		catch(Throwable e) {
			
		}
		Set<String> keys = fake.getAllKeys();
		for(String name : keys)
			compound.remove(name);
		
		return compound;
	}
	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
		if(scroll.id == 0)
			init();
		else if(selected != null){
			selected.scrollClicked(i, j, k, scroll);
		}
	}

    @Override
    public void buttonEvent(GuiButtonNop btn) {
    	if(selected != null)
    		selected.buttonEvent(btn);
    }
    
    abstract class GuiType{
    	public String name;
    	public GuiType(String name){
    		this.name = name;
    	}
    	public void init(){}

		public void buttonEvent(GuiButtonNop button){}

		public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll){}
	}

    class GuiTypeBoolean extends GuiType{
    	private boolean bo;
    	public GuiTypeBoolean(String name, boolean bo){
    		super(name);
    		this.bo = bo;
    	}
		@Override
		public void init() {
			addButton(new GuiButtonYesNo(GuiCreationExtra.this, 11, guiLeft + 120, guiTop + 50, 60, 20, bo));
		}
		@Override
		public void buttonEvent(GuiButtonNop button) {
			if(button.id != 11)
				return;
			bo = ((GuiButtonYesNo)button).getBoolean();
			if(name.equals("Child")){
	    		playerdata.extra.putInt("Age",bo?-24000:0);
	    		playerdata.clearEntity();
			}
			else{
	    		playerdata.extra.putBoolean(name, bo);
	    		playerdata.clearEntity();
				updateTexture();
			}
		}
    	
    }
    class GuiTypeByte extends GuiType{
    	private final byte b;
    	public GuiTypeByte(String name, byte b){
    		super(name);
    		this.b = b;
    	}
    	
    	@Override
    	public void init(){
    		addButton(new GuiButtonBiDirectional(GuiCreationExtra.this,11, guiLeft + 120, guiTop + 45, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"}, b));
    	}
		@Override
		public void buttonEvent(GuiButtonNop button) {
			if(button.id != 11)
				return;
			playerdata.extra.putByte(name, (byte) button.getValue());
    		playerdata.clearEntity();
			updateTexture();
		}
    	
    }
    class GuiTypePixelmon extends GuiType{
    	
		public GuiTypePixelmon(String name) {
			super(name);
		}

		@Override
		public void init() {
			GuiCustomScroll scroll = new GuiCustomScroll(GuiCreationExtra.this, 1);
			scroll.setSize(120, 200);
			scroll.guiLeft = guiLeft + 120;
			scroll.guiTop = guiTop + 20;
			addScroll(scroll);
			
			scroll.setList(PixelmonHelper.getPixelmonList());
			scroll.setSelected(PixelmonHelper.getName(entity));
		}

		@Override
    	public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll){
			String name = scroll.getSelected();
	    	playerdata.setExtra(entity, "name", name);
			updateTexture();
    	}

	}
    
    class GuiTypeDoggyStyle extends GuiType{
		public GuiTypeDoggyStyle(String name) {
			super(name);
		}

		@Override
		public void init() {
			Enum breed = null;
			try {
				Method method = entity.getClass().getMethod("getBreedID");
				breed = (Enum) method.invoke(entity);
			} catch (Exception e) {
				
			}
	    	addButton(new GuiButtonBiDirectional(GuiCreationExtra.this,11, guiLeft + 120, guiTop + 45, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26"}, breed.ordinal()));
		}
		
		@Override
		public void buttonEvent(GuiButtonNop button) {
			if(button.id != 11)
				return;
			int breed = button.getValue();
	    	LivingEntity entity = playerdata.getEntity(npc);
	    	playerdata.setExtra(entity, "breed", button.getValue() + "");
			updateTexture();
		}
    }

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
