
package noppes.npcs.client.gui.roles;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface2;
import noppes.npcs.containers.ContainerNPCTraderSetup;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcMarketSet;
import noppes.npcs.packets.server.SPacketNpcRoleSave;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNpcTraderSetup extends GuiContainerNPCInterface2<ContainerNPCTraderSetup> implements ITextfieldListener {
	
	private final ResourceLocation slot = new ResourceLocation("customnpcs","textures/gui/slot.png");
	private final RoleTrader role;

	public GuiNpcTraderSetup(ContainerNPCTraderSetup container, PlayerInventory inv, ITextComponent titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
    	imageHeight = 220;
    	menuYOffset = 10;
    	role = container.role;
    }
    
	@Override
    public void init(){
    	super.init();
        buttons.clear();
        setBackground("tradersetup.png");
        addLabel(new GuiLabel(0, "role.marketname", guiLeft + 214, guiTop + 150));
        addTextField(new GuiTextFieldNop(0, this, guiLeft + 214, guiTop + 160, 180, 20, role.marketName));

        addLabel(new GuiLabel(1, "gui.ignoreDamage", guiLeft + 260, guiTop + 29));
        addButton(new GuiButtonYesNo(this, 1, guiLeft + 340, guiTop + 24, role.ignoreDamage));
        
        addLabel(new GuiLabel(2, "gui.ignoreNBT", guiLeft + 260, guiTop + 51));
        addButton(new GuiButtonYesNo(this, 2, guiLeft + 340, guiTop + 46, role.ignoreNBT));
    }

	@Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    	guiTop += 10;
    	super.render(matrixStack, mouseX, mouseY, partialTicks);
    	guiTop -= 10;
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton){
    	if(guibutton.id == 1){
    		role.ignoreDamage = ((GuiButtonYesNo)guibutton).getBoolean();
    	}
    	if(guibutton.id == 2){
    		role.ignoreNBT = ((GuiButtonYesNo)guibutton).getBoolean();
    	}
    }

	@Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int xMouse, int yMouse) {
		super.renderBg(matrixStack, partialTicks, xMouse, yMouse);
		for(int slot = 0; slot < 18; slot++){
			int x = guiLeft + slot%3 * 94 + 7;
			int y = guiTop + slot/3 * 22 + 4;

	        minecraft.getTextureManager().bind(this.slot);
			blit(matrixStack, x - 1, y, 0, 0, 18, 18);
			blit(matrixStack, x + 17, y, 0, 0, 18, 18);
			
            font.draw(matrixStack, "=", x + 36, y + 5, CustomNpcResourceListener.DefaultTextColor);
	        minecraft.getTextureManager().bind(this.slot);
			blit(matrixStack, x + 42, y, 0, 0, 18, 18);
		}
    }
	
	@Override
	public void save() {
		Packets.sendServer(new SPacketNpcMarketSet(role.marketName, true));
		Packets.sendServer(new SPacketNpcRoleSave(role.save(new CompoundNBT())));
	}
	
	@Override
	public void unFocused(GuiTextFieldNop guiNpcTextField) {
		String name = guiNpcTextField.getValue();
		if(!name.equalsIgnoreCase(role.marketName)){
			role.marketName = name;
			Packets.sendServer(new SPacketNpcMarketSet(role.marketName, false));
		}
			
	}
}
