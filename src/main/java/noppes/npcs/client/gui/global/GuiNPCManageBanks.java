package noppes.npcs.client.gui.global;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.containers.ContainerManageBanks;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketBankGet;
import noppes.npcs.packets.server.SPacketBankRemove;
import noppes.npcs.packets.server.SPacketBankSave;
import noppes.npcs.packets.server.SPacketBanksGet;

public class GuiNPCManageBanks extends GuiContainerNPCInterface2<ContainerManageBanks> implements IScrollData,ICustomScrollListener,ITextfieldListener, IGuiData
{
	private GuiCustomScroll scroll;
	private Map<String,Integer> data = new HashMap<String,Integer>();
	private ContainerManageBanks container;
	private Bank bank = new Bank();
	private String selected = null;

	public GuiNPCManageBanks(ContainerManageBanks container, PlayerInventory inv, ITextComponent titleIn) {
    	super(NoppesUtil.getLastNpc(),container, inv, titleIn);
    	this.container = container;
    	drawDefaultBackground = false;
    	setBackground("npcbanksetup.png");
    	imageHeight = 200;
		Packets.sendServer(new SPacketBanksGet());
    }

    @Override
    public void init(){
        super.init();
        
       	this.addButton(new GuiButtonNop(this, 6,guiLeft + 340, guiTop + 10, 45, 20, "gui.add"));
    	this.addButton(new GuiButtonNop(this, 7,guiLeft + 340, guiTop + 32, 45, 20, "gui.remove"));
    	if(scroll == null) {
			scroll = new GuiCustomScroll(this, 0);
			scroll.setSize(160, 180);
		}
    	scroll.guiLeft = guiLeft + 174;
    	scroll.guiTop = guiTop + 8;
    	addScroll(scroll);
    	
        for(int i = 0; i < 6; i++){
        	int x = guiLeft + 6;
        	int y = (guiTop + 36) + (i * 22);
            addButton(new GuiButtonNop(this, i, x + 50, y,80, 20, new String[]{"bank.canUpgrade","bank.cantUpgrade","bank.upgraded"},0));
            getButton(i).setEnabled(false);
        }
        
        addTextField(new GuiTextFieldNop(0,this, guiLeft+8, guiTop + 8, 160, 16, ""));
        getTextField(0).setMaxLength(20);
        
        addTextField(new GuiTextFieldNop(1,this, guiLeft+10, guiTop + 80, 16, 16, ""));
        getTextField(1).numbersOnly = true;
        getTextField(1).setMaxLength(1);
        
        addTextField(new GuiTextFieldNop(2,this, guiLeft+10, guiTop + 110, 16, 16, ""));
        getTextField(2).numbersOnly = true;
        getTextField(2).setMaxLength(1);
    }
    
    @Override
	public void buttonEvent(GuiButtonNop guibutton)
    {
		GuiButtonNop button = (GuiButtonNop) guibutton;
        if(button.id == 6)
        {
        	save();
        	scroll.clear();
        	String name = "New";
        	while(data.containsKey(name))
        		name += "_";
        	Bank bank = new Bank();
        	bank.name = name;
        	
			CompoundNBT compound = new CompoundNBT();
			bank.addAdditionalSaveData(compound);
			Packets.sendServer(new SPacketBankSave(compound));
        }
        else if(button.id == 7)
        {
        	if(data.containsKey(scroll.getSelected()))
        		Packets.sendServer(new SPacketBankRemove(data.get(selected)));
        }
        else if(button.id >= 0 && button.id < 6)
        {
        	bank.slotTypes.put(button.id, button.getValue());
        }
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int par1, int limbSwingAmount)
    { 
    	font.draw(matrixStack, I18n.get("bank.tabCost"), 23, 28, CustomNpcResourceListener.DefaultTextColor);
    	font.draw(matrixStack, I18n.get("bank.upgCost"), 123, 28, CustomNpcResourceListener.DefaultTextColor);
    	font.draw(matrixStack, I18n.get("gui.start"), 6, 70, CustomNpcResourceListener.DefaultTextColor);
    	font.draw(matrixStack, I18n.get("gui.max"), 9, 100, CustomNpcResourceListener.DefaultTextColor);
    }

	@Override
	public void setGuiData(CompoundNBT compound) {
		Bank bank = new Bank();
		bank.readAdditionalSaveData(compound);
		this.bank = bank;
		
		if (bank.id == -1) {
			getTextField(0).setValue("");
			getTextField(1).setValue("");
			getTextField(2).setValue("");
			
	        for(int i = 0; i < 6; i++)
	        {
	        	getButton(i).setDisplay(0); 
	        	getButton(i).setEnabled(false);
	        }
		} else {			
			getTextField(0).setValue(bank.name);
			getTextField(1).setValue(Integer.toString(bank.startSlots));
			getTextField(2).setValue(Integer.toString(bank.maxSlots));
			
	        for(int i = 0; i < 6; i++)
	        {
	        	int type = 0;
	        	if(bank.slotTypes.containsKey(i))
	        		type = bank.slotTypes.get(i);
	        	getButton(i).setDisplay(type); 
	        	getButton(i).setEnabled(true);
	        }
		}
		setSelected(bank.name);
	}
	
	@Override
	public void setData(Vector<String> list, Map<String, Integer> data) {
		String name = scroll.getSelected();
		this.data = data;
		scroll.setList(list);
		
		if(name != null)
			scroll.setSelected(name);
	}
	
	@Override
	public void setSelected(String selected) {
		this.selected = selected;
		scroll.setSelected(selected);
	}
	
	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll guiCustomScroll) {
		if(guiCustomScroll.id == 0)
		{
			save();
			selected = scroll.getSelected();
			Packets.sendServer(new SPacketBankGet(data.get(selected)));
		}
	}
	
	@Override
	public void save() {
		if(selected != null && data.containsKey(selected) && bank != null){
			CompoundNBT compound = new CompoundNBT();
			bank.currencyInventory = container.bank.currencyInventory;
			bank.upgradeInventory = container.bank.upgradeInventory;
			bank.addAdditionalSaveData(compound);
			Packets.sendServer(new SPacketBankSave(compound));
		}
	}

	@Override
	public void unFocused(GuiTextFieldNop guiNpcTextField) {
		if(bank.id != -1) {
			if(guiNpcTextField.id == 0) {
				String name = guiNpcTextField.getValue();
				if(!name.isEmpty() && !data.containsKey(name)){
					String old = bank.name;
					data.remove(bank.name);
					bank.name = name;
					data.put(bank.name, bank.id);
					selected = name;
					scroll.replace(old,bank.name);
				}
			} else if(guiNpcTextField.id == 1 || guiNpcTextField.id == 2) {
				int num = 1;
				
				if(!guiNpcTextField.isEmpty())
					num = guiNpcTextField.getInteger();
				
		        if(num > 6)
		        	num = 6;
		        
		        if(num < 0)
		        	num = 0;
				
		        if(guiNpcTextField.id == 1) {
		        	bank.startSlots = num;
		        } else if(guiNpcTextField.id == 2) {
			        bank.maxSlots = num;
		        }
		        
		        if(bank.startSlots > bank.maxSlots)
		        	bank.maxSlots = bank.startSlots;
		        
		        guiNpcTextField.setValue(Integer.toString(num));
			} 
		}
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}

}
