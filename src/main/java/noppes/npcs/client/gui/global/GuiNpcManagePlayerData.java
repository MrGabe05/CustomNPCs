package noppes.npcs.client.gui.global;

import java.util.*;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.constants.EnumPlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketPlayerDataGet;
import noppes.npcs.packets.server.SPacketPlayerDataRemove;

public class GuiNpcManagePlayerData extends GuiNPCInterface2 implements IScrollData,ICustomScrollListener
{
	private GuiCustomScroll scroll;
	private String selectedPlayer = null;
	private String selected = null;
	private final Map<String,Integer> data = new HashMap<String,Integer>();
	private EnumPlayerData selection = EnumPlayerData.Players;
	private String search = "";
	
    public GuiNpcManagePlayerData(EntityNPCInterface npc,GuiNPCInterface2 parent){
    	super(npc);
    	Packets.sendServer(new SPacketPlayerDataGet(selection, ""));
    }

    @Override
    public void init(){
        super.init();        
        scroll = new GuiCustomScroll(this,0);
        scroll.setSize(190, 175);
        scroll.guiLeft = guiLeft + 4;
        scroll.guiTop = guiTop + 16;
        addScroll(scroll);
        selected = null;
        
        addLabel(new GuiLabel(0,"playerdata.allPlayers", guiLeft + 10, guiTop + 6));

        this.addButton(new GuiButtonNop(this, 0, guiLeft + 200, guiTop + 10,98, 20, "selectWorld.deleteButton"));
    	this.addButton(new GuiButtonNop(this, 1, guiLeft + 200, guiTop + 32,98, 20, "playerdata.players"));
    	this.addButton(new GuiButtonNop(this, 2, guiLeft + 200, guiTop + 54,98, 20, "quest.quest"));
    	this.addButton(new GuiButtonNop(this, 3, guiLeft + 200, guiTop + 76,98, 20, "dialog.dialog"));
    	this.addButton(new GuiButtonNop(this, 4, guiLeft + 200, guiTop + 98,98, 20, "global.transport"));
    	this.addButton(new GuiButtonNop(this, 5, guiLeft + 200, guiTop + 120,98, 20, "role.bank"));
    	this.addButton(new GuiButtonNop(this, 6, guiLeft + 200, guiTop + 142,98, 20, "menu.factions"));
    	
    	addTextField(new GuiTextFieldNop(0, this, guiLeft + 4, guiTop + 193, 190, 20, search));
    	getTextField(0).enabled = selection == EnumPlayerData.Players;
    	
        initButtons();
    }

    public void initButtons(){
    	getButton(1).setEnabled(selection != EnumPlayerData.Players);
    	getButton(2).setEnabled(selection != EnumPlayerData.Quest);
    	getButton(3).setEnabled(selection != EnumPlayerData.Dialog);
    	getButton(4).setEnabled(selection != EnumPlayerData.Transport);
    	getButton(5).setEnabled(selection != EnumPlayerData.Bank);
    	getButton(6).setEnabled(selection != EnumPlayerData.Factions);
    	
    	if(selection == EnumPlayerData.Players)
    		getLabel(0).setMessage(new TranslationTextComponent("playerdata.allPlayers"));
    	else
    		getLabel(0).setMessage(new TranslationTextComponent("playerdata.selectedPlayer", selectedPlayer));
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        scroll.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double i, double j, int k){
    	if(k == 0 && scroll != null)
    		scroll.mouseClicked(i, j, k);
    	return super.mouseClicked(i, j, k);
    }
    
    @Override
    public boolean charTyped(char c, int i){
    	super.charTyped(c, i);

    	if(selection != EnumPlayerData.Players)
    		return false;
    	
    	if(search.equals(getTextField(0).getValue()))
			return false;

    	search = getTextField(0).getValue().toLowerCase();
    	scroll.setList(getSearchList());
    	return true;
    }

    private List<String> getSearchList(){
    	if(search.isEmpty() || selection != EnumPlayerData.Players)
    		return new ArrayList<String>(this.data.keySet());
    	List<String> list = new ArrayList<String>();
    	for(String name : data.keySet()){
    		if(name.toLowerCase().contains(search))
    			list.add(name);
    	}
    	return list;
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
        if(id == 0){
        	if(selected != null){
				Packets.sendServer(new SPacketPlayerDataRemove(selection, selectedPlayer, data.get(selected)));
        		data.clear();
        	}
        	selected = null;
        }
        if(id >= 1 && id <= 6)
        {
        	if(selectedPlayer == null && id != 1)
        		return;
        	selection = EnumPlayerData.values()[id - 1];
        	initButtons();
        	scroll.clear();
        	data.clear();
        	Packets.sendServer(new SPacketPlayerDataGet(selection, selectedPlayer));
        	selected = null;
        }
    }
	
	@Override
	public void save() {
	}

	@Override
	public void setData(Vector<String> list, Map<String, Integer> data) {
		this.data.putAll(data);
    	scroll.setList(getSearchList());
		if(selection == EnumPlayerData.Players && selectedPlayer != null){
			scroll.setSelected(selectedPlayer);
			selected = selectedPlayer;
		}
	}

	@Override
	public void setSelected(String selected) {
	}

	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll guiCustomScroll) {
		selected = guiCustomScroll.getSelected();
		if(selection == EnumPlayerData.Players)
			selectedPlayer = selected;
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}


}
