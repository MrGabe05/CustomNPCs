package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.containers.ContainerNPCFollower;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketFollowerExtend;
import noppes.npcs.packets.server.SPacketFollowerState;
import noppes.npcs.packets.server.SPacketNpcRoleGet;
import noppes.npcs.roles.RoleFollower;

public class GuiNpcFollower extends GuiContainerNPCInterface<ContainerNPCFollower>  implements IGuiData{
	private final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/follower.png");
	private RoleFollower role;

	public GuiNpcFollower(ContainerNPCFollower container, PlayerInventory inv, ITextComponent titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        role = (RoleFollower) npc.role;
        
        Packets.sendServer(new SPacketNpcRoleGet());
    }
    @Override
    public void init(){
    	super.init();
        buttons.clear();
        addButton(new GuiButtonNop(this, 4, guiLeft + 100, guiTop+ 110, 50, 20, new String[]{I18n.get("follower.waiting"),I18n.get("follower.following")},role.isFollowing?1:0));
        if(!role.infiniteDays)
        	addButton(new GuiButtonNop(this, 5, guiLeft + 8, guiTop+ 30, 50, 20, I18n.get("follower.hire")));
    }
    @Override
    public void buttonEvent(GuiButtonNop guibutton){

    	int id = guibutton.id;
        if(id == 4){
        	Packets.sendServer(new SPacketFollowerState());
        }
        if(id == 5){
        	Packets.sendServer(new SPacketFollowerExtend());
        }
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {
    	font.draw(matrixStack, I18n.get("follower.health")+": " + npc.getHealth()+"/"+ npc.getMaxHealth(), 62, 70, CustomNpcResourceListener.DefaultTextColor);
    	if(!role.infiniteDays){
	       if(role.getDays() <= 1)
	    	   font.draw(matrixStack, I18n.get("follower.daysleft")+": " + I18n.get("follower.lastday"), 62, 94, CustomNpcResourceListener.DefaultTextColor);
	       else
	    	   font.draw(matrixStack, I18n.get("follower.daysleft") + ": " + (role.getDays()-1), 62, 94, CustomNpcResourceListener.DefaultTextColor);
    	}
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
    	RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(resource);
		int l = guiLeft;
	    int i1 = guiTop;
		blit(matrixStack, l, i1, 0, 0, imageWidth, imageHeight);
		int index = 0;
		if(!role.infiniteDays){
			for(int slot = 0; slot < role.inventory.items.size(); slot++){
				ItemStack itemstack = role.inventory.items.get(slot);
				if(NoppesUtilServer.IsItemStackNull(itemstack))
					continue;
	            int days = 1;
	            if(role.rates.containsKey(slot))
	            	days = role.rates.get(slot);
	            
				int yOffset = index * 20;
				
				int i = guiLeft +  68;
				int j = guiTop + yOffset + 4;
				RenderSystem.enableRescaleNormal();
	            //RenderHelper.enableGUIStandardItemLighting();
	            itemRenderer.renderAndDecorateItem(itemstack, x + 11,y);
	            itemRenderer.renderGuiItemDecorations(font, itemstack, x+11,y);
	            //RenderHelper.disableStandardItemLighting();
	            RenderSystem.disableRescaleNormal();
	
	            String daysS = days + " " + ((days == 1)?I18n.get("follower.day"):I18n.get("follower.days"));
	            font.draw(matrixStack, " = "+daysS, i + 27, j + 4, CustomNpcResourceListener.DefaultTextColor);
		        //font.draw(quantity, x + 0 + (12-font.width(quantity))/2, y + 4, 0x404040);

		        if (this.isHovering(i - guiLeft  + 11, j - guiTop, 16, 16, mouseX, mouseY))
		        {
		            this.renderTooltip(matrixStack, itemstack, mouseX, mouseY);
		        }
		        index++;
	    	}
		}
		this.drawNpc(33, 131);
    }
    
	@Override
	public void save() {
		
	}
	@Override
	public void setGuiData(CompoundNBT compound) {
		npc.role.load(compound);
		init();
	}
}
