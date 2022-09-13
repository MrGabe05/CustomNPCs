package noppes.npcs.client.gui.roles;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.roles.JobBard;

public class GuiNpcBard extends GuiNPCInterface2 {	
	private final JobBard job;
	
    public GuiNpcBard(EntityNPCInterface npc){
    	super(npc);    	
    	job = (JobBard) npc.job;
    }

    @Override
    public void init(){
    	super.init();

    	this.addButton(new GuiButtonNop(this, 1, guiLeft + 55, guiTop + 15,20,20, "X"));
        addLabel(new GuiLabel(0,job.song, guiLeft + 80, guiTop + 20));
    	this.addButton(new GuiButtonNop(this, 0, guiLeft + 75, guiTop + 50, "gui.selectSound"));

    	this.addButton(new GuiButtonNop(this, 3, guiLeft + 75, guiTop + 72, new String[]{"bard.jukebox","bard.background"}, job.isStreamer?0:1));


		addLabel(new GuiLabel(6,"bard.loops", guiLeft + 60, guiTop + 120));
		addButton(new GuiButtonNop(this, 6, guiLeft + 160, guiTop + 115, 60, 20, new String[]{"gui.no","gui.yes"}, job.isLooping?1:0));
        
        addLabel(new GuiLabel(2,"bard.ondistance", guiLeft + 60, guiTop + 143));
        addTextField(new GuiTextFieldNop(2,this, guiLeft+160, guiTop + 138, 40, 20, job.minRange + ""));
        getTextField(2).numbersOnly = true;
        getTextField(2).setMinMaxDefault(2, 64, 5);

        addLabel(new GuiLabel(4,"bard.hasoff", guiLeft + 60, guiTop + 166));
        addButton(new GuiButtonNop(this, 4, guiLeft + 160, guiTop + 161, 60, 20, new String[]{"gui.no","gui.yes"}, job.hasOffRange?1:0));
        
        addLabel(new GuiLabel(3,"bard.offdistance", guiLeft + 60, guiTop + 189));
        addTextField(new GuiTextFieldNop(3,this, guiLeft+160, guiTop + 184, 40, 20, job.maxRange + ""));
        getTextField(3).numbersOnly = true;
        getTextField(3).setMinMaxDefault(2, 64, 10);

    	getLabel(3).enabled = job.hasOffRange;
    	getTextField(3).enabled = job.hasOffRange;
    	
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton){
    	GuiButtonNop button = guibutton;
        if(button.id == 0){
        	setSubGui(new GuiSoundSelection(job.song));
        	MusicController.Instance.stopMusic();
        }
        if(button.id == 1){
        	job.song = "";
        	getLabel(0).setMessage(StringTextComponent.EMPTY);
        	MusicController.Instance.stopMusic();
        }
        if(button.id == 3){
        	job.isStreamer = button.getValue() == 0;
        	init();
        }
		if(button.id == 4){
			job.hasOffRange = button.getValue() == 1;
			init();
		}
		if(button.id == 6){
			job.isLooping = button.getValue() == 1;
			init();
		}
	}

    @Override
	public void save() {
    	job.minRange = getTextField(2).getInteger();
    	job.maxRange = getTextField(3).getInteger();
    	
    	if(job.minRange > job.maxRange)
    		job.maxRange = job.minRange;

    	MusicController.Instance.stopMusic();
		Packets.sendServer(new SPacketNpcJobSave(job.save(new CompoundNBT())));
	}

	@Override
	public void subGuiClosed(Screen subgui) {
		GuiSoundSelection gss = (GuiSoundSelection) subgui;
		if(gss.selectedResource != null) {
	    	job.song = gss.selectedResource.toString();
        	getLabel(0).setMessage(new TranslationTextComponent(job.song));
		}
	}


}
