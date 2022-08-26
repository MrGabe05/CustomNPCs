package noppes.npcs.client.gui.roles;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketBanksGet;
import noppes.npcs.packets.server.SPacketNpcRoleSave;
import noppes.npcs.roles.RoleBank;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class GuiNpcBankSetup extends GuiNPCInterface2 implements IScrollData, ICustomScrollListener {
	private GuiCustomScroll scroll;
	private Map<String,Integer> data = new HashMap<String,Integer>();
	private RoleBank role;

    public GuiNpcBankSetup(EntityNPCInterface npc){
    	super(npc);
    	role = (RoleBank) npc.role;
		Packets.sendServer(new SPacketBanksGet());
    }

    @Override
    public void init(){
        super.init();
        if(scroll == null)
        	scroll = new GuiCustomScroll(this,0);
        scroll.setSize(200, 152);
        scroll.guiLeft = guiLeft + 85;
        scroll.guiTop = guiTop + 20;
        addScroll(scroll);
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
    }
	
	@Override
	public void setData(Vector<String> list, Map<String, Integer> data) {
		String name = null;
		Bank bank = role.getBank();
		if(bank != null)
			name = bank.name;
		this.data = data;
		scroll.setList(list);
		
		if(name != null)
			setSelected(name);
	}

    @Override
    public boolean mouseClicked(double i, double j, int k){
    	if(k == 0 && scroll != null)
    		scroll.mouseClicked(i, j, k);
		return super.mouseClicked(i, j, k);
    }
    
	@Override
	public void setSelected(String selected) {
		scroll.setSelected(selected);
	}
	
	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll guiCustomScroll) {
		if(guiCustomScroll.id == 0){
			role.bankId = data.get(scroll.getSelected());
			save();
		}
	}

	@Override
	public void save() {
		Packets.sendServer(new SPacketNpcRoleSave(role.save(new CompoundNBT())));
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
