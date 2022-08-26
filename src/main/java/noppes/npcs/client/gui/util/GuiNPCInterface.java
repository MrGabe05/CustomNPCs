package noppes.npcs.client.gui.util;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiBasic;

public abstract class GuiNPCInterface extends GuiBasic {
    public EntityNPCInterface npc;
    public GuiNPCInterface(EntityNPCInterface npc){
        super();
        this.npc = npc;
    }
    public GuiNPCInterface(){
        super();
    }

    public void setSubGui(Screen gui) {
        if(gui instanceof GuiNPCInterface){
            ((GuiNPCInterface)gui).npc = npc;
        }
        if(gui instanceof GuiContainerNPCInterface){
            ((GuiContainerNPCInterface)gui).npc = npc;
        }
        super.setSubGui(gui);
    }

    public void drawNpc(int x, int y) {
        drawNpc(npc, x, y, 1, 0);
    }
}
