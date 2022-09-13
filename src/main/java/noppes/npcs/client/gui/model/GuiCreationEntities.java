package noppes.npcs.client.gui.model;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.NPCRendererHelper;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomEntities;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

import java.util.*;
import java.util.stream.Collectors;

public class GuiCreationEntities extends GuiCreationScreenInterface implements ICustomScrollListener {
	private final List<EntityType<? extends Entity>> types;
	private GuiCustomScroll scroll;
	private boolean resetToSelected = true;
	
	public GuiCreationEntities(EntityNPCInterface npc){
		super(npc);
		types = getAllEntities(npc.level);
		Collections.sort(types, Comparator.comparing(t -> t.getDescriptionId().toLowerCase()));
		active = 1;
		xOffset = 60;
	}

	private static List<EntityType<? extends Entity>> getAllEntities(World level){
		List<EntityType<? extends Entity>> data = new ArrayList<>();

		for(EntityType<? extends Entity> ent : ForgeRegistries.ENTITIES.getValues()){
			try {
				Entity e = ent.create(level);
				if(e != null){
					if(LivingEntity.class.isAssignableFrom(e.getClass())){
						data.add(ent);
					}
					e.remove();
					e.removed = true;
				}
			}
			catch(Exception e){

			}
		}

		return data;
	}

    @Override
    public void init() {
    	super.init();
    	addButton(new GuiButtonNop(this, 10, guiLeft, guiTop + 46, 120, 20, "Reset To NPC", button -> {
			playerdata.setEntity((String)null);
			npc.display.setSkinTexture("customnpcs:textures/entity/humanmale/steve.png");
			resetToSelected = true;
			init();
		}));
    	if(scroll == null){
    		scroll = new GuiCustomScroll(this, 0);
    		scroll.setUnsortedList(types.stream().map(EntityType::getDescriptionId).collect(Collectors.toList()));
    	}
    	scroll.guiLeft = guiLeft;
    	scroll.guiTop = guiTop + 68;
    	scroll.setSize(120, imageHeight - 96);

		int index = -1;
		EntityType selectedType = CustomEntities.entityCustomNpc;
    	if(entity != null){
	    	for(int i = 0; i < types.size(); i++){
				EntityType type = types.get(i);
	    		if(type == entity.getType()){
					index = i;
					selectedType = type;
					break;
	    		}
	    	}
    	}
		if(index >= 0){
			scroll.setSelectedIndex(index);
		}
		else{
			scroll.setSelected("entity.customnpcs.customnpc");
		}
    	
    	if(resetToSelected){
    		scroll.scrollTo(scroll.getSelected());
    		resetToSelected = false;
    	}
    	addScroll(scroll);

		addLabel(new GuiLabel(110, "gui.simpleRenderer", guiLeft + 124, guiTop + 5, 0xff0000));
		addButton(new GuiButtonYesNo(this, 110, guiLeft + 260, guiTop, playerdata.simpleRender, b -> playerdata.simpleRender = ((GuiButtonYesNo)b).getBoolean()));
    }

	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
		String selected = scroll.getSelected();
		if(selected.equals("entity.customnpcs.customnpc")){
			playerdata.setEntity((String)null);
		}
		else{
			playerdata.setEntity(types.get(scroll.getSelectedIndex()).getRegistryName());
		}
		Entity entity = playerdata.getEntity(npc);
		if(entity != null){
			EntityRenderer render = minecraft.getEntityRenderDispatcher().getRenderer(entity);
			if(render instanceof LivingRenderer && !NPCRendererHelper.getTexture(render, entity).equals("minecraft:missingno")) {
				npc.display.setSkinTexture(NPCRendererHelper.getTexture(render, entity));
			}
		}
		else{
			npc.display.setSkinTexture("customnpcs:textures/entity/humanmale/steve.png");
		}
		init();
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}

}
