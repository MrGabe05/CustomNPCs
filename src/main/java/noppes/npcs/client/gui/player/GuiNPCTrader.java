package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.containers.ContainerNPCTrader;
import noppes.npcs.roles.RoleTrader;

public class GuiNPCTrader extends GuiContainerNPCInterface<ContainerNPCTrader>{
	private final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/trader.png");
	private final ResourceLocation slot = new ResourceLocation("customnpcs","textures/gui/slot.png");
	private RoleTrader role;
	private ContainerNPCTrader container;

	public GuiNPCTrader(ContainerNPCTrader container, PlayerInventory inv, ITextComponent titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        this.container = container;
        role = (RoleTrader) npc.role;
        imageHeight = 224;
        imageWidth = 223;
        this.title = "role.trader";
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        super.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(resource);
        blit(matrixStack, guiLeft, guiTop, 0, 0, imageWidth, imageHeight);
        RenderSystem.enableRescaleNormal();

        minecraft.getTextureManager().bind(slot);
		for(int slot = 0; slot < 18; slot++){
			int i = guiLeft + slot%3 * 72 + 10;
			int j = guiTop + slot/3 * 21 + 6;
			
			ItemStack item = role.inventoryCurrency.items.get(slot);
			ItemStack item2 = role.inventoryCurrency.items.get(slot + 18);
			if(NoppesUtilServer.IsItemStackNull(item)){
				item = item2;
				item2 = ItemStack.EMPTY;
			}
			if(NoppesUtilPlayer.compareItems(item, item2, false, false)){
				item = item.copy();
				item.setCount(item.getCount() + item2.getCount());
				item2 = ItemStack.EMPTY;
			}

			ItemStack sold = role.inventorySold.items.get(slot);
			RenderSystem.color4f(1, 1, 1, 1);
	        minecraft.getTextureManager().bind(this.slot);
	        blit(matrixStack, i + 42, j, 0, 0, 18, 18);
			if(!NoppesUtilServer.IsItemStackNull(item) && !NoppesUtilServer.IsItemStackNull(sold)){
	            //RenderHelper.enableGUIStandardItemLighting();
	            if(!NoppesUtilServer.IsItemStackNull(item2)){
	            	itemRenderer.renderAndDecorateItem(item2, i, j + 1);
		        	itemRenderer.renderGuiItemDecorations(font, item2, i, j + 1);
	            }
		        itemRenderer.renderAndDecorateItem(item, i + 18, j + 1);
		        itemRenderer.renderGuiItemDecorations(font, item, i + 18, j + 1);
	            //RenderHelper.disableStandardItemLighting();
	
	            font.draw(matrixStack, "=", i + 36, j + 5, CustomNpcResourceListener.DefaultTextColor);

			}
    	}
		RenderSystem.disableRescaleNormal();

    }
    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {
		for(int slot = 0; slot < 18; slot++){
			int i = slot%3 * 72 + 10;
			int j = slot/3 * 21 + 6;
			
			ItemStack item = role.inventoryCurrency.items.get(slot);
			ItemStack item2 = role.inventoryCurrency.items.get(slot + 18);
			if(NoppesUtilServer.IsItemStackNull(item)){
				item = item2;
				item2 = ItemStack.EMPTY;
			}
			if(NoppesUtilPlayer.compareItems(item, item2, role.ignoreDamage, role.ignoreNBT)){
				item = item.copy();
				item.setCount(item.getCount() + item2.getCount());
				item2 = ItemStack.EMPTY;
			}
			ItemStack sold = role.inventorySold.items.get(slot);
			if(NoppesUtilServer.IsItemStackNull(sold))
				continue;
			
			if(this.isHovering(i + 43, j + 1, 16, 16, x, y)){
				if(!container.canBuy(item, item2, player)){
					RenderSystem.translatef(0, 0, 300);
					if(!item.isEmpty() && !NoppesUtilPlayer.compareItems(player, item, role.ignoreDamage, role.ignoreNBT))
						this.fillGradient(matrixStack, i + 17, j, i + 35, j + 18, 0x70771010, 0x70771010);
					if(!item2.isEmpty() && !NoppesUtilPlayer.compareItems(player, item2, role.ignoreDamage, role.ignoreNBT))
						this.fillGradient(matrixStack, i - 1, j, i + 17, j + 18, 0x70771010, 0x70771010);
					
		        	String title = I18n.get("trader.insufficient");
					this.font.draw(matrixStack, title, (imageWidth - font.width(title))/2, 131, 0xDD0000);
					RenderSystem.translatef(0, 0, -300);
				}
				else{
		        	String title = I18n.get("trader.sufficient");
					this.font.draw(matrixStack, title, (imageWidth - font.width(title))/2, 131, 0x00DD00);
				}
			}

            if (this.isHovering(i, j, 16, 16, x, y) && !NoppesUtilServer.IsItemStackNull(item2)){
                this.renderTooltip(matrixStack, item2, x - guiLeft, y - guiTop);
            }
            if (this.isHovering(i + 18, j, 16, 16, x, y)){
                this.renderTooltip(matrixStack, item, x - guiLeft, y - guiTop);
            }
    	}
    }

	@Override
	public void buttonEvent(GuiButtonNop button) {

	}

	@Override
	public void save() {
	}
}
