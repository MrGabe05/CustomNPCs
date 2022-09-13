package noppes.npcs.client.gui.advanced;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.SubGuiNpcFactionOptions;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketFactionsGet;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.packets.server.SPacketNpcFactionSet;

public class GuiNPCFactionSetup extends GuiNPCInterface2 implements IScrollData,ICustomScrollListener
{
	private GuiCustomScroll scrollFactions;
	private Map<String,Integer> data = new HashMap<String,Integer>();
	
    public GuiNPCFactionSetup(EntityNPCInterface npc)
    {
    	super(npc);
    }

    public void init()
    {
        super.init();

        this.addLabel(new GuiLabel(0, "faction.attackHostile", guiLeft + 4, guiTop + 25));
        this.addButton(new GuiButtonNop(this, 0, guiLeft + 144, guiTop + 20,40,20, new String[]{"gui.no","gui.yes"}, npc.advanced.attackOtherFactions?1:0));

        this.addLabel(new GuiLabel(1, "faction.defend", guiLeft + 4, guiTop + 47));
        this.addButton(new GuiButtonNop(this, 1, guiLeft + 144, guiTop + 42,40,20, new String[]{"gui.no","gui.yes"}, npc.advanced.defendFaction?1:0));

        this.addLabel(new GuiLabel(12, "faction.ondeath", guiLeft + 4, guiTop + 69));
        addButton(new GuiButtonNop(this, 12, guiLeft + 90, guiTop + 64, 80, 20, "faction.points"));
        
        if(scrollFactions == null){
	        scrollFactions = new GuiCustomScroll(this,0);
	        scrollFactions.setSize(180, 200);
        }
        scrollFactions.guiLeft = guiLeft + 200;
        scrollFactions.guiTop = guiTop + 4;
        this.addScroll(scrollFactions);
    	Packets.sendServer(new SPacketFactionsGet());
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
    {
    	GuiButtonNop button = guibutton;
        if(button.id == 0)
        {
        	npc.advanced.attackOtherFactions = button.getValue() == 1;
        }
        if(button.id == 1)
        {
        	npc.advanced.defendFaction = button.getValue() == 1;
        }
    	if(button.id == 12)
        	setSubGui(new SubGuiNpcFactionOptions(npc.advanced.factions));
    }
    
    }
	
	@Override
	public void setData(Vector<String> list, Map<String, Integer> data)
	{
		String name = npc.getFaction().name;
		this.data = data;
		scrollFactions.setList(list);
		
		if(name != null)
			setSelected(name);
	}

	@Override
    public boolean mouseClicked(double i, double j, int k) {
    	if(k == 0 && scrollFactions != null)
    		scrollFactions.mouseClicked(i, j, k);
    	return super.mouseClicked(i, j, k);
    }
	
	@Override
	public void setSelected(String selected) {
		scrollFactions.setSelected(selected);
	}
	
	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll guiCustomScroll) {
		if(guiCustomScroll.id == 0)
		{
			Packets.sendServer(new SPacketNpcFactionSet(data.get(scrollFactions.getSelected())));
		}
	}

	@Override
	public void save() {
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.ADVANCED, npc.advanced.save(new CompoundNBT())));
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
