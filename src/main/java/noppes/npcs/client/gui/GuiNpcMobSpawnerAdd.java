package noppes.npcs.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.controllers.ClientCloneController;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCloneNameCheck;
import noppes.npcs.packets.server.SPacketCloneSave;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNpcMobSpawnerAdd extends GuiNPCInterface implements IGuiData {
	
	private final Entity toClone;
	private final CompoundNBT compound;
	private static boolean serverSide = true;
	private static int tab = 1;

	public GuiNpcMobSpawnerAdd(CompoundNBT compound){
		this.toClone = EntityType.create(compound, Minecraft.getInstance().level).orElse(null);
		this.compound = compound;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
		
	}
	@Override
	public void init(){
		super.init();
		String name = toClone.getName().getString();
		addLabel(new GuiLabel(0, "Save as", guiLeft + 4, guiTop + 6));
		addTextField(new GuiTextFieldNop(0, this,  guiLeft + 4, guiTop + 18, 200, 20, name));
		

		addLabel(new GuiLabel(1, "Tab", guiLeft + 10, guiTop + 50));
		addButton(new GuiButtonNop(this, 2, guiLeft + 40, guiTop + 45, 20, 20, new String[]{"1","2","3","4","5","6","7","8","9"}, tab - 1));

		addButton(new GuiButtonNop(this, 3, guiLeft + 4, guiTop + 95, new String[]{"clone.client", "clone.server"}, serverSide?1:0));
		
		addButton(new GuiButtonNop(this, 0, guiLeft + 4, guiTop + 70, 80, 20, "gui.save"));
		addButton(new GuiButtonNop(this, 1, guiLeft + 86, guiTop + 70, 80, 20, "gui.cancel"));
	}
	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
		if(id == 0){
			String name = getTextField(0).getValue();
			if(name.isEmpty())
				return;
			int tab = guibutton.getValue() + 1;
			if(!serverSide){
				if(ClientCloneController.Instance.getCloneData(null, name, tab) != null)
					setScreen(new ConfirmScreen(this::accept, new TranslationTextComponent(""), new TranslationTextComponent("clone.overwrite")));
				else
					accept(true);
			}
			else
				Packets.sendServer(new SPacketCloneNameCheck(name, tab));
		}
		if(id == 1){
			close();
		}
		if(id == 2){
			tab = guibutton.getValue() + 1;
		}
		if(id == 3){
			serverSide = guibutton.getValue() == 1;
		}
	}

    public void accept(boolean confirm){
		if(confirm){
			String name = getTextField(0).getValue();
			if(!serverSide)
				ClientCloneController.Instance.addClone(compound, name, tab);
			else
				Packets.sendServer(new SPacketCloneSave(name, tab));
			close();
		}
		else{
			setScreen(this);
		}
    }

	@Override
	public void save() {
		
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		if(compound.contains("NameExists")){
			if(compound.getBoolean("NameExists"))
				setScreen(new ConfirmScreen(this::accept, new TranslationTextComponent(""),new TranslationTextComponent("clone.overwrite")));
			else
				accept(true);
		}
	}

}
