package noppes.npcs.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiNpcTextureOverlays extends GuiNpcSelectionInterface{
    public GuiNpcTextureOverlays(EntityNPCInterface npc,Screen parent){
    	super(npc, parent, npc.display.getOverlayTexture().isEmpty()?"customnpcs:textures/overlays/":npc.display.getOverlayTexture());
    	title = "Select Overlay";
    	this.parent = parent;
    }

    @Override
    public void init(){
    	super.init();
        int index = npc.display.getOverlayTexture().lastIndexOf("/");
        if(index > 0){
        	String asset = npc.display.getOverlayTexture().substring(index + 1);
        	if(npc.display.getOverlayTexture().equals(assets.getAsset(asset)))
        		slot.setSelected(asset);
        }
    }


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    	int l = -50;
    	int i1 =  (height/2) + 30;
        drawNpc(npc, l, i1, 2, 0);

    }
    
    @Override
    public void elementClicked(){
    	if(slot.getSelectedString() != null && dataTextures.contains(slot.getSelectedString())){
    		npc.display.setOverlayTexture(assets.getAsset(slot.getSelectedString()));
    	}
    }

	@Override
	public String[] getExtension() {
		return new String[]{"png"};
	}


}
