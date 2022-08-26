package noppes.npcs.client.gui.roles;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.roles.JobFollower;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

import java.util.ArrayList;
import java.util.List;

public class GuiNpcFollowerJob extends GuiNPCInterface2 implements ICustomScrollListener {
	private JobFollower job;
	private GuiCustomScroll scroll;
    public GuiNpcFollowerJob(EntityNPCInterface npc)
    {
    	super(npc);    	
    	job = (JobFollower) npc.job;
    }

	@Override
    public void init() {
    	super.init();

        addLabel(new GuiLabel(1,"gui.name", guiLeft + 6, guiTop + 110));
        addTextField(new GuiTextFieldNop(1,this, guiLeft + 50, guiTop + 105, 200, 20, job.name));

    	scroll = new GuiCustomScroll(this,0);
        scroll.setSize(143, 208);
        scroll.guiLeft = guiLeft + 268;
        scroll.guiTop = guiTop + 4;
        this.addScroll(scroll);
        
        List<String> names = new ArrayList<String>();
        List<EntityNPCInterface> list = npc.level.getEntitiesOfClass(EntityNPCInterface.class, npc.getBoundingBox().inflate(40, 40, 40));
        for(EntityNPCInterface npc : list){
        	if(npc == this.npc || names.contains(npc.display.getName()))
        		continue;
        	names.add(npc.display.getName());
        }
        scroll.setList(names);
    }

    @Override
	public void save() {
    	job.name = getTextField(1).getValue();
		Packets.sendServer(new SPacketNpcJobSave(job.save(new CompoundNBT())));
	}

	@Override
	public void scrollClicked(double i, double j, int k,
			GuiCustomScroll guiCustomScroll) {
		getTextField(1).setValue(guiCustomScroll.getSelected());
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}


}
