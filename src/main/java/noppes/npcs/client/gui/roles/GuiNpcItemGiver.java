package noppes.npcs.client.gui.roles;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.SubGuiNpcAvailability;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.containers.ContainerNpcItemGiver;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.roles.JobItemGiver;

import java.util.ArrayList;
import java.util.List;

public class GuiNpcItemGiver extends GuiContainerNPCInterface2<ContainerNpcItemGiver>
{	
	private final JobItemGiver role;

	public GuiNpcItemGiver(ContainerNpcItemGiver container, PlayerInventory inv, ITextComponent titleIn) {
    	super(NoppesUtil.getLastNpc(), container, inv, titleIn);
    	imageHeight = 200;
    	role = (JobItemGiver) npc.job;
    	setBackground("npcitemgiver.png");
    }

    public void init()
    {
    	super.init();
    	this.addButton(new GuiButtonNop(this, 0, guiLeft + 6, guiTop + 6, 140,20, new String[]{"Random Item","All Items","Give Not Owned Items","Give When Doesnt Own Any","Chained"},role.givingMethod));
    	this.addButton(new GuiButtonNop(this, 1, guiLeft + 6, guiTop + 29, 140,20, new String[]{"Timer","Give Only Once","Daily"},role.cooldownType));

    	addTextField(new GuiTextFieldNop(0, this,  guiLeft + 55, guiTop + 54, 90, 20, role.cooldown + ""));
    	getTextField(0).numbersOnly = true;
        addLabel(new GuiLabel(0,"Cooldown:", guiLeft + 6, guiTop + 59));
        addLabel(new GuiLabel(1,"Items to give", guiLeft + 46, guiTop + 79));
        
        getTextField(0).numbersOnly = true;

        int i = 0;
        for(String line : role.lines){
        	addTextField(new GuiTextFieldNop(i+1, this,  guiLeft + 150, guiTop + 6 + i * 24, 236, 20,line));
        	i++;
        }
        for(;i <3; i++){
        	addTextField(new GuiTextFieldNop(i+1, this,  guiLeft + 150, guiTop + 6 + i * 24, 236, 20,""));
        }
    	getTextField(0).enabled = role.isOnTimer();
    	getLabel(0).enabled = role.isOnTimer();

		addLabel(new GuiLabel(4, "availability.options", guiLeft + 180, guiTop + 101));
		addButton(new GuiButtonNop(this, 4, guiLeft + 280, guiTop + 96, 50, 20, "selectServer.edit"));
    }

    public void buttonEvent(GuiButtonNop guibutton)
    {
    	GuiButtonNop button = guibutton;
        if(button.id == 0)
        {
        	role.givingMethod = button.getValue();
        }
        if(button.id == 1)
        {
        	role.cooldownType = button.getValue();
        	getTextField(0).enabled = role.isOnTimer();
        	getLabel(0).enabled = role.isOnTimer();
        }
        if(button.id == 4){
        	setSubGui(new SubGuiNpcAvailability(role.availability));
        }
    	        
    }

	public void save() {

		List<String> lines = new ArrayList<String>();
    	for(int i = 1; i < 4; i++){
    		GuiTextFieldNop tf = getTextField(i);
    		if(!tf.isEmpty())
    			lines.add(tf.getValue());
    	}
    	role.lines = lines;
		int cc = 10;
		if(!getTextField(0).isEmpty() && getTextField(0).isInteger())
			cc = getTextField(0).getInteger();

		role.cooldown = cc;

		Packets.sendServer(new SPacketNpcJobSave(role.save(new CompoundNBT())));
	}

}
