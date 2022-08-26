package noppes.npcs.client.gui.mainmenu;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.SubGuiNpcMovement;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataAI;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;

public class GuiNpcAI extends GuiNPCInterface2 implements ITextfieldListener, IGuiData{
	private String[] tacts = {"aitactics.rush", "aitactics.stagger", "aitactics.orbit", "aitactics.hitandrun", "aitactics.ambush", "aitactics.stalk", "gui.none"};
	
	private DataAI ai;
	public GuiNpcAI(EntityNPCInterface npc) {
		super(npc,3);
		ai = npc.ais;
		Packets.sendServer(new SPacketMenuGet(EnumMenuType.AI));
	}

	@Override
    public void init() {
        super.init();
        addLabel(new GuiLabel(0,"ai.enemyresponse", guiLeft + 5, guiTop + 17));
    	addButton(new GuiButtonNop(this, 0,guiLeft + 86, guiTop + 10, 60, 20, new String[]{"gui.retaliate","gui.panic","gui.retreat","gui.nothing"} ,npc.ais.onAttack));
    	addLabel(new GuiLabel(1,"ai.door", guiLeft + 5, guiTop + 40));
    	addButton(new GuiButtonNop(this, 1,guiLeft + 86, guiTop + 35, 60, 20, new String[]{"gui.break","gui.open","gui.disabled"} ,npc.ais.doorInteract));
    	addLabel(new GuiLabel(12,"ai.swim", guiLeft + 5, guiTop + 65));
    	addButton(new GuiButtonNop(this, 7,guiLeft + 86, guiTop + 60, 60, 20, new String[]{"gui.no", "gui.yes"} ,npc.ais.canSwim ? 1:0));
    	addLabel(new GuiLabel(13,"ai.shelter", guiLeft + 5, guiTop + 90));
    	addButton(new GuiButtonNop(this, 9,guiLeft + 86, guiTop + 85, 60, 20, new String[]{"gui.darkness","gui.sunlight","gui.disabled"} ,npc.ais.findShelter));
    	addLabel(new GuiLabel(14,"ai.clearlos", guiLeft + 5, guiTop + 115));
    	addButton(new GuiButtonNop(this, 10,guiLeft + 86, guiTop + 110, 60, 20, new String[]{"gui.no", "gui.yes"} ,npc.ais.directLOS ? 1:0));

    	addButton(new GuiButtonYesNo(this, 23, guiLeft + 86, guiTop + 135, 60, 20, ai.attackInvisible));
    	addLabel(new GuiLabel(23,"stats.attackInvisible", guiLeft + 5, guiTop + 140));
    	
    	addLabel(new GuiLabel(10,"ai.avoidwater", guiLeft + 150, guiTop + 17));
    	addButton(new GuiButtonNop(this, 5, guiLeft + 230, guiTop + 10, 60, 20, new String[]{"gui.no","gui.yes"} ,ai.avoidsWater? 1:0));
    	addLabel(new GuiLabel(11,"ai.return", guiLeft + 150, guiTop + 40));
    	addButton(new GuiButtonNop(this, 6, guiLeft + 230, guiTop + 35, 60, 20, new String[]{"gui.no","gui.yes"} ,npc.ais.returnToStart ? 1:0));
    	addLabel(new GuiLabel(17,"ai.leapattarget", guiLeft + 150, guiTop + 65));
        addButton(new GuiButtonNop(this, 15,guiLeft + 230, guiTop + 60, 60, 20, new String[]{"gui.no", "gui.yes"} ,npc.ais.canLeap ? 1:0));

    	addLabel(new GuiLabel(2,"ai.movement", guiLeft + 4, guiTop + 165));
    	addButton(new GuiButtonNop(this, 2, guiLeft + 86, guiTop + 160, 60, 20, "selectServer.edit"));
    }
    
	@Override
	public void unFocused(GuiTextFieldNop textfield){
	}
	
    @Override
	public void buttonEvent(GuiButtonNop guibutton)
    {
		GuiButtonNop button = (GuiButtonNop) guibutton;
		if(button.id == 0){
			ai.onAttack = button.getValue();
			init();
		}
		else if(button.id == 1){
			ai.doorInteract = button.getValue();
		}
		else if(button.id == 2){
			setSubGui(new SubGuiNpcMovement(ai));
		}
		else if (button.id == 5) {
			npc.ais.setAvoidsWater(button.getValue() == 1);
		}
		else if (button.id == 6) {
			ai.returnToStart = (button.getValue() == 1);
		}
		else if (button.id == 7) {
			ai.canSwim = (button.getValue() == 1);
		}
		else if(button.id == 9){
			ai.findShelter = button.getValue();
		}
		else if (button.id == 10) {
			ai.directLOS = (button.getValue() == 1);
		}
		else if (button.id == 15) {
			ai.canLeap = (button.getValue() == 1);
		}
		else if (button.id == 23) {
			ai.attackInvisible = ((GuiButtonYesNo)button).getBoolean();
		}
    }
    
	@Override
	public void save() {
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.AI, ai.save(new CompoundNBT())));
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		ai.readToNBT(compound);
		init();
	}

}
