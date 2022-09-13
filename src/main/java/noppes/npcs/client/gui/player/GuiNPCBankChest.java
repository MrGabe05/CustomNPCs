package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.containers.ContainerNPCBankInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketBankUnlock;
import noppes.npcs.packets.server.SPacketBankUpgrade;
import noppes.npcs.packets.server.SPacketBanksSlotOpen;

public class GuiNPCBankChest extends GuiContainerNPCInterface<ContainerNPCBankInterface> implements IGuiData
{
	private final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/bankchest.png");
    private final ContainerNPCBankInterface container;
    private int availableSlots = 0;
    private int maxSlots = 1;
    private int unlockedSlots = 1;
    private ItemStack currency;

	public GuiNPCBankChest(ContainerNPCBankInterface container, PlayerInventory inv, ITextComponent titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        this.container = container;
        this.title = "";
        passEvents = false;
        imageHeight = 235;
        
    }
    
    @Override
    public void init(){
    	super.init();
    	availableSlots = 0;
    	if(maxSlots > 1){
	        for(int i = 0; i < maxSlots ; i++){
	        	GuiButtonNop button = new GuiButtonNop(this, i, guiLeft - 50, guiTop + 10 + i * 24, 50, 20, I18n.get("gui.tab") + " " + (i+1));
	        	if( i > unlockedSlots)
	        		button.setEnabled(false);
	        	addButton(button);
	        	availableSlots++;
	        }
	        if(availableSlots == 1)
	        	buttons.clear();
    	}
        if(!container.isAvailable()){
        	addButton(new GuiButtonNop(this, 8, guiLeft + 48, guiTop + 48,80,20, I18n.get("bank.unlock")));
        }
        else if(container.canBeUpgraded()){
        	addButton(new GuiButtonNop(this, 9, guiLeft + 48, guiTop + 48,80,20, I18n.get("bank.upgrade")));
        }
    	if(maxSlots > 1){
    		getButton(container.slot).visible = false;
    		getButton(container.slot).setEnabled(false);
    	}
    }
    
    @Override
    public void buttonEvent(GuiButtonNop guibutton){
    	int id = guibutton.id;
    	if(id < 6){
    		Packets.sendServer(new SPacketBanksSlotOpen(id, container.bankid));
    	}
    	if(id == 8){
    		Packets.sendServer(new SPacketBankUnlock());
    	}
    	if(id == 9){
    		Packets.sendServer(new SPacketBankUpgrade());
    	}
    	

    }
    
    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		super.renderBg(matrixStack, partialTicks, x, y);
    	RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(resource);
        int l = (width - imageWidth) / 2;
        int i1 = (height - imageHeight) / 2;
		blit(matrixStack, l, i1, 0, 0, imageWidth, 6);
        if(!container.isAvailable()){
			blit(matrixStack, l, i1 + 6, 0, 6, imageWidth, 64);
			blit(matrixStack, l, i1 + 70, 0, 124, imageWidth, 222-124);
	        int i = guiLeft + 30;
	        int j = guiTop + 8;
	      	font.draw(matrixStack, I18n.get("bank.unlockCosts")+":", i , j + 4 , CustomNpcResourceListener.DefaultTextColor);
	        drawItem(matrixStack, i + 90,j,currency,x,y);
        }
        else if(container.isUpgraded()){
			blit(matrixStack, l, i1 + 60, 0, 60, imageWidth, 162);
			blit(matrixStack, l, i1 + 6, 0, 60, imageWidth, 64);
        }
        else if(container.canBeUpgraded()){
			blit(matrixStack, l, i1 + 6, 0, 6, imageWidth, 216);
	        int i = guiLeft + 30;
	        int j = guiTop + 8;
	        font.draw(matrixStack, I18n.get("bank.upgradeCosts") + ":", i , j + 4 , CustomNpcResourceListener.DefaultTextColor);
	        drawItem(matrixStack, i + 90,j,currency,x,y);
        }
        else{
			blit(matrixStack, l, i1 + 6, 0, 60, imageWidth, 162);
        }
        if(maxSlots > 1){
	        for(int ii = 0; ii < maxSlots ; ii++){
	        	if(availableSlots == ii)
	        		break;
	        	font.draw(matrixStack, "Tab " + (ii+1), guiLeft - 40, guiTop + 16 + ii * 24 , 0xFFFFFF);
	        }
        }

    }
    private void drawItem(MatrixStack matrixStack, int x, int y, ItemStack item, int mouseX, int mouseY){
		if(NoppesUtilServer.IsItemStackNull(item))
			return;
		RenderSystem.enableRescaleNormal();
        //RenderHelper.enableGUIStandardItemLighting();
        itemRenderer.renderAndDecorateItem(item, x,y);
        itemRenderer.renderGuiItemDecorations(font, item, x,y);
        //RenderHelper.disableStandardItemLighting();
        RenderSystem.disableRescaleNormal();

        if (this.isHovering(x - guiLeft, y - guiTop, 16, 16, mouseX, mouseY)){
            this.renderTooltip(matrixStack, item, mouseX, mouseY);
        }
    }
	@Override
	public void save() {
	}
	@Override
	public void setGuiData(CompoundNBT compound) {
		maxSlots = compound.getInt("MaxSlots");
		unlockedSlots = compound.getInt("UnlockedSlots");
		if(compound.contains("Currency"))
			currency = ItemStack.of(compound.getCompound("Currency"));
		else
			currency = ItemStack.EMPTY;
		if(container.currency != null)
			container.currency.item = currency;
		init();
	}
}
