package noppes.npcs.client.gui.roles;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.roles.JobFarmer;


public class GuiJobFarmer extends GuiNPCInterface2{
	private JobFarmer job;

    public GuiJobFarmer(EntityNPCInterface npc){
    	super(npc);
    	job = (JobFarmer) npc.job;
    }

    @Override
    public void init(){
        super.init();
        
        addLabel(new GuiLabel(0, "farmer.itempicked", guiLeft + 10, guiTop + 20));
        addButton(new GuiButtonNop(this, 0, guiLeft + 100, guiTop + 15, 160, 20, new String[]{"farmer.donothing", "farmer.chest", "farmer.drop"}, job.chestMode));
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
    	if(guibutton.id == 0){
    		job.chestMode = ((GuiButtonNop)guibutton).getValue();
    	}
    }

    @Override
	public void save() {
		Packets.sendServer(new SPacketNpcJobSave(job.save(new CompoundNBT())));
	}
}
