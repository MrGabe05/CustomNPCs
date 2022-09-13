package noppes.npcs.client.gui.player.companion;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCompanionOpenInv;
import noppes.npcs.packets.server.SPacketNpcRoleGet;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiMenuTopIconButton;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNpcCompanionStats extends GuiNPCInterface implements IGuiData {
	private final RoleCompanion role;
	private boolean isEating = false;

	public GuiNpcCompanionStats(EntityNPCInterface npc) {
		super(npc);
		role = (RoleCompanion) npc.role;
		setBackground("companion.png");
		imageWidth = 171;
		imageHeight = 166;
		Packets.sendServer(new SPacketNpcRoleGet());
	}

	@Override
	public void init() {
		super.init();
		int y = guiTop + 10;
		addLabel(new GuiLabel(0, NoppesStringUtils.translate("gui.name", ": ",npc.display.getName()), guiLeft + 4, y));
		addLabel(new GuiLabel(1, NoppesStringUtils.translate("companion.owner", ": ",role.ownerName), guiLeft + 4, y+=12));
		addLabel(new GuiLabel(2, NoppesStringUtils.translate("companion.age", ": ", role.ticksActive / 18000 + " (", role.stage.name, ")" ), guiLeft + 4, y+=12));
		addLabel(new GuiLabel(3, NoppesStringUtils.translate("companion.strength", ": ", npc.stats.melee.getStrength()), guiLeft + 4, y+=12));
		addLabel(new GuiLabel(4, NoppesStringUtils.translate("companion.level", ": ", role.getTotalLevel()), guiLeft + 4, y+=12));
		addLabel(new GuiLabel(5, NoppesStringUtils.translate("job.name", ": ", "gui.none"), guiLeft + 4, y+=12));
		
		addTopMenu(role, this, 1);
	
	}
	
	public static void addTopMenu(RoleCompanion role, Screen screen, int active){
		if(screen instanceof GuiNPCInterface){
			GuiNPCInterface gui = (GuiNPCInterface) screen;
			GuiMenuTopIconButton button;
			gui.addTopButton(button = new GuiMenuTopIconButton(gui, 1, gui.guiLeft + 4, gui.guiTop - 27, "menu.stats", new ItemStack(Items.BOOK)));
			gui.addTopButton(button = new GuiMenuTopIconButton(gui, 2, button, "companion.talent", new ItemStack(Items.NETHER_STAR)));
			if(role.hasInv())
				gui.addTopButton(button = new GuiMenuTopIconButton(gui, 3, button, "inv.inventory", new ItemStack(Blocks.CHEST)));
			if(role.companionJobInterface.getType() != EnumCompanionJobs.NONE)
				gui.addTopButton(new GuiMenuTopIconButton(gui, 4, button, "job.name", new ItemStack(Items.CARROT)));
			gui.getTopButton(active).active = true;
		}
		if(screen instanceof GuiContainerNPCInterface){
			GuiContainerNPCInterface gui = (GuiContainerNPCInterface) screen;
			GuiMenuTopIconButton button;
			gui.addTopButton(button = new GuiMenuTopIconButton(gui, 1, gui.guiLeft + 4, gui.guiTop - 27, "menu.stats", new ItemStack(Items.BOOK)));
			gui.addTopButton(button = new GuiMenuTopIconButton(gui, 2, button, "companion.talent", new ItemStack(Items.NETHER_STAR)));
			if(role.hasInv())
				gui.addTopButton(button = new GuiMenuTopIconButton(gui, 3, button, "inv.inventory", new ItemStack(Blocks.CHEST)));
			if(role.companionJobInterface.getType() != EnumCompanionJobs.NONE)
				gui.addTopButton(new GuiMenuTopIconButton(gui, 4, button, "job.name", new ItemStack(Items.CARROT)));
			gui.getTopButton(active).active = true;
		}
	}

	@Override
	public void buttonEvent(GuiButtonNop guibutton) {

		int id = guibutton.id;
		if(id == 2){
			CustomNpcs.proxy.openGui(npc, EnumGuiType.CompanionTalent);
		}
		if(id == 3){
			Packets.sendServer(new SPacketCompanionOpenInv());
		}
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		if(isEating && !role.isEating()){
			Packets.sendServer(new SPacketNpcRoleGet());
		}
		
		isEating = role.isEating();
		super.drawNpc(34, 150);
		int y = drawHealth(matrixStack, guiTop + 88);
	}
	
	private int drawHealth(MatrixStack matrixStack, int y){
		this.minecraft.getTextureManager().bind(GUI_ICONS_LOCATION);

		int max = role.getTotalArmorValue();
		if(role.talents.containsKey(EnumCompanionTalent.ARMOR) || max > 0){
	        for (int i = 0; i < 10; ++i){
	            int x = guiLeft + 66 + i * 10;
	
	            if (i * 2 + 1 < max){
	                this.blit(matrixStack, x, y, 34, 9, 9, 9);
	            }
	
	            if (i * 2 + 1 == max){
	                this.blit(matrixStack, x, y, 25, 9, 9, 9);
	            }
	
	            if (i * 2 + 1 > max){
	                this.blit(matrixStack, x, y, 16, 9, 9, 9);
	            }
	            
	        }
	        y += 10;
		}
		
		max = MathHelper.ceil(npc.getMaxHealth());
        int k = (int)npc.getHealth();
        float scale = 1;
        if(max > 40){
        	scale = max / 40f;
        	k = (int) (k / scale);
        	max = 40;
        }
        for(int i = 0; i < max; i++){
        	int x = guiLeft + 66 + i % 20 * 5;
        	int offset = i / 20 * 10;
            this.blit(matrixStack, x, y + offset, 52 + i % 2 * 5, 9,  i % 2 == 1?4:5, 9);
            if(k > i)
                this.blit(matrixStack, x, y + offset, 52 + i % 2 * 5, 0, i % 2 == 1?4:5, 9);
        }
        
        k = role.foodstats.getFoodLevel();
        y += 10;
        if(max > 20)
            y += 10;
        
        for(int i = 0; i < 20; i++){
        	int x = guiLeft + 66 + i % 20 * 5;
            this.blit(matrixStack, x, y, 16 + i % 2 * 5, 27, i % 2 == 1?4:5, 9);
            if(k > i)
                this.blit(matrixStack, x, y, 52 + i % 2 * 5, 27, i % 2 == 1?4:5, 9);
        }
        return y;
	}

	@Override
	public void save() {
		
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		role.load(compound);
	}
}
