package noppes.npcs.client.gui.mainmenu;

import net.minecraft.util.math.BlockPos;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.global.GuiNpcManagePlayerData;
import noppes.npcs.client.gui.global.GuiNpcNaturalSpawns;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiNPCGlobalMainMenu extends GuiNPCInterface2{
    public GuiNPCGlobalMainMenu(EntityNPCInterface npc){
    	super(npc,6);
    }

    @Override
    public void init(){
    	super.init();
    	int y = guiTop + 10;
    	this.addButton(new GuiButtonNop(this, 2, guiLeft + 85, y, "global.banks"));
    	this.addButton(new GuiButtonNop(this, 3, guiLeft + 85, y += 22, "menu.factions"));
    	this.addButton(new GuiButtonNop(this, 4, guiLeft + 85, y += 22, "dialog.dialogs"));
    	this.addButton(new GuiButtonNop(this, 11, guiLeft + 85, y += 22, "quest.quests"));
    	this.addButton(new GuiButtonNop(this, 12, guiLeft + 85, y += 22, "global.transport"));
    	this.addButton(new GuiButtonNop(this, 13, guiLeft + 85, y += 22, "global.playerdata"));
    	this.addButton(new GuiButtonNop(this, 14, guiLeft + 85, y += 22, NoppesStringUtils.translate("global.recipes", "(Broken)")));
    	this.addButton(new GuiButtonNop(this, 15, guiLeft + 85, y += 22, NoppesStringUtils.translate("global.naturalspawn", "(WIP)")));
    	this.addButton(new GuiButtonNop(this, 16, guiLeft + 85, y += 22, "global.linked"));
    	
    }
    
    @Override
	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
		if (id == 11) {
			NoppesUtil.requestOpenGUI(EnumGuiType.ManageQuests);
		}
		if (id == 2) {
			NoppesUtil.requestOpenGUI(EnumGuiType.ManageBanks);
		}
		if (id == 3) {
			NoppesUtil.requestOpenGUI(EnumGuiType.ManageFactions);
		}
		if (id == 4) {
			NoppesUtil.requestOpenGUI(EnumGuiType.ManageDialogs);
		}
		if (id == 12) {
			NoppesUtil.requestOpenGUI(EnumGuiType.ManageTransport);
		}
		if (id == 13) {
			NoppesUtil.openGUI(player, new GuiNpcManagePlayerData(npc, this));
		}
		if (id == 14) {
			NoppesUtil.requestOpenGUI(EnumGuiType.ManageRecipes, new BlockPos(4, 0, 0));
		}
		if (id == 15) {
			NoppesUtil.openGUI(player, new GuiNpcNaturalSpawns(npc));
		}
		if (id == 16) {
			NoppesUtil.requestOpenGUI(EnumGuiType.ManageLinked);
		}
	}
	@Override
	public void save() {
	}
    

}
