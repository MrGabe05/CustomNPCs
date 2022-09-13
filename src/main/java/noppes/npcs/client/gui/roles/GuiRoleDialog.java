package noppes.npcs.client.gui.roles;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.shared.client.gui.GuiTextAreaScreen;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcRoleSave;
import noppes.npcs.roles.RoleDialog;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

import java.util.HashMap;


public class GuiRoleDialog extends GuiNPCInterface2  {
	private final RoleDialog role;
	
	private int slot = 0;

    public GuiRoleDialog(EntityNPCInterface npc){
    	super(npc);
    	role = (RoleDialog) npc.role;
    }

    @Override
    public void init(){
        super.init();
        
        addLabel(new GuiLabel(0, "dialog.starttext", guiLeft + 4, guiTop + 10));
        addButton(new GuiButtonNop(this, 0, guiLeft + 60, guiTop + 5, 50, 20, "selectServer.edit"));

        addLabel(new GuiLabel(100, "dialog.options", guiLeft + 4, guiTop + 34));
        
        for(int i = 1; i <= 6; i++){
        	int y = guiTop + 24 + i *23;
            addLabel(new GuiLabel(i, i + ":", guiLeft + 4, y + 5));
            String text = role.options.get(i);
            if(text == null)
            	text = "";
            addTextField(new GuiTextFieldNop(i, this, guiLeft + 16, y, 280, 20, text));
            addButton(new GuiButtonNop(this, i, guiLeft + 310, y, 50, 20, "selectServer.edit"));
            
        }
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
    	if(guibutton.id <= 6){
    		save();
    		slot = guibutton.id;
    		String text = role.dialog;
    		if(slot >= 1){
    			text = role.optionsTexts.get(slot);
    		}
    		if(text == null)
    			text = "";
    		setSubGui(new GuiTextAreaScreen(text));
    	}
    }

	@Override
	public void save() {
		HashMap<Integer,String> map = new HashMap<Integer, String>();
        for(int i = 1; i <= 6; i++){
        	String text = getTextField(i).getValue();
        	if(!text.isEmpty())
        		map.put(i, text);
        }
        role.options = map;
		Packets.sendServer(new SPacketNpcRoleSave(role.save(new CompoundNBT())));
	}

	@Override
	public void subGuiClosed(Screen subgui) {
		if(subgui instanceof GuiTextAreaScreen){
			GuiTextAreaScreen text = (GuiTextAreaScreen) subgui;
			if(slot == 0)
				role.dialog = text.text;
			else{
				if(text.text.isEmpty())
					role.optionsTexts.remove(slot);
				else
					role.optionsTexts.put(slot, text.text);
			}
		}
	}
}
