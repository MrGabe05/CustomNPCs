package noppes.npcs.client.gui.player.companion;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerNPCCompanion;
import noppes.npcs.roles.RoleCompanion;

public class GuiNpcCompanionInv extends GuiContainerNPCInterface<ContainerNPCCompanion>{
	private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/companioninv.png");
	private final ResourceLocation slot = new ResourceLocation("customnpcs", "textures/gui/slot.png");
	private final RoleCompanion role;

	public GuiNpcCompanionInv(ContainerNPCCompanion container, PlayerInventory inv, ITextComponent titleIn) {
		super(NoppesUtil.getLastNpc(), container, inv, titleIn);
		role = (RoleCompanion) npc.role;
		imageWidth = 171;
		imageHeight = 166;
	}

	@Override
	public void init() {
		super.init();
		GuiNpcCompanionStats.addTopMenu(role, this, 3);
	
	}

	@Override
	public void buttonEvent(GuiButtonNop guibutton) {

		int id = guibutton.id;
		if(id == 1){
			CustomNpcs.proxy.openGui(npc, EnumGuiType.Companion);
		}
		if(id == 2){
			CustomNpcs.proxy.openGui(npc, EnumGuiType.CompanionTalent);
		}
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int par1, int limbSwingAmount) {
		//super.renderLabels(par1, limbSwingAmount);
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float f, int xMouse, int yMouse) {
        super.renderBackground(matrixStack, 0);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(resource);
		blit(matrixStack, guiLeft, guiTop, 0, 0, imageWidth, imageHeight);
		minecraft.getTextureManager().bind(slot);
		if(role.getTalentLevel(EnumCompanionTalent.ARMOR) > 0){
			for(int i = 0; i < 4; i++){
				blit(matrixStack, guiLeft + 5, guiTop + 7 + i * 18 , 0, 0, 18, 18);
			}
		}
		if(role.getTalentLevel(EnumCompanionTalent.SWORD) > 0){
			blit(matrixStack, guiLeft + 78, guiTop + 16, 0, npc.inventory.weapons.get(0) == null?18:0, 18, 18);
		}
		if(role.getTalentLevel(EnumCompanionTalent.RANGED) > 0){
			
		}
		if(role.talents.containsKey(EnumCompanionTalent.INVENTORY)){
			int size = (role.getTalentLevel(EnumCompanionTalent.INVENTORY) + 1) * 2;
			for(int i = 0; i < size; i++){
				blit(matrixStack, guiLeft + 113 + i % 3 * 18, guiTop + 7 + i / 3 * 18 , 0, 0, 18, 18);
			}
		}

	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		super.drawNpc(52, 70);
	}

	@Override
	public void save() {
		
	}
}
