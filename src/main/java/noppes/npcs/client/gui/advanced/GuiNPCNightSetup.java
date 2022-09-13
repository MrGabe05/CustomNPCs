package noppes.npcs.client.gui.advanced;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.DataTransform;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.packets.server.SPacketNpcTransform;

public class GuiNPCNightSetup extends GuiNPCInterface2 implements IGuiData {
	private final DataTransform data;
	
    public GuiNPCNightSetup(EntityNPCInterface npc) {
    	super(npc);
    	data = npc.transform;
    	Packets.sendServer(new SPacketMenuGet(EnumMenuType.TRANSFORM));
    }

    public void init()
    {
        super.init();

        this.addLabel(new GuiLabel(0, "menu.display", guiLeft + 4, guiTop + 25));
        this.addButton(new GuiButtonNop(this, 0, guiLeft + 104, guiTop + 20, 50, 20, new String[]{"gui.no","gui.yes"}, data.hasDisplay?1:0));

        this.addLabel(new GuiLabel(1, "menu.stats", guiLeft + 4, guiTop + 47));
        this.addButton(new GuiButtonNop(this, 1, guiLeft + 104, guiTop + 42, 50, 20, new String[]{"gui.no","gui.yes"}, data.hasStats?1:0));

        this.addLabel(new GuiLabel(2, "menu.ai", guiLeft + 4, guiTop + 69));
        this.addButton(new GuiButtonNop(this, 2, guiLeft + 104, guiTop + 64, 50, 20, new String[]{"gui.no","gui.yes"}, data.hasAi?1:0));

        this.addLabel(new GuiLabel(3, "menu.inventory", guiLeft + 4, guiTop + 91));
        this.addButton(new GuiButtonNop(this, 3, guiLeft + 104, guiTop + 86, 50, 20, new String[]{"gui.no","gui.yes"}, data.hasInv?1:0));

        this.addLabel(new GuiLabel(4, "menu.advanced", guiLeft + 4, guiTop + 113));
        this.addButton(new GuiButtonNop(this, 4, guiLeft + 104, guiTop + 108, 50, 20, new String[]{"gui.no","gui.yes"}, data.hasAdvanced?1:0));
        
        this.addLabel(new GuiLabel(5, "role.name", guiLeft + 4, guiTop + 135));
        this.addButton(new GuiButtonNop(this, 5, guiLeft + 104, guiTop + 130, 50, 20, new String[]{"gui.no","gui.yes"}, data.hasRole?1:0));
        
        this.addLabel(new GuiLabel(6, "job.name", guiLeft + 4, guiTop + 157));
        this.addButton(new GuiButtonNop(this, 6, guiLeft + 104, guiTop + 152, 50, 20, new String[]{"gui.no","gui.yes"}, data.hasJob?1:0));
    
        this.addLabel(new GuiLabel(10, "advanced.editingmode", guiLeft + 170, guiTop + 9));
        this.addButton(new GuiButtonNop(this, 10, guiLeft + 244, guiTop + 4, 50, 20, new String[]{"gui.no","gui.yes"}, data.editingModus?1:0));
    
        if(data.editingModus){
        	this.addButton(new GuiButtonNop(this, 11, guiLeft + 170, guiTop + 34, "advanced.loadday"));
        	this.addButton(new GuiButtonNop(this, 12, guiLeft + 170, guiTop + 56, "advanced.loadnight"));
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
    	GuiButtonNop button = guibutton;
        if(button.id == 0)
        	data.hasDisplay = button.getValue() == 1;
        if(button.id == 1)
        	data.hasStats = button.getValue() == 1;
        if(button.id == 2)
        	data.hasAi = button.getValue() == 1;
        if(button.id == 3)
        	data.hasInv = button.getValue() == 1;
        if(button.id == 4)
        	data.hasAdvanced = button.getValue() == 1;
        if(button.id == 5)
        	data.hasRole = button.getValue() == 1;
        if(button.id == 6)
        	data.hasJob = button.getValue() == 1;

        if(button.id == 10){
        	data.editingModus = button.getValue() == 1;
        	save();
        	init();
        }
        if(button.id == 11){
        	Packets.sendServer(new SPacketNpcTransform(false));
        }
        if(button.id == 12){
        	Packets.sendServer(new SPacketNpcTransform(true));
        }
    }

    @Override
	public void save() {
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.TRANSFORM, data.writeOptions(new CompoundNBT())));
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		data.readOptions(compound);
		init();
	}
}
