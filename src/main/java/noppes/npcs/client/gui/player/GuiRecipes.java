package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.shared.client.gui.components.GuiButtonNextPage;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GuiRecipes extends GuiNPCInterface
{
	private static final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/slot.png");
    private int page = 0;
    private boolean npcRecipes = true;
    private GuiLabel label;
    private GuiButtonNop left, right;
    private List<IRecipe> recipes = new ArrayList<IRecipe>();

    public GuiRecipes(){
        this.imageHeight = 182;
        this.imageWidth = 256;
        setBackground("recipes.png");
		recipes.addAll(RecipeController.instance.anvilRecipes.values());
    }
    @Override
    public void init(){
    	super.init();

    	addLabel(new GuiLabel(0, "Recipe List", guiLeft + 5, guiTop + 5));
    	addLabel(label = new GuiLabel(1, "", guiLeft + 5, guiTop + 168));

        addButton(this.left = new GuiButtonNextPage(this, 1, guiLeft + 150, guiTop + 164, true, (b) -> {
			page++;
			updateButton();
		}));
        addButton(this.right = new GuiButtonNextPage(this, 2, guiLeft + 80, guiTop + 164, false, (b) -> {
			page--;
			updateButton();
		}));
        
        updateButton();
    }
    private void updateButton(){
    	right.visible = right.active = page > 0;
    	left.visible = left.active = page + 1 < MathHelper.ceil(recipes.size() / 4f);
    }
    
    @Override
    public void render(MatrixStack matrixStack, int xMouse, int yMouse, float f){
    	super.render(matrixStack, xMouse, yMouse, f);
    	minecraft.getTextureManager().bind(resource);
		
		label.setMessage(new StringTextComponent(page + 1 + "/" + MathHelper.ceil(recipes.size() / 4f)));
		label.x = guiLeft + (256 - Minecraft.getInstance().font.width(label.getMessage())) / 2;
		for(int i = 0; i < 4; i++){
			int index = i + page * 4;
			if(index >= recipes.size())
				break;
			IRecipe irecipe = recipes.get(index);
			if(irecipe.getResultItem().isEmpty())
				continue;
			int x = guiLeft + 5 + i / 2 * 126;
			int y = guiTop + 15 + i % 2 * 76;
			drawItem(irecipe.getResultItem(), x + 98, y + 28, xMouse, yMouse);
			if(irecipe instanceof RecipeCarpentry){
				RecipeCarpentry recipe = (RecipeCarpentry) irecipe;
				x += (72 - recipe.getRecipeWidth() * 18) / 2;
				y += (72 - recipe.getRecipeHeight() * 18) / 2;
				for(int j = 0; j < recipe.getRecipeWidth(); j++){
					for(int k = 0; k < recipe.getRecipeHeight(); k++){
				    	minecraft.getTextureManager().bind(resource);
				    	RenderSystem.color4f(1, 1, 1, 1);
						blit(matrixStack, x + j * 18, y + k * 18, 0, 0, 18, 18);
				        ItemStack item = recipe.getCraftingItem(j + k * recipe.getRecipeWidth());
				        if(item.isEmpty())
				        	continue;
				        drawItem(item, x + j * 18 + 1, y + k * 18 + 1, xMouse, yMouse);
					}
				}
			}
		}
		for(int i = 0; i < 4; i++){
			int index = i + page * 4;
			if(index >= recipes.size())
				break;
			IRecipe irecipe = recipes.get(index);
			if(irecipe instanceof RecipeCarpentry){
				RecipeCarpentry recipe = (RecipeCarpentry) irecipe;
				if(recipe.getResultItem().isEmpty())
					continue;
				int x = guiLeft + 5 + i / 2 * 126;
				int y = guiTop + 15 + i % 2 * 76;
				drawOverlay(matrixStack, recipe.getResultItem(), x + 98, y + 22, xMouse, yMouse);
				x += (72 - recipe.getRecipeWidth() * 18) / 2;
				y += (72 - recipe.getRecipeHeight() * 18) / 2;
				for(int j = 0; j < recipe.getRecipeWidth(); j++){
					for(int k = 0; k < recipe.getRecipeHeight(); k++){
				        ItemStack item = recipe.getCraftingItem(j + k * recipe.getRecipeWidth());
				        if(item.isEmpty())
				        	continue;
				        drawOverlay(matrixStack, item, x + j * 18 + 1, y + k * 18 + 1, xMouse, yMouse);
					}
				}
			}
		}
    }

    private void drawItem(ItemStack item, int x, int y, int xMouse, int yMouse){
    	RenderSystem.pushMatrix();
    	RenderSystem.enableRescaleNormal();
        //RenderHelper.enableGUIStandardItemLighting();
        itemRenderer.blitOffset = 100.0F;
        itemRenderer.renderAndDecorateItem(item, x, y);
        itemRenderer.renderGuiItemDecorations(font, item, x, y);
        itemRenderer.blitOffset = 0.0F;
        //RenderHelper.disableStandardItemLighting();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }
    
    private void drawOverlay(MatrixStack matrixStack, ItemStack item, int x, int y, int xMouse, int yMouse){
        if (this.func_146978_c(x - guiLeft, y - guiTop, 16, 16, xMouse, yMouse)){
            this.renderTooltip(matrixStack, item, xMouse, yMouse);
        }
    }
    protected boolean func_146978_c(int p_146978_1_, int p_146978_2_, int p_146978_3_, int p_146978_4_, int p_146978_5_, int p_146978_6_)
    {
        int k1 = this.guiLeft;
        int l1 = this.guiTop;
        p_146978_5_ -= k1;
        p_146978_6_ -= l1;
        return p_146978_5_ >= p_146978_1_ - 1 && p_146978_5_ < p_146978_1_ + p_146978_3_ + 1 && p_146978_6_ >= p_146978_2_ - 1 && p_146978_6_ < p_146978_2_ + p_146978_4_ + 1;
    }

	@Override
	public void save() {
	}
}