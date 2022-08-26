package noppes.npcs.client.gui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuClose;
import noppes.npcs.packets.server.SPacketNpcDelete;
import noppes.npcs.shared.client.gui.components.GuiMenuTopButton;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiNpcMenu{
	
	private IGuiInterface parent;
	private GuiMenuTopButton[] topButtons = new GuiMenuTopButton[0];

	private int activeMenu;
	private EntityNPCInterface npc;

	public GuiNpcMenu(IGuiInterface parent, int activeMenu, EntityNPCInterface npc) {
		this.parent = parent;
		this.activeMenu = activeMenu;
		this.npc = npc;
	}

    public void initGui(int guiLeft, int guiTop, int width){
		Minecraft mc = Minecraft.getInstance();
        mc.keyboardHandler.setSendRepeatsToGui(true);

        GuiMenuTopButton display = new GuiMenuTopButton(parent,1,guiLeft + 4, guiTop - 17, "menu.display"){
			@Override
			public void onClick(double x, double y){
				save();
				activeMenu = 1;
				CustomNpcs.proxy.openGui(npc, EnumGuiType.MainMenuDisplay);
			}
		};
        GuiMenuTopButton stats = new GuiMenuTopButton(parent,2,display.x + display.getWidth() , guiTop - 17, "menu.stats"){
			@Override
			public void onClick(double x, double y){
				save();
				activeMenu = 2;
				CustomNpcs.proxy.openGui(npc, EnumGuiType.MainMenuStats);
			}
		};
        GuiMenuTopButton ai = new GuiMenuTopButton(parent,3,stats.x + stats.getWidth() , guiTop - 17, "menu.ai"){
			@Override
			public void onClick(double x, double y){
				save();
				activeMenu = 3;
				CustomNpcs.proxy.openGui(npc, EnumGuiType.MainMenuAI);
			}
		};
        GuiMenuTopButton inv = new GuiMenuTopButton(parent,4,ai.x + ai.getWidth() , guiTop - 17,  "menu.inventory"){
        	@Override
        	public void onClick(double x, double y){
				save();
				activeMenu = 4;
				NoppesUtil.requestOpenGUI(EnumGuiType.MainMenuInv);
			}
		};
        GuiMenuTopButton advanced = new GuiMenuTopButton(parent,5,inv.x + inv.getWidth() , guiTop - 17, "menu.advanced"){
			@Override
			public void onClick(double x, double y){
				save();
				activeMenu = 5;
				CustomNpcs.proxy.openGui(npc, EnumGuiType.MainMenuAdvanced);
			}
		};
        GuiMenuTopButton global = new GuiMenuTopButton(parent,6,advanced.x + advanced.getWidth() , guiTop - 17, "menu.global"){
			@Override
			public void onClick(double x, double y){
				save();
				activeMenu = 6;
				CustomNpcs.proxy.openGui(npc, EnumGuiType.MainMenuGlobal);
			}
		};

        GuiMenuTopButton close = new GuiMenuTopButton(parent,0,guiLeft + width - 22, guiTop - 17, "X"){
			@Override
			public void onClick(double x, double y){
				close();
			}
		};
        GuiMenuTopButton delete = new GuiMenuTopButton(parent,66,guiLeft + width - 72, guiTop - 17,"selectWorld.deleteButton"){
			@Override
			public void onClick(double x, double y){
				ConfirmScreen guiyesno = new ConfirmScreen(GuiNpcMenu.this::accept, new TranslationTextComponent(""), new TranslationTextComponent("gui.deleteMessage"));
				mc.setScreen(guiyesno);
			}
		};
        delete.x = close.x - delete.getWidth();
        
        topButtons = new GuiMenuTopButton[]{display,stats,ai,inv,advanced,global,close,delete};
        
        for(GuiMenuTopButton button : topButtons)
        	button.active = button.id == activeMenu;
    }
	private void save() {
    	GuiTextFieldNop.unfocus();
    	parent.save();
	}

	private void close() {
		((Screen)parent).onClose();
		if(npc != null){
			npc.reset();
			Packets.sendServer(new SPacketMenuClose());
		}
	} 

	public boolean mouseClicked(double i, double j, int k) {
        if (k == 0)
        {
        	Minecraft mc = Minecraft.getInstance();
            for (GuiMenuTopButton button : topButtons)
            {
                if (button.mouseClicked(i, j, k))
                {
                    return true;
                }
            }
        }
		return false;
	}


	public void drawElements(MatrixStack matrixStack, FontRenderer font, int i, int j,
							 Minecraft mc, float f) {
        for(GuiMenuTopButton button: topButtons)
        	button.render(matrixStack,i, j, f);
        
	}

    public void accept(boolean flag){
    	Minecraft mc = Minecraft.getInstance();
		if(flag){
			Packets.sendServer(new SPacketNpcDelete());
			mc.setScreen(null);
	        mc.mouseHandler.grabMouse();
		}
		else{
			NoppesUtil.openGUI(mc.player, parent);
		}
    }
}
