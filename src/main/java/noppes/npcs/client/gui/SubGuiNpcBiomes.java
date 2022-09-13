package noppes.npcs.client.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.controllers.data.SpawnData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class SubGuiNpcBiomes extends GuiBasic
{
	private final SpawnData data;
	private GuiCustomScroll scroll1;
	private GuiCustomScroll scroll2;
	
    public SubGuiNpcBiomes(SpawnData data){
    	this.data = data;
		setBackground("menubg.png");
		imageWidth = 346;
		imageHeight = 216;
    }

	@Override
    public void init(){
        super.init();
        if(scroll1 == null){
        	scroll1 = new GuiCustomScroll(this,0);
        	scroll1.setSize(140, 180);
        }
        scroll1.guiLeft = guiLeft + 4;
        scroll1.guiTop = guiTop + 14;
        this.addScroll(scroll1);
        addLabel(new GuiLabel(1, "spawning.availableBiomes", guiLeft + 4, guiTop + 4));
        
        if(scroll2 == null){
        	scroll2 = new GuiCustomScroll(this,1);
        	scroll2.setSize(140, 180);
        }
        scroll2.guiLeft = guiLeft + 200;
        scroll2.guiTop = guiTop + 14;
        this.addScroll(scroll2);
        addLabel(new GuiLabel(2, "spawning.spawningBiomes", guiLeft + 200, guiTop + 4));
        
        List<String> biomes = new ArrayList<String>();
        Iterator<Biome> ita = ForgeRegistries.BIOMES.iterator();
        while (ita.hasNext()) {
        	Biome base = ita.next();
            if (base != null && base.getRegistryName() != null && !data.biomes.contains(base.getRegistryName().toString())) {
            	biomes.add(base.getRegistryName().toString());
            }
        }
        scroll1.setList(biomes);
        scroll2.setList(data.biomes.stream().map(Object::toString).collect(Collectors.toList()));

    	addButton(new GuiButtonNop(this, 1, guiLeft + 145, guiTop + 40, 55, 20, ">"));
    	addButton(new GuiButtonNop(this, 2, guiLeft + 145, guiTop + 62, 55, 20, "<"));

    	addButton(new GuiButtonNop(this, 3, guiLeft + 145, guiTop + 90, 55, 20, ">>"));
    	addButton(new GuiButtonNop(this, 4, guiLeft + 145, guiTop + 112, 55, 20, "<<"));
        
        
    	addButton(new GuiButtonNop(this, 66, guiLeft + 260, guiTop + 194, 60, 20, "gui.done"));
    }

	@Override
	public void buttonEvent(GuiButtonNop guibutton){
    	GuiButtonNop button = guibutton;
		if(button.id == 1){
			if(scroll1.hasSelected()){
				data.biomes.add(new ResourceLocation(scroll1.getSelected()));
				scroll1.clearSelection();
				scroll2.clearSelection();
				init();
			}				
		}
		if(button.id == 2){
			if(scroll2.hasSelected()){
				data.biomes.remove(scroll2.getSelected());
				scroll2.clearSelection();
				init();
			}				
		}
		if(button.id == 3){
			data.biomes.clear();
	        Iterator<Biome> ita = ForgeRegistries.BIOMES.iterator();
	        while (ita.hasNext()) {
	        	Biome base = ita.next();
	            if (base != null) {
	            	data.biomes.add(base.getRegistryName());
	            }
	        }
			scroll1.clearSelection();
			scroll2.clearSelection();
			init();
		}
		if(button.id == 4){
			data.biomes.clear();
			scroll1.clearSelection();
			scroll2.clearSelection();
			init();
		}
		if(button.id == 66){
        	close();
        }
    }

}
