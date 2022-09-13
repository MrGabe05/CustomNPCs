package micdoodle8.mods.galacticraft.api.client.tabs;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.gui.player.GuiQuestLog;
import noppes.npcs.util.CustomNPCsScheduler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InventoryTabQuests extends AbstractTab {

    public ITextComponent displayString;
	public InventoryTabQuests() {
		super(0, new ItemStack(Items.BOOK));
		displayString = new TranslationTextComponent("quest.quest").append(" (").append(ClientProxy.QuestLog.getKey().getDisplayName()).append(")");
	}

	@Override
	public void onTabClicked() {
        CustomNPCsScheduler.runTack(() -> {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new GuiQuestLog(mc.player));
        });
	}

	@Override
	public boolean shouldAddToList() {
		return true;
	}
	
	@Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (!visible){
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if(hovered){
            matrixStack.translate(mouseX, y + 2, 0);
            drawHoveringText(matrixStack, Collections.singletonList(displayString), -mc.font.width(displayString), 0, mc.font);
            matrixStack.translate(-mouseX, -(y + 2), -0);
        }

    }


    protected void drawHoveringText(MatrixStack matrixStack, List<ITextComponent> list, int x, int y, FontRenderer font){
        if (list.isEmpty())
            return;

        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        int k = 0;

        for (ITextComponent o : list)
        {
            int l = font.width(o);

            if (l > k)
            {
                k = l;
            }
        }

        int k2 = y;
        int i1 = 8;

        if (list.size() > 1) {
            i1 += 2 + (list.size() - 1) * 10;
        }
        int oldBlit = this.getBlitOffset();
        this.setBlitOffset(300);
        int j1 = -267386864;
        this.fillGradient(matrixStack, x - 3, k2 - 4, x + k + 3, k2 - 3, j1, j1);
        this.fillGradient(matrixStack, x - 3, k2 + i1 + 3, x + k + 3, k2 + i1 + 4, j1, j1);
        this.fillGradient(matrixStack, x - 3, k2 - 3, x + k + 3, k2 + i1 + 3, j1, j1);
        this.fillGradient(matrixStack, x - 4, k2 - 3, x - 3, k2 + i1 + 3, j1, j1);
        this.fillGradient(matrixStack, x + k + 3, k2 - 3, x + k + 4, k2 + i1 + 3, j1, j1);
        int k1 = 1347420415;
        int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
        this.fillGradient(matrixStack, x - 3, k2 - 3 + 1, x - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
        this.fillGradient(matrixStack, x + k + 2, k2 - 3 + 1, x + k + 3, k2 + i1 + 3 - 1, k1, l1);
        this.fillGradient(matrixStack, x - 3, k2 - 3, x + k + 3, k2 - 3 + 1, k1, k1);
        this.fillGradient(matrixStack, x - 3, k2 + i1 + 2, x + k + 3, k2 + i1 + 3, l1, l1);

        for (int i2 = 0; i2 < list.size(); ++i2){
            ITextComponent s1 = list.get(i2);
            font.drawShadow(matrixStack, s1, x, k2, -1);

            if (i2 == 0){
                k2 += 2;
            }

            k2 += 10;
        }

        this.setBlitOffset(oldBlit);
        RenderSystem.enableDepthTest();
        RenderSystem.enableRescaleNormal();
    }
}