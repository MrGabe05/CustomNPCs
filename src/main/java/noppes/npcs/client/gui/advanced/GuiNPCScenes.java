package noppes.npcs.client.gui.advanced;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.client.gui.GuiTextAreaScreen;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.entity.data.DataScenes.SceneContainer;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class GuiNPCScenes extends GuiNPCInterface2 {
	private final DataScenes scenes;
	private SceneContainer scene;
	
    public GuiNPCScenes(EntityNPCInterface npc){
    	super(npc);
    	this.scenes = npc.advanced.scenes;
    }

    @Override
    public void init(){
        super.init();
        addLabel(new GuiLabel(102, "gui.button", guiLeft + 236, guiTop + 4));
        int y = guiTop + 14;
        for(int i = 0; i < scenes.scenes.size(); i++){
        	SceneContainer scene = scenes.scenes.get(i);
            this.addLabel(new GuiLabel(0 + i * 10, scene.name, guiLeft + 10, y + 5));
           	this.addButton(new GuiButtonNop(this, 1 + i * 10, guiLeft + 120, y, 60, 20, new String[]{"gui.disabled", "gui.enabled"}, scene.enabled?1:0));
        	this.addButton(new GuiButtonNop(this, 2 + i * 10, guiLeft + 181, y, 50, 20, "selectServer.edit"));
        	this.addButton(new GuiButtonNop(this, 3 + i * 10, guiLeft + 293, y, 50, 20, "X"));
        	if(CustomNpcs.SceneButtonsEnabled){
        		this.addButton(new GuiButtonNop(this, 4 + i * 10, guiLeft + 232, y, 60, 20, new String[]{"gui.none", ClientProxy.Scene1.getName(), ClientProxy.Scene2.getName(), ClientProxy.Scene3.getName()}, scene.btn));
			}
        	y += 22;
        }
        if(scenes.scenes.size() < 6){
        	this.addTextField(new GuiTextFieldNop(101, this, guiLeft + 4, y + 10, 190, 20, "Scene" + (scenes.scenes.size() + 1)));
        	this.addButton(new GuiButtonNop(this, 101 , guiLeft + 204, y + 10, 60, 20, "gui.add"));
        }
        
    }

    @Override
	public void buttonEvent(GuiButtonNop button){
    	if(button.id < 60){
        	SceneContainer scene = scenes.scenes.get(button.id / 10);
	        if(button.id % 10 == 1){
	        	scene.enabled = button.getValue() == 1;
	        }
	        if(button.id % 10 == 2){
				this.scene = scene;
	        	this.setSubGui(new GuiTextAreaScreen(scene.lines));
	        }
	        if(button.id % 10 == 3){
	        	scenes.scenes.remove(scene);
	        	init();
	        }
	        if(button.id % 10 == 4){
	        	scene.btn = button.getValue();
	        	init();
	        }
    	}
        if(button.id == 101){
        	scenes.addScene(getTextField(101).getValue());
    		init();
        }
        
    }

	@Override
	public void subGuiClosed(Screen gui) {
		if(gui instanceof GuiTextAreaScreen){
			scene.lines = ((GuiTextAreaScreen)gui).text;
			scene = null;
		}
	}

	@Override
	public void save() {
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.ADVANCED, npc.advanced.save(new CompoundNBT())));
	}
	
}
