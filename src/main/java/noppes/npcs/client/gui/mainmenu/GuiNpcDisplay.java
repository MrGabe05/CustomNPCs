package noppes.npcs.client.gui.mainmenu;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.GuiNpcTextureCloaks;
import noppes.npcs.client.gui.GuiNpcTextureOverlays;
import noppes.npcs.client.gui.SubGuiNpcAvailability;
import noppes.npcs.client.gui.SubGuiNpcName;
import noppes.npcs.client.gui.model.GuiCreationParts;
import noppes.npcs.client.gui.select.GuiTextureSelection;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataDisplay;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.packets.server.SPacketNpRandomNameSet;
import noppes.npcs.shared.client.gui.components.GuiButtonBiDirectional;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNpcDisplay extends GuiNPCInterface2 implements ITextfieldListener, IGuiData {

	private final DataDisplay display;
	
	public GuiNpcDisplay(EntityNPCInterface npc) {
		super(npc,1);
		display = npc.display;
		Packets.sendServer(new SPacketMenuGet(EnumMenuType.DISPLAY));
	}

	@Override
    public void init(){
        super.init();
        int y = guiTop + 4;
        addLabel(new GuiLabel(0,"gui.name", guiLeft + 5, y + 5));
        addTextField(new GuiTextFieldNop(0,this,  guiLeft + 50, y, 206, 20, display.getName()));
    	this.addButton(new GuiButtonNop(this, 0, guiLeft + 253+52, y , 110, 20, new String[]{"display.show","display.hide","display.showAttacking"} ,display.getShowName()));
    	
    	this.addButton(new GuiButtonNop(this, 14, guiLeft + 259, y , 20, 20, Character.toString('\u21bb')));
    	this.addButton(new GuiButtonNop(this, 15, guiLeft + 259 + 22, y , 20, 20, Character.toString('\u22EE')));
    	
    	y+=23;
        addLabel(new GuiLabel(11,"gui.title", guiLeft + 5, y + 5));
        addTextField(new GuiTextFieldNop(11,this,  guiLeft + 50, y, 186, 20, display.getTitle()));

    	y+=23;
        addLabel(new GuiLabel(1,"display.model", guiLeft + 5, y + 5));
    	this.addButton(new GuiButtonNop(this, 1, guiLeft + 50, y,110,20, "selectServer.edit"));
    	addLabel(new GuiLabel(2,"display.size", guiLeft + 175, y + 5));
        addTextField(new GuiTextFieldNop(2,this,  guiLeft + 203, y, 40, 20, display.getSize() + ""));
        getTextField(2).numbersOnly = true;
        getTextField(2).setMinMaxDefault(1, 30, 5);
        addLabel(new GuiLabel(3,"(1-30)", guiLeft + 246 , y + 5));

    	y+=23;
    	addLabel(new GuiLabel(4,"display.texture", guiLeft + 5, y + 5));
        addTextField(new GuiTextFieldNop(3,this,  guiLeft + 80, y, 200, 20, display.skinType == 0?display.getSkinTexture():display.getSkinUrl()));
    	this.addButton(new GuiButtonNop(this, 3, guiLeft + 325, y, 38, 20, "mco.template.button.select"));
    	this.addButton(new GuiButtonNop(this, 2, guiLeft + 283, y, 40, 20, new String[]{"display.texture","display.player", "display.url"},display.skinType));
    	getButton(3).setEnabled(display.skinType == 0);
    	if(display.skinType == 1 && !display.getSkinPlayer().isEmpty())
    		getTextField(3).setValue(display.getSkinPlayer());

    	y+=23;
    	addLabel(new GuiLabel(8,"display.cape", guiLeft + 5, y + 5));
        addTextField(new GuiTextFieldNop(8,this,  guiLeft + 80, y, 200, 20, display.getCapeTexture()));
    	this.addButton(new GuiButtonNop(this, 8, guiLeft + 283, y, 80, 20, "display.selectTexture"));

    	y+=23;
    	addLabel(new GuiLabel(9,"display.overlay", guiLeft + 5, y + 5));
        addTextField(new GuiTextFieldNop(9,this,  guiLeft + 80, y, 200, 20, display.getOverlayTexture()));
    	this.addButton(new GuiButtonNop(this, 9, guiLeft + 283, y, 80, 20, "display.selectTexture"));

    	y+=23;
    	addLabel(new GuiLabel(5,"display.livingAnimation", guiLeft + 5, y + 5));
    	this.addButton(new GuiButtonNop(this, 5, guiLeft + 120, y, 50, 20, new String[]{"gui.yes","gui.no"},display.getHasLivingAnimation()?0:1));

    	addLabel(new GuiLabel(6,"display.tint", guiLeft + 180, y + 5));
    	String color = Integer.toHexString(display.getTint());
    	while(color.length() < 6)
    		color = "0" + color;
    	this.addTextField(new GuiTextFieldNop(6, this, guiLeft + 220, y, 60, 20, color));
    	getTextField(6).setTextColor(display.getTint());
    	
    	y+=23;
    	addLabel(new GuiLabel(7,"display.visible", guiLeft + 5, y + 5));
    	this.addButton(new GuiButtonNop(this, 7, guiLeft + 40, y, 50, 20, new String[]{"gui.yes","gui.no","gui.partly"}, display.getVisible()));
		this.addButton(new GuiButtonNop(this, 16, guiLeft + 92, y, 78, 20, "availability.name"));
    	addLabel(new GuiLabel(13,"display.hitbox", guiLeft + 180, y + 5));
    	this.addButton(new GuiButtonBiDirectional(this, 13, guiLeft + 230, y, 100, 20, display.getHitboxState(), "stats.normal", "gui.none", "hair.solid"));
    
    	y+=23;
    	addLabel(new GuiLabel(10,"display.bossbar", guiLeft + 5, y + 5));
    	this.addButton(new GuiButtonNop(this, 10, guiLeft + 60, y, 110, 20, new String[]{"display.hide","display.show","display.showAttacking"}, display.getBossbar()));
    	addLabel(new GuiLabel(12,"gui.color", guiLeft + 180, y + 5));
    	this.addButton(new GuiButtonBiDirectional(this, 12, guiLeft + 230, y, 100, 20, display.getBossColor(), "color.pink","color.blue","color.red","color.green","color.yellow","color.purple","color.white"));
    
    	//addExtra(new GuiHoverText(0, "testing", guiLeft, guiTop));
    }

	@Override
	public void unFocused(GuiTextFieldNop textfield){
		if(textfield.id == 0){
			if(!textfield.isEmpty())
				display.setName(textfield.getValue());
			else
				textfield.setValue(display.getName());
		}
		else if(textfield.id == 2){
			display.setSize(textfield.getInteger());
		}
		else if(textfield.id == 3){
			if(display.skinType == 2)
				display.setSkinUrl(textfield.getValue());
			else if(display.skinType == 1)
				display.setSkinPlayer(textfield.getValue());
			else
				display.setSkinTexture(textfield.getValue());
		}
		else if(textfield.id == 6){
			int color = 0;
			try{
				color = Integer.parseInt(textfield.getValue(),16);
			}
			catch(NumberFormatException e){
				color = 0xFFFFFF;
			}
	    	display.setTint(color);
	    	textfield.setTextColor(display.getTint());
		}
		else if(textfield.id == 8){
			display.setCapeTexture(textfield.getValue());
		}
		else if(textfield.id == 9){
			display.setOverlayTexture(textfield.getValue());
		}
		else if(textfield.id == 11){
			display.setTitle(textfield.getValue());
		}
	}
	public void buttonEvent(GuiButtonNop guibutton){
		GuiButtonNop button = guibutton;
		if(button.id == 0){
			display.setShowName(button.getValue());
		}
		if(button.id == 1){
			NoppesUtil.openGUI(player, new GuiCreationParts(npc));
			//NoppesUtil.openGUI(player, new GuiNpcModelSelection(npc,this));
		}
		if(button.id == 2){
			display.setSkinUrl("");
			display.setSkinPlayer(null);
			display.skinType = (byte) button.getValue();
			init();
		}
		else if(button.id == 3){
			setSubGui(new GuiTextureSelection(npc, npc.display.getSkinTexture()));
		}
		else if(button.id == 5){
			display.setHasLivingAnimation(button.getValue() == 0);
		}
		else if(button.id == 7){
			display.setVisible(button.getValue());
		}
		else if(button.id == 8){
			NoppesUtil.openGUI(player, new GuiNpcTextureCloaks(npc, this));
		}
		else if(button.id == 9){
			NoppesUtil.openGUI(player, new GuiNpcTextureOverlays(npc, this));
		}
		else if(button.id == 10){
			display.setBossbar(button.getValue());
		}
		else if(button.id == 12){
			display.setBossColor(button.getValue());
		}
		else if(button.id == 13){
			display.setHitboxState((byte)button.getValue());
		}
		else if(button.id == 14){
			Packets.sendServer(new SPacketNpRandomNameSet(display.getMarkovGeneratorId(), display.getMarkovGender()));
		}
		else if(button.id == 15){
			setSubGui(new SubGuiNpcName(display));
		}
		else if(button.id == 16){
			setSubGui(new SubGuiNpcAvailability(display.availability));
		}
    }

	@Override
	public void subGuiClosed(Screen subgui) {
		init();
	}

	@Override
	public void save() {
		if(display.skinType == 1)
			display.loadProfile();
		npc.textureLocation = null;
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.DISPLAY, display.save(new CompoundNBT())));
		
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		display.readToNBT(compound);
		init();
	}

}
