package noppes.npcs.client.gui.advanced;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.GuiNPCLinesEdit;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuSave;

public class GuiNPCLinesMenu extends GuiNPCInterface2{
    public GuiNPCLinesMenu(EntityNPCInterface npc){
    	super(npc);
    }

    @Override
    public void init(){
        super.init();
    	this.addButton(new GuiButtonNop(this, 0, guiLeft + 85, guiTop + 20, "lines.world"));
    	this.addButton(new GuiButtonNop(this, 1, guiLeft + 85, guiTop + 43, "lines.attack"));
    	this.addButton(new GuiButtonNop(this, 2, guiLeft + 85, guiTop + 66, "lines.interact"));
    	this.addButton(new GuiButtonNop(this, 5, guiLeft + 85, guiTop + 89, "lines.killed"));
    	this.addButton(new GuiButtonNop(this, 6, guiLeft + 85, guiTop + 112, "lines.kill"));
    	this.addButton(new GuiButtonNop(this, 7, guiLeft + 85, guiTop + 135, "lines.npcinteract"));

    	this.addLabel(new GuiLabel(16, "lines.random", guiLeft + 85, guiTop + 157));
    	this.addButton(new GuiButtonYesNo(this, 16, guiLeft + 175, guiTop + 152, !npc.advanced.orderedLines));
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
        if(id == 0){
        	NoppesUtil.openGUI(player, new GuiNPCLinesEdit(npc, npc.advanced.worldLines));
        }
        if(id == 1){
        	NoppesUtil.openGUI(player, new GuiNPCLinesEdit(npc, npc.advanced.attackLines));
        }
        if(id == 2){
        	NoppesUtil.openGUI(player, new GuiNPCLinesEdit(npc, npc.advanced.interactLines));
        }
        if(id == 5){
        	NoppesUtil.openGUI(player, new GuiNPCLinesEdit(npc, npc.advanced.killedLines));
        }
        if(id == 6){
        	NoppesUtil.openGUI(player, new GuiNPCLinesEdit(npc, npc.advanced.killLines));
        }
        if(id == 7){
        	NoppesUtil.openGUI(player, new GuiNPCLinesEdit(npc, npc.advanced.npcInteractLines));
        }
        if(id == 16){
        	npc.advanced.orderedLines = !((GuiButtonYesNo)guibutton).getBoolean();
        }
    }
	
	public void save() {
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.ADVANCED, npc.advanced.save(new CompoundNBT())));
	}


}
