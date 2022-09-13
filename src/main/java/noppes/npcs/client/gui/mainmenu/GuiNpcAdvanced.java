package noppes.npcs.client.gui.mainmenu;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.advanced.*;
import noppes.npcs.client.gui.roles.*;
import noppes.npcs.shared.client.gui.components.GuiButtonBiDirectional;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.packets.server.SPacketNpcJobGet;
import noppes.npcs.packets.server.SPacketNpcRoleGet;

public class GuiNpcAdvanced extends GuiNPCInterface2 implements IGuiData
{
	private boolean hasChanges = false;
    public GuiNpcAdvanced(EntityNPCInterface npc)
    {
    	super(npc, 5);
    	Packets.sendServer(new SPacketMenuGet(EnumMenuType.ADVANCED));
    }
    @Override
    public void init(){
    	super.init();
    	int y = guiTop + 8;
    	this.addButton(new GuiButtonNop(this, 3, guiLeft + 85 + 160, y, 52, 20, "selectServer.edit"));
    	this.addButton(new GuiButtonBiDirectional(this,8, guiLeft + 85, y,155,20, new String[]{"role.none","role.trader","role.follower","role.bank","role.transporter", "role.mailman", NoppesStringUtils.translate("role.companion", "(WIP)"), "dialog.dialog"},npc.role.getType()));
    	getButton(3).setEnabled(npc.role.getType() != RoleType.NONE && npc.role.getType() != RoleType.MAILMAN);

    	this.addButton(new GuiButtonNop(this, 4, guiLeft + 85 + 160, y += 22, 52, 20, "selectServer.edit"));
    	this.addButton(new GuiButtonBiDirectional(this,5, guiLeft + 85, y,155,20, new String[]{"job.none","job.bard","job.healer","job.guard","job.itemgiver","role.follower", "job.spawner", "job.conversation", "job.chunkloader", "job.puppet", "job.builder", "job.farmer"},npc.job.getType()));

   		getButton(4).setEnabled(npc.job.getType() != JobType.NONE && npc.job.getType() != JobType.CHUNKLOADER && npc.job.getType() != JobType.BUILDER);

    	this.addButton(new GuiButtonNop(this, 7, guiLeft + 15, y += 22, 190, 20, "advanced.lines"));
    	this.addButton(new GuiButtonNop(this, 9, guiLeft + 208, y, 190, 20, "menu.factions"));
    	this.addButton(new GuiButtonNop(this, 10, guiLeft + 15, y += 22, 190, 20, "dialog.dialogs"));
    	this.addButton(new GuiButtonNop(this, 11, guiLeft + 208, y, 190, 20, "advanced.sounds"));
    	this.addButton(new GuiButtonNop(this, 12, guiLeft + 15, y += 22, 190, 20, "advanced.night"));
    	this.addButton(new GuiButtonNop(this, 13, guiLeft + 208, y, 190, 20, "global.linked"));
    	this.addButton(new GuiButtonNop(this, 14, guiLeft + 15, y += 22, 190, 20, "advanced.scenes"));
    	this.addButton(new GuiButtonNop(this, 15, guiLeft + 208, y, 190, 20, "advanced.marks"));
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton) {
    	GuiButtonNop button = guibutton;
		if (button.id == 3) {
			save();
			Packets.sendServer(new SPacketNpcRoleGet());
		}
        if(button.id == 8){
			hasChanges = true;
        	npc.advanced.setRole(button.getValue());
			Packets.sendServer(new SPacketMenuSave(EnumMenuType.ADVANCED, npc.advanced.save(new CompoundNBT())));

        	getButton(3).setEnabled(npc.role.getType() != RoleType.NONE && npc.role.getType() != RoleType.MAILMAN);
        }
        if(button.id == 4){
        	save();
			Packets.sendServer(new SPacketNpcJobGet());
        }
        if(button.id == 5){
			hasChanges = true;
        	npc.advanced.setJob(button.getValue());
			Packets.sendServer(new SPacketMenuSave(EnumMenuType.ADVANCED, npc.advanced.save(new CompoundNBT())));

       		getButton(4).setEnabled(npc.job.getType() != JobType.NONE && npc.job.getType() != JobType.CHUNKLOADER && npc.job.getType() != JobType.BUILDER);
        }
        if(button.id == 9){
        	save();
        	NoppesUtil.openGUI(player, new GuiNPCFactionSetup(npc));
        }
        if(button.id == 10){
        	save();
        	NoppesUtil.openGUI(player, new GuiNPCDialogNpcOptions(npc,this));
        }
        if(button.id == 11){
        	save();
        	NoppesUtil.openGUI(player, new GuiNPCSoundsMenu(npc));
        }
		if (button.id == 7) {
        	save();
			NoppesUtil.openGUI(player, new GuiNPCLinesMenu(npc));
		}
        if(button.id == 12){
        	save();
			NoppesUtil.openGUI(player, new GuiNPCNightSetup(npc));
        }
        if(button.id == 13){
        	save();
			NoppesUtil.openGUI(player, new GuiNPCAdvancedLinkedNpc(npc));
        }
        if(button.id == 14){
        	save();
			NoppesUtil.openGUI(player, new GuiNPCScenes(npc));
        }
        if(button.id == 15){
        	save();
			NoppesUtil.openGUI(player, new GuiNPCMarks(npc));
        }
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		if(compound.contains("RoleData")){
			npc.role.load(compound);
			
			if(npc.role.getType() == RoleType.TRADER)
				NoppesUtil.requestOpenGUI(EnumGuiType.SetupTrader);
			else if(npc.role.getType() == RoleType.FOLLOWER)
				NoppesUtil.requestOpenGUI(EnumGuiType.SetupFollower);
			else if(npc.role.getType() == RoleType.BANK)
				setScreen(new GuiNpcBankSetup(npc));
			else if(npc.role.getType() == RoleType.TRANSPORTER)
				setScreen(new GuiNpcTransporter(npc));
			else if(npc.role.getType() == RoleType.COMPANION)
				setScreen(new GuiNpcCompanion(npc));
			else if(npc.role.getType() == RoleType.DIALOG)
				NoppesUtil.openGUI(player, new GuiRoleDialog(npc));
		}
		else if(compound.contains("JobData")){
			npc.job.load(compound);
			
			if(npc.job.getType() == JobType.BARD)
				NoppesUtil.openGUI(player, new GuiNpcBard(npc));
			else if(npc.job.getType() == JobType.HEALER)
				NoppesUtil.openGUI(player, new GuiNpcHealer(npc));
			else if(npc.job.getType() == JobType.GUARD)
				NoppesUtil.openGUI(player, new GuiNpcGuard(npc));
			else if(npc.job.getType() == JobType.ITEMGIVER)
				NoppesUtil.requestOpenGUI(EnumGuiType.SetupItemGiver);
			else if(npc.job.getType() == JobType.FOLLOWER)
				NoppesUtil.openGUI(player, new GuiNpcFollowerJob(npc));
			else if(npc.job.getType() == JobType.SPAWNER)
				NoppesUtil.openGUI(player, new GuiNpcSpawner(npc));
			else if(npc.job.getType() == JobType.CONVERSATION)
				NoppesUtil.openGUI(player, new GuiNpcConversation(npc));
			else if(npc.job.getType() == JobType.PUPPET)
				NoppesUtil.openGUI(player, new GuiNpcPuppet(this,(EntityCustomNpc) npc));
			else if(npc.job.getType() == JobType.FARMER)
				NoppesUtil.openGUI(player, new GuiJobFarmer(npc));	
		}
		else{
			npc.advanced.readToNBT(compound);
			init();
		}
	}
	
	@Override
	public void save() {
		if(hasChanges){
			Packets.sendServer(new SPacketMenuSave(EnumMenuType.ADVANCED, npc.advanced.save(new CompoundNBT())));
			hasChanges = false;
		}
	}
    

}
