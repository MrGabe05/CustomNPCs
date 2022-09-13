package noppes.npcs.client.gui.advanced;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuSave;

public class GuiNPCSoundsMenu extends GuiNPCInterface2 implements ITextfieldListener {
	private GuiSoundSelection gui;
	private GuiTextFieldNop selectedField;
	
    public GuiNPCSoundsMenu(EntityNPCInterface npc){
    	super(npc);
    }

	@Override
    public void init(){
        super.init();
        addLabel(new GuiLabel(0,"advanced.idlesound", guiLeft + 5, guiTop + 20));
        addTextField(new GuiTextFieldNop(0,this,  guiLeft + 80, guiTop + 15, 200, 20, npc.advanced.getSound(0)));
        addButton(new GuiButtonNop(this, 0, guiLeft + 290, guiTop + 15, 80, 20, "gui.selectSound"));
                
        addLabel(new GuiLabel(2,"advanced.angersound", guiLeft + 5, guiTop + 45));
        addTextField(new GuiTextFieldNop(2,this,  guiLeft + 80, guiTop + 40, 200, 20, npc.advanced.getSound(1)));
        addButton(new GuiButtonNop(this, 2, guiLeft + 290, guiTop + 40, 80, 20, "gui.selectSound"));
        
        addLabel(new GuiLabel(3,"advanced.hurtsound", guiLeft + 5, guiTop + 70));
        addTextField(new GuiTextFieldNop(3,this,  guiLeft + 80, guiTop + 65, 200, 20, npc.advanced.getSound(2)));
        addButton(new GuiButtonNop(this, 3, guiLeft + 290, guiTop + 65, 80, 20, "gui.selectSound"));
        
        addLabel(new GuiLabel(4,"advanced.deathsound", guiLeft + 5, guiTop + 95));
        addTextField(new GuiTextFieldNop(4,this,  guiLeft + 80, guiTop + 90, 200, 20, npc.advanced.getSound(3)));
        addButton(new GuiButtonNop(this, 4, guiLeft + 290, guiTop + 90, 80, 20, "gui.selectSound"));
        
        addLabel(new GuiLabel(5,"advanced.stepsound", guiLeft + 5, guiTop + 120));
        addTextField(new GuiTextFieldNop(5,this,  guiLeft + 80, guiTop + 115, 200, 20, npc.advanced.getSound(4)));
        addButton(new GuiButtonNop(this, 5, guiLeft + 290, guiTop + 115, 80, 20, "gui.selectSound"));

        addLabel(new GuiLabel(6,"advanced.haspitch", guiLeft + 5, guiTop + 150));
        addButton(new GuiButtonNop(this, 6, guiLeft + 120, guiTop + 145, 80, 20, new String[]{"gui.no","gui.yes"}, npc.advanced.disablePitch?0:1));
    
    }

	@Override
    public void buttonEvent(GuiButtonNop button){
		if(button.id == 6)
			npc.advanced.disablePitch = button.getValue() == 0;
		else{
	    	selectedField = getTextField(button.id);
	    	setSubGui(new GuiSoundSelection(selectedField.getValue()));
		}
    	
    }
    
	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 0)
			npc.advanced.setSound(0, textfield.getValue());
		if(textfield.id == 2)
			npc.advanced.setSound(1, textfield.getValue());
		if(textfield.id == 3)
			npc.advanced.setSound(2, textfield.getValue());
		if(textfield.id == 4)
			npc.advanced.setSound(3, textfield.getValue());
		if(textfield.id == 5)
			npc.advanced.setSound(4, textfield.getValue());
	}
	
	@Override
	public void save() {
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.ADVANCED, npc.advanced.save(new CompoundNBT())));
	}

	@Override
	public void subGuiClosed(Screen subgui) {
		GuiSoundSelection gss = (GuiSoundSelection) subgui;
		if(gss.selectedResource != null) {
			selectedField.setValue(gss.selectedResource.toString());
			unFocused(selectedField);
		}
	}

}
