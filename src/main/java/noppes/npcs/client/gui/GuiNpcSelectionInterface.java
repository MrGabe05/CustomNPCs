package noppes.npcs.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import noppes.npcs.client.AssetsBrowser;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiStringSlotNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.common.util.NaturalOrderComparator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

public abstract class GuiNpcSelectionInterface extends GuiNPCInterface {
	public GuiStringSlotNop slot;
	public Screen parent;
	
	private final String up = "..<" + I18n.get("gui.up") + ">..";

	private String root = "";
	public AssetsBrowser assets;
	private final HashSet<String> dataFolder = new HashSet<String>();
	protected HashSet<String> dataTextures = new HashSet<String>();
	
    public GuiNpcSelectionInterface(EntityNPCInterface npc, Screen parent, String sound){
    	super(npc);
    	root = AssetsBrowser.getRoot(sound);
    	assets = new AssetsBrowser(root, getExtension());
    	drawDefaultBackground = false;
    	title = "";
    	this.parent = parent;
    }

    @Override
    public void init(){
        super.init();
        dataFolder.clear();
        String ss = "Current Folder: /assets" + root;
        addLabel(new GuiLabel(0,ss, width / 2 - (this.font.width(ss)/2), 20, 0xffffff));
        Vector<String> list = new Vector<String>();
        if(!assets.isRoot)
        	list.add(up);
        for(String folder : assets.folders){
        	list.add("/" + folder);
        	dataFolder.add("/" + folder);
        }
        for(String texture : assets.files){
        	list.add(texture);
        	dataTextures.add(texture);
        }
        Collections.sort(list, new NaturalOrderComparator());
        slot = new GuiStringSlotNop(list,this,false);
		this.children.add(this.slot);

    	this.addButton(new GuiButtonNop(this, 2, width / 2 - 100, height - 44,98, 20, "gui.back"));
		this.addButton(new GuiButtonNop(this, 3, width / 2 + 2, height - 44,98, 20, "gui.up"));
    	getButton(3).active = !assets.isRoot;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    	slot.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void elementClicked(){
    	if(slot.getSelected() != null && this.dataTextures.contains(slot.getSelectedString())){
    		if(parent instanceof GuiNPCInterface){
    			((GuiNPCInterface)parent).elementClicked();
    		}
    		else if(parent instanceof GuiNPCInterface2){
    			((GuiNPCInterface2)parent).elementClicked();
    		}
    	}
    }
    
    @Override
    public void doubleClicked(){
    	if(slot.getSelectedString().equals(up)){
    		root = root.substring(0,root.lastIndexOf("/"));
        	assets = new AssetsBrowser(root, getExtension());
    		init();
    	}
    	else if(dataFolder.contains(slot.getSelectedString())){
    		root += slot.getSelectedString();
        	assets = new AssetsBrowser(root, getExtension());
    		init();
    	}
    	else {
    		close();
    		NoppesUtil.openGUI(player, parent);
    	}
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
        if(id == 2){
        	close();
        	NoppesUtil.openGUI(player, parent);
        }
        if(id == 3){
    		root = root.substring(0,root.lastIndexOf("/"));
        	assets = new AssetsBrowser(root, getExtension());
    		init();
        }
    }

    @Override
	public void save() {
	}

    public String getSelected(){
    	return assets.getAsset(slot.getSelectedString());
    }
    
    public abstract String[] getExtension();
}
