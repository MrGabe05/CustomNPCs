package noppes.npcs.client.gui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class GuiContainerNPCInterface2<T extends Container> extends GuiContainerNPCInterface<T>
{
	private ResourceLocation background = new ResourceLocation("customnpcs","textures/gui/menubg.png");
	private final ResourceLocation defaultBackground = new ResourceLocation("customnpcs","textures/gui/menubg.png");
	private GuiNpcMenu menu;
	public int menuYOffset = 0;

    public GuiContainerNPCInterface2(EntityNPCInterface npc, T cont, PlayerInventory inv, ITextComponent titleIn){
    	this(npc, cont, inv, titleIn, -1);
    }
    public GuiContainerNPCInterface2(EntityNPCInterface npc, T cont, PlayerInventory inv, ITextComponent titleIn, int activeMenu){
    	super(npc, cont, inv, titleIn);
    	this.imageWidth = 420;
    	this.menu = new GuiNpcMenu(this,activeMenu,npc);
    	title = "";
    }
    
    public void setBackground(String texture){
    	background = new ResourceLocation("customnpcs","textures/gui/" + texture);
    }

    public ResourceLocation getResource(String texture){
    	return new ResourceLocation("customnpcs","textures/gui/" + texture);
    }
    
	@Override
    public void init(){
    	super.init();
        menu.initGui(guiLeft, guiTop + menuYOffset, imageWidth);
    }   

    @Override
    public boolean mouseClicked(double i, double j, int k){
    	if(!hasSubGui())
	    	menu.mouseClicked(i, j, k);
    	return super.mouseClicked(i, j, k);
    }
    
    public void delete(){
    	npc.delete();
        setScreen(null);
        minecraft.mouseHandler.grabMouse();
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
    	renderBackground(matrixStack);
    	//RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(background);
        blit(matrixStack, guiLeft, guiTop, 0, 0, 256, 256);
        //RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(defaultBackground);
        blit(matrixStack, guiLeft + imageWidth-200, guiTop, 26, 0, 200, 220);
        
        menu.drawElements(matrixStack, font, x, y, minecraft, partialTicks);
        super.renderBg(matrixStack, partialTicks, x, y);
    }

//    @Override
//    protected void drawSlotInventory(Slot par1Slot)
//    {this.dra
//        if(subgui == null)
//        	super.drawSlotInventory(par1Slot);
//    } //TODO fix
}
