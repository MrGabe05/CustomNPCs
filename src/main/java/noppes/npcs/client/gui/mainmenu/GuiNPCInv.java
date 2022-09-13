package noppes.npcs.client.gui.mainmenu;

import java.util.HashMap;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface2;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiSliderNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ISliderListener;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.containers.ContainerNPCInv;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;

public class GuiNPCInv extends GuiContainerNPCInterface2<ContainerNPCInv> implements ISliderListener, IGuiData{
	private final HashMap<Integer,Integer> chances = new HashMap<Integer, Integer>();
	private final ContainerNPCInv container;
	private final ResourceLocation slot;

	public GuiNPCInv(ContainerNPCInv container, PlayerInventory inv, ITextComponent titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn,4);
        this.setBackground("npcinv.png");
        this.container = container;
        imageHeight = 200;
        slot = getResource("slot.png");
		Packets.sendServer(new SPacketMenuGet(EnumMenuType.INVENTORY));
    }

    @Override
    public void init(){
        super.init();
        addLabel(new GuiLabel(0,"inv.minExp", guiLeft + 118, guiTop + 18));
        addTextField(new GuiTextFieldNop(0,this,  guiLeft + 108, guiTop + 29, 60, 20, npc.inventory.getExpMin() + ""));
        getTextField(0).numbersOnly = true;
        getTextField(0).setMinMaxDefault(0, Short.MAX_VALUE, 0);
        
        addLabel(new GuiLabel(1,"inv.maxExp", guiLeft + 118, guiTop + 52));
        addTextField(new GuiTextFieldNop(1,this,  guiLeft + 108, guiTop + 63, 60, 20, npc.inventory.getExpMax() + ""));
        getTextField(1).numbersOnly = true;
        getTextField(1).setMinMaxDefault(0, Short.MAX_VALUE, 0);    
        
        addButton(new GuiButtonNop(this, 10, guiLeft + 88, guiTop + 88, 80, 20, new String[]{"stats.normal", "inv.auto"}, npc.inventory.lootMode));

        addLabel(new GuiLabel(2,"inv.npcInventory", guiLeft + 191, guiTop + 5));
        addLabel(new GuiLabel(3,"inv.inventory", guiLeft + 8, guiTop + 101));
        
        for(int i = 0; i < 9; i++){
        	int chance = 100;
        	if(npc.inventory.dropchance.containsKey(i)){
        		chance = npc.inventory.dropchance.get(i);
        	}
        	if(chance <= 0 || chance > 100)
        		chance = 100;
        	chances.put(i, chance);
            addSlider(new GuiSliderNop(this, i, guiLeft + 211, guiTop + 14 + i * 21, ((float)chance)/100));
        }
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton) {
    	if(guibutton.id == 10){
    		npc.inventory.lootMode = guibutton.getValue();
    	}
    }

    @Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		super.renderBg(matrixStack, partialTicks, x, y);
    	RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(slot);
    	
    	for(int id = 4; id <= 6;id++){
        	Slot slot = container.getSlot(id);
        	if(slot.hasItem())
				blit(matrixStack, guiLeft + slot.x - 1, guiTop + slot.y -1, 0, 0, 18, 18);
    	}
    }
    
	@Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
    	int showname = npc.display.getShowName();
    	npc.display.setShowName(1);
        drawNpc(50, 84);
    	npc.display.setShowName(showname);
    }
    
	@Override
	public void save() {
        npc.inventory.dropchance = chances;
    	npc.inventory.setExp(getTextField(0).getInteger(), getTextField(1).getInteger());
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.INVENTORY, npc.inventory.save(new CompoundNBT())));
	}
	
	@Override
	public void setGuiData(CompoundNBT compound) {
		npc.inventory.load(compound);
		init();
	}
	
	@Override
	public void mouseDragged(GuiSliderNop guiNpcSlider) {
		guiNpcSlider.setMessage(new TranslationTextComponent("inv.dropChance").append(": "  + (int) (guiNpcSlider.sliderValue * 100) + "%"));
	}
	
	@Override
	public void mousePressed(GuiSliderNop guiNpcSlider) {
	}
	
	@Override
	public void mouseReleased(GuiSliderNop guiNpcSlider) {
		chances.put(guiNpcSlider.id, (int) (guiNpcSlider.sliderValue * 100));
	}
}
