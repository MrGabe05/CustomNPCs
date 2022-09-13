package noppes.npcs.client.gui.global;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.containers.ContainerNpcQuestReward;
import noppes.npcs.controllers.data.Quest;

public class GuiNpcQuestReward extends GuiContainerNPCInterface<ContainerNpcQuestReward> implements ITextfieldListener{
	private final Quest quest;
	private final ResourceLocation resource;

    public GuiNpcQuestReward(ContainerNpcQuestReward container, PlayerInventory inv, ITextComponent titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        this.quest = NoppesUtilServer.getEditingQuest(player);
        resource = getResource("questreward.png");
    }
    
    @Override
    public void init(){
        super.init();
        addLabel(new GuiLabel(0,"quest.randomitem", guiLeft + 4, guiTop + 4));
        addButton(new GuiButtonNop(this, 0, guiLeft + 4, guiTop + 14, 60, 20, new String[]{"gui.no", "gui.yes"}, quest.randomReward?1:0));
        
        addButton(new GuiButtonNop(this, 5, guiLeft, guiTop+ imageHeight, 98, 20, "gui.back"));
        
        addLabel(new GuiLabel(1, "quest.exp", guiLeft + 4, guiTop + 45));
        addTextField(new GuiTextFieldNop(0, this, guiLeft + 4, guiTop + 55, 60, 20, quest.rewardExp + ""));
        getTextField(0).numbersOnly = true;
        getTextField(0).setMinMaxDefault(0, 99999, 0);
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton){
		int id = guibutton.id;
        if(id == 5){
        	NoppesUtil.openGUI(player,GuiNPCManageQuest.Instance);
        }
        if(id == 0){
        	quest.randomReward = guibutton.getValue() == 1;
        }
    }

    @Override    
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        super.renderBg(matrixStack, partialTicks, x, y);
    	RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(resource);
        int l = (width - imageWidth) / 2;
        int i1 = (height - imageHeight) / 2;
        blit(matrixStack, l, i1, 0, 0, imageWidth, imageHeight);

    }
    
	@Override
	public void save() {
		
	}
	
	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		quest.rewardExp = textfield.getInteger();
	}
}
