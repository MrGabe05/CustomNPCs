package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.listeners.ITopButtonListener;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketQuestCompletionCheck;

public class GuiQuestCompletion extends GuiNPCInterface implements ITopButtonListener{

    private final IQuest quest;
	private final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/smallbg.png");

	public GuiQuestCompletion(IQuest quest) {
        imageWidth = 176;
        imageHeight = 222;
        this.quest = quest;
        this.drawDefaultBackground = false;
        title = "";
        closeOnEsc = false;
	}
	
    @Override
    public void init(){
        super.init();

        String questTitle = I18n.get(quest.getName());
        int left = (imageWidth - this.font.width(questTitle)) / 2;
        this.addLabel(new GuiLabel(0, questTitle, guiLeft + left, guiTop + 4));
        
        this.addButton(new GuiButtonNop(this, 0, guiLeft + 38, guiTop + imageHeight - 24, 100, 20, I18n.get("quest.complete")));
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    	renderBackground(matrixStack);
    	RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(resource);
        blit(matrixStack, guiLeft, guiTop, 0, 0, imageWidth, imageHeight);
        hLine(matrixStack, guiLeft + 4, guiLeft + 170, guiTop + 13,  + 0xFF000000 + CustomNpcResourceListener.DefaultTextColor);
        
        
        drawQuestText(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    private void drawQuestText(MatrixStack matrixStack){
        int xoffset = guiLeft + 4;
    	TextBlockClient block = new TextBlockClient(quest.getCompleteText(), 172, true, player);
        int yoffset = guiTop + 20; 
    	for(int i = 0; i < block.lines.size(); i++){
    		String text = block.lines.get(i).getString();
    		font.draw(matrixStack, text, guiLeft + 4, guiTop + 16 + (i * font.lineHeight), CustomNpcResourceListener.DefaultTextColor);
    	}
    }
    
    @Override
	public void buttonEvent(GuiButtonNop guibutton){
		if(guibutton.id == 0){
            Packets.sendServer(new SPacketQuestCompletionCheck(quest.getId()));
			close();
		}
    }
    
	@Override
	public void save() {
	}

}
