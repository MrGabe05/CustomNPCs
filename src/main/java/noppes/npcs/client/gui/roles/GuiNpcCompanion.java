package noppes.npcs.client.gui.roles;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionTalents.GuiTalent;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiSliderNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ISliderListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.constants.EnumCompanionStage;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcRoleCompanionUpdate;
import noppes.npcs.packets.server.SPacketNpcRoleSave;
import noppes.npcs.roles.RoleCompanion;

public class GuiNpcCompanion extends GuiNPCInterface2 implements ITextfieldListener, ISliderListener{	
	private RoleCompanion role;
	private List<GuiTalent> talents = new ArrayList<GuiTalent>();

    public GuiNpcCompanion(EntityNPCInterface npc){
    	super(npc);    	
    	role = (RoleCompanion) npc.role;
    }

    @Override
    public void init(){
    	super.init();
    	talents = new ArrayList<GuiTalent>();
    	int y = guiTop + 4;
    	
    	addButton(new GuiButtonNop(this, 0, guiLeft + 70, y, 90, 20, new String[]{EnumCompanionStage.BABY.name, EnumCompanionStage.CHILD.name, EnumCompanionStage.TEEN.name, EnumCompanionStage.ADULT.name, EnumCompanionStage.FULLGROWN.name}, role.stage.ordinal()));
    	addLabel(new GuiLabel(0, "companion.stage", guiLeft + 4, y + 5));
    	addButton(new GuiButtonNop(this, 1, guiLeft + 162, y, 90, 20, "gui.update"));
    	
    	addButton(new GuiButtonNop(this, 2, guiLeft + 70, y += 22, 90, 20, new String[]{"gui.no", "gui.yes"}, role.canAge?1:0));
    	addLabel(new GuiLabel(2, "companion.age", guiLeft + 4, y + 5));
    	if(role.canAge){
	    	addTextField(new GuiTextFieldNop(2, this, guiLeft + 162, y, 140, 20, role.ticksActive + ""));
	    	getTextField(2).numbersOnly = true;
	    	getTextField(2).setMinMaxDefault(0, Integer.MAX_VALUE, 0);
    	}

    	talents.add(new GuiTalent(role, EnumCompanionTalent.INVENTORY, guiLeft + 4, y+=26));
    	addSlider(new GuiSliderNop(this, 10, guiLeft + 30, y + 2, 100, 20, role.getExp(EnumCompanionTalent.INVENTORY)/5000f));
    	
    	talents.add(new GuiTalent(role, EnumCompanionTalent.ARMOR, guiLeft + 4, y+=26));
    	addSlider(new GuiSliderNop(this, 11, guiLeft + 30, y + 2, 100, 20, role.getExp(EnumCompanionTalent.ARMOR)/5000f));
    	
    	talents.add(new GuiTalent(role, EnumCompanionTalent.SWORD, guiLeft + 4, y+=26));
    	addSlider(new GuiSliderNop(this, 12, guiLeft + 30, y + 2, 100, 20, role.getExp(EnumCompanionTalent.SWORD)/5000f));
    	
    	for(GuiTalent gui : talents){
    		gui.init(minecraft, width, height);
    	}
    	
//    	talents.add(new GuiTalent(role, EnumCompanionTalent.RANGED, guiLeft + 4, y+=26));
//    	addSlider(new GuiNpcSlider(this, 13, guiLeft + 30, y + 2, 100, 20, role.getExp(EnumCompanionTalent.RANGED)/5000f));
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton){
    	if(guibutton.id == 0){
        	GuiButtonNop button = (GuiButtonNop) guibutton;
    		role.matureTo(EnumCompanionStage.values()[button.getValue()]);
    		if(role.canAge)
    			role.ticksActive = role.stage.matureAge;
    		init();
    	}
    	if(guibutton.id == 1){
    		Packets.sendServer(new SPacketNpcRoleCompanionUpdate(role.stage));
    	}
    	if(guibutton.id == 2){
        	GuiButtonNop button = (GuiButtonNop) guibutton;
    		role.canAge = button.getValue() == 1;
    		init();
    	}
    }

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		if(textfield.id == 2){
			role.ticksActive = textfield.getInteger();
		}
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		for(GuiTalent talent : new ArrayList<GuiTalent>(talents)){
			talent.render(matrixStack, mouseX, mouseY, partialTicks);
		}
	}
    
    @Override
    public void elementClicked(){
    	
    }

    @Override
	public void save() {
		Packets.sendServer(new SPacketNpcRoleSave(role.save(new CompoundNBT())));
	}

	@Override
	public void mouseDragged(GuiSliderNop slider) {
		if(slider.sliderValue <= 0){
			slider.setMessage(new TranslationTextComponent("gui.disabled"));
			role.talents.remove(EnumCompanionTalent.values()[slider.id - 10]);
		}
		else{
			slider.setMessage(new TranslationTextComponent((int)(slider.sliderValue * 50) * 100 + " exp"));
			role.setExp(EnumCompanionTalent.values()[slider.id - 10], (int)(slider.sliderValue * 50) * 100);
		}
	}

	@Override
	public void mousePressed(GuiSliderNop slider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(GuiSliderNop slider) {
		// TODO Auto-generated method stub
		
	}


}
