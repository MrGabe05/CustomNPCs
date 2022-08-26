package noppes.npcs.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiNpcTextureCloaks extends GuiNpcSelectionInterface
{
    public GuiNpcTextureCloaks(EntityNPCInterface npc,Screen parent){
    	super(npc, parent, npc.display.getCapeTexture().isEmpty()?"customnpcs:textures/cloak/":npc.display.getCapeTexture());
    	title = "Select Cloak";
    }

    public void init(){
        super.init();
        int index = npc.display.getCapeTexture().lastIndexOf("/");
        if(index > 0){
        	String asset = npc.display.getCapeTexture().substring(index + 1);
        	if(npc.display.getCapeTexture().equals(assets.getAsset(asset)))
        		slot.setSelected(asset);
        }
    }


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    	int l = -50;
    	int i1 =  (height/2) + 30;
        drawNpc(npc,l, i1, 2, 180);

    }

    @Override
    public void elementClicked(){
    	if(dataTextures.contains(slot.getSelectedString()) && slot.getSelectedString() != null){
    		npc.display.setCapeTexture(assets.getAsset(slot.getSelectedString()));
    	}
    }

	@Override
	public String[] getExtension() {
		return new String[]{"png"};
	}


}
