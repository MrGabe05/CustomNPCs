package noppes.npcs.client.gui.player;

import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketPlayerMailDelete;
import noppes.npcs.packets.server.SPacketPlayerMailGet;
import noppes.npcs.packets.server.SPacketPlayerMailOpen;
import noppes.npcs.packets.server.SPacketPlayerMailRead;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiMailbox extends GuiNPCInterface implements IGuiData, ICustomScrollListener {
    
    private GuiCustomScroll scroll;
    private PlayerMailData data;
    private PlayerMail selected;

	public GuiMailbox() {
		super();
        imageWidth = 256;
        setBackground("menubg.png");
        Packets.sendServer(new SPacketPlayerMailGet());
	}
	
	@Override
    public void init(){
        super.init();
        if(scroll == null){
	        scroll = new GuiCustomScroll(this,0);
	        scroll.setSize(165, 186);
        }
        scroll.guiLeft = guiLeft + 4;
        scroll.guiTop = guiTop + 4;
        addScroll(scroll);
        
        String title = I18n.get("mailbox.name");
        int x = (imageWidth - this.font.width(title)) / 2;

        this.addLabel(new GuiLabel(0, title, guiLeft + x, guiTop - 8));

        if(selected != null){
        	this.addLabel(new GuiLabel(3, I18n.get("mailbox.sender") + ":", guiLeft + 170, guiTop + 6));
        	this.addLabel(new GuiLabel(1, selected.sender, guiLeft + 174, guiTop + 18));
        	this.addLabel(new GuiLabel(2, I18n.get("mailbox.timesend",getTimePast()), guiLeft + 174, guiTop + 30));
        }

        this.addButton(new GuiButtonNop(this, 0, guiLeft + 4, guiTop + 192,82,20, "mailbox.read"));
        this.addButton(new GuiButtonNop(this, 1, guiLeft + 88, guiTop + 192,82,20, "selectWorld.deleteButton"));
        getButton(1).setEnabled(selected != null);
    }

    private String getTimePast() {
		if(selected.timePast > 86400000){
			int days = (int) (selected.timePast / 86400000);
			if(days == 1)
				return days + " " + I18n.get("mailbox.day");
			else
				return days + " " + I18n.get("mailbox.days");
		}
		if(selected.timePast > 3600000){
			int hours = (int) (selected.timePast / 3600000);
			if(hours == 1)
				return hours + " " + I18n.get("mailbox.hour");
			else
				return hours + " " + I18n.get("mailbox.hours");
		}
		int minutes = (int) (selected.timePast / 60000);
		if(minutes == 1)
			return minutes + " " + I18n.get("mailbox.minutes");
		else
			return minutes + " " + I18n.get("mailbox.minutes");
	}

	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
    	if(!scroll.hasSelected())
    		return;
    	if(id == 0){
    		GuiMailmanWrite.parent = this;
    		GuiMailmanWrite.mail = selected;
    		Packets.sendServer(new SPacketPlayerMailOpen(selected.time, selected.sender));
    		selected = null;
			scroll.clearSelection();
    	}
    	if(id == 1){
            ConfirmScreen guiyesno = new ConfirmScreen((bo) -> {
				if(bo && selected != null){
					Packets.sendServer(new SPacketPlayerMailDelete(selected.time, selected.sender));
					selected = null;
				}
				NoppesUtil.openGUI(player, GuiMailbox.this);
			}, new TranslationTextComponent(""), new TranslationTextComponent("gui.deleteMessage"));
            setScreen(guiyesno);
    	}
    }
	
    @Override
    public boolean mouseClicked(double i, double j, int k)
    {
    	super.mouseClicked(i, j, k);
    	scroll.mouseClicked(i, j, k);
    	return true;
    }

	@Override
	public void save() {
		
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		PlayerMailData data = new PlayerMailData();
		data.loadNBTData(compound);
		
		List<String> list = new ArrayList<String>();
        Collections.sort(data.playermail, (o1, o2) -> {
            if (o1.time == o2.time)
                return 0;
            return o1.time > o2.time ? -1 : 1;
        });
		for(PlayerMail mail : data.playermail){
			list.add(mail.subject);
		}

		this.data = data;
		scroll.clear();
		selected = null;
		scroll.setUnsortedList(list);
	}
	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll guiCustomScroll) {
		selected = data.playermail.get(guiCustomScroll.getSelectedIndex());
		init();
		
		if(selected != null && !selected.beenRead){
			selected.beenRead = true;
			Packets.sendServer(new SPacketPlayerMailRead(selected.time, selected.sender));
		}
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}

}
