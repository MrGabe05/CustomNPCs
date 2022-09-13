package noppes.npcs.client.gui.select;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.NpcGuiHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.util.AssetsFinder;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.*;

public class GuiTextureSelection extends GuiNPCInterface implements ICustomScrollListener{

	private final String up = "..<" + I18n.get("gui.up") + ">..";
	private GuiCustomScroll scrollCategories;
	private GuiCustomScroll scrollQuests;

	private String location = "";
	private String selectedDomain;
	public ResourceLocation selectedResource;

	private static final HashMap<String, List<ResourceLocation>> domains = new HashMap<>();
	private static final HashMap<String, ResourceLocation> textures = new HashMap<>();

    public GuiTextureSelection(EntityNPCInterface npc, String texture){
    	this.npc = npc;
    	drawDefaultBackground = false;
		title = "";
		setBackground("menubg.png");
		imageWidth = 366;
		imageHeight = 226;

		if(domains.isEmpty()){
			List<ResourceLocation> resources = AssetsFinder.find("assets", ".png");
			for(ResourceLocation loc : resources){
				domains.computeIfAbsent(loc.getNamespace(), k -> new ArrayList<>()).add(loc);
			}
		}

		if(texture != null && !texture.isEmpty() && !texture.startsWith("http")){
			selectedResource = new ResourceLocation(texture);
			selectedDomain = selectedResource.getNamespace();
			if(!domains.containsKey(selectedDomain)) {
				selectedDomain = null;
			}
			int i = selectedResource.getPath().lastIndexOf('/');
			location = selectedResource.getPath().substring(0, i + 1);
		}
    }

	public static void clear(){
		domains.clear();
		textures.clear();
	}

    @Override
    public void init(){
        super.init();

        if(selectedDomain != null) {
            title = selectedDomain + ":" + location;
        }
        else {
        	title = "";
        }

    	this.addButton(new GuiButtonNop(this, 2, guiLeft + 264, guiTop + 170, 90, 20, "gui.done"));
    	this.addButton(new GuiButtonNop(this, 1, guiLeft + 264, guiTop + 190, 90, 20, "gui.cancel"));

        if(scrollCategories == null){
	        scrollCategories = new GuiCustomScroll(this,0);
	        scrollCategories.setSize(120, 200);
        }

        if(selectedDomain == null) {
            scrollCategories.setList(Lists.newArrayList(domains.keySet()));
            if(selectedDomain != null) {
            	scrollCategories.setSelected(selectedDomain);
            }
        }
        else {
        	List<String> list = new ArrayList<String>();
        	list.add(up);
        	List<ResourceLocation> data = domains.get(selectedDomain);
        	for(ResourceLocation td : data) {
				String fullPath = td.getPath();
				if(fullPath.indexOf('/') >= 0){
					fullPath = fullPath.substring(0, fullPath.lastIndexOf('/') + 1);
				}
        		if(location.isEmpty() || fullPath.startsWith(location) && !fullPath.equals(location)) {
        			String path = fullPath.substring(location.length());
        			int i = path.indexOf('/');
        			if(i < 0)
        				continue;
        			path = path.substring(0, i);
        			if(!path.isEmpty() && !list.contains(path)) {
        				list.add(path);
        			}
        		}
        	}
            scrollCategories.setList(list);
        }
        scrollCategories.guiLeft = guiLeft + 4;
        scrollCategories.guiTop = guiTop + 14;
        this.addScroll(scrollCategories);

        if(scrollQuests == null){
        	scrollQuests = new GuiCustomScroll(this,1);
        	scrollQuests.setSize(130, 200);
        }
        if(selectedDomain != null) {
        	textures.clear();
        	List<ResourceLocation> data = domains.get(selectedDomain);
        	List<String> list = new ArrayList<String>();
        	String loc = location;
        	if(scrollCategories.hasSelected() && !scrollCategories.getSelected().equals(up)) {
        		loc += scrollCategories.getSelected() + '/';
        	}
        	for(ResourceLocation td : data) {
				String name = td.getPath();
				String path = td.getPath();
				if(name.indexOf('/') >= 0){
					name = name.substring(name.lastIndexOf('/') + 1);
					path = path.substring(0, path.lastIndexOf('/') + 1);
				}
        		if(path.equals(loc) && !list.contains(name)) {
        			list.add(name);
        			textures.put(name, td);
        		}
        	}
        	scrollQuests.setList(list);
        }
        if(selectedResource != null) {
        	scrollQuests.setSelected(selectedResource.getPath());
        }
        scrollQuests.guiLeft = guiLeft + 125;
        scrollQuests.guiTop = guiTop + 14;
        this.addScroll(scrollQuests);

    }

	@Override
	public void buttonEvent(GuiButtonNop guibutton){

        if(guibutton.id == 2){
        	npc.display.setSkinTexture(selectedResource.toString());
        }
        npc.textureLocation = null;
		close();
		NpcGuiHelper.initGui(wrapper.parent);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

		npc.textureLocation = selectedResource;
        drawNpc(npc, guiLeft + 276, guiTop + 140, 2, 0);
    }


	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
		if(scroll == scrollQuests) {
			if(scroll.id == 1){
				selectedResource = textures.get(scroll.getSelected());
			}
		}
		else {
			init();
		}
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
		if(scroll == scrollCategories) {
			if(selectedDomain == null) {
				selectedDomain = selection;
			}
			else if(selection.equals(up)) {
				int i = location.lastIndexOf('/', location.length() - 2);
				if(i < 0) {
					if(location.isEmpty()) {
						selectedDomain = null;
					}
					location = "";
				}
				else {
					location = location.substring(0, i + 1);
				}
			}
			else {
				location = location + selection + '/';
			}
			scrollCategories.clearSelection();
			scrollQuests.clearSelection();
			init();
		}
		else {
        	npc.display.setSkinTexture(selectedResource.toString());
			close();
			NpcGuiHelper.initGui(wrapper.parent);
		}
	}
}
