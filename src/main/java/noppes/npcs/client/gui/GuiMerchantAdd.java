package noppes.npcs.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.containers.ContainerManageRecipes;

@OnlyIn(Dist.CLIENT)
public class GuiMerchantAdd extends ContainerScreen<ContainerManageRecipes> implements IGuiInterface
{
//    private static final ResourceLocation merchantGuiTextures = new ResourceLocation("textures/gui/container/villager.png");
//
//    /** Instance of IMerchant interface. */
//    private IMerchant theIMerchant;
//    private MerchantButton nextRecipeButtonIndex;
//    private MerchantButton previousRecipeButtonIndex;
//    private int currentRecipeIndex;
//    private String field_94082_v;


    public GuiMerchantAdd(ContainerManageRecipes container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        //this.theIMerchant = ServerEventsHandler.Merchant;
        //this.field_94082_v = I18n.get("entity.Villager.name");
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {

    }

    @Override
    public void buttonEvent(GuiButtonNop button) {

    }

    @Override
    public void save() {

    }

    @Override
    public boolean hasSubGui() {
        return false;
    }

    @Override
    public Screen getSubGui() {
        return null;
    }

//
//    @Override
//    public void init() {
//        super.init();
//
//        int i = (this.width - this.xSize) / 2;
//        int j = (this.height - this.ySize) / 2;
//        this.buttons.add(this.nextRecipeButtonIndex = new MerchantButton(1, i + 120 + 27, j + 24 - 1, true));
//        this.buttons.add(this.previousRecipeButtonIndex = new MerchantButton(2, i + 36 - 19, j + 24 - 1, false));
//        this.buttons.add(new GuiNpcButton(this, 4, i + imageWidth,  j + 20, 60, 20, "gui.remove"));
//        this.buttons.add(new GuiNpcButton(this, 5, i + imageWidth,  j + 50, 60, 20, "gui.add"));
//        this.nextRecipeButtonIndex.enabled = false;
//        this.previousRecipeButtonIndex.enabled = false;
//    }
//
//    /**
//     * Draw the foreground layer for the ContainerScreen (everything in front of the items)
//     */
//    protected void renderLabels(MatrixStack matrixStack, int par1, int limbSwingAmount)
//    {
//        this.font.draw(matrixStack, this.field_94082_v, this.xSize / 2 - this.font.width(this.field_94082_v) / 2, 6, CustomNpcResourceListener.DefaultTextColor);
//        this.font.draw(I18n.get("container.inventory"), 8, this.ySize - 96 + 2, CustomNpcResourceListener.DefaultTextColor);
//    }
//
//    /**
//     * Called from the main game loop to update the screen.
//     */
//    public void tick()
//    {
//        super.tick();
//        Minecraft mc = Minecraft.getInstance();
//        MerchantRecipeList merchantrecipelist = this.theIMerchant.getRecipes(mc.player);
//
//        if (merchantrecipelist != null)
//        {
//            this.nextRecipeButtonIndex.enabled = this.currentRecipeIndex < merchantrecipelist.size() - 1;
//            this.previousRecipeButtonIndex.enabled = this.currentRecipeIndex > 0;
//        }
//    }
//
//    /**
//     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
//     */
//    @Override
//    public void buttonEvent(GuiNpcButton par1GuiButton)
//    {
//        boolean flag = false;
//        Minecraft mc = Minecraft.getInstance();
//
//        if (par1GuiButton == this.nextRecipeButtonIndex)
//        {
//            ++this.currentRecipeIndex;
//            flag = true;
//        }
//        else if (par1GuiButton == this.previousRecipeButtonIndex)
//        {
//            --this.currentRecipeIndex;
//            flag = true;
//        }
//
//        if(par1GuiButton.id == 4){
//            MerchantRecipeList merchantrecipelist = this.theIMerchant.getRecipes(mc.player);
//            if(currentRecipeIndex < merchantrecipelist.size()){
//            	merchantrecipelist.remove(currentRecipeIndex);
//            	if(currentRecipeIndex > 0)
//            		currentRecipeIndex--;
//            	Packets.sendServer(new SPacketMerchantUpdate(ServerEventsHandler.Merchant.getId(), merchantrecipelist));
//            }
//        }
//
//        if(par1GuiButton.id == 5){
//        	ItemStack item1 = this.slots.getSlot(0).getStack();
//        	ItemStack item2 = this.slots.getSlot(1).getStack();
//        	ItemStack sold = this.slots.getSlot(2).getStack();
//        	if(item1 == null && item2 != null){
//        		item1 = item2;
//        		item2 = null;
//        	}
//
//        	if(item1 != null && sold != null){
//        		item1 = item1.copy();
//    			sold = sold.copy();
//        		if(item2 != null)
//        			item2 = item2.copy();
//        		MerchantRecipe recipe = new MerchantRecipe(item1, item2, sold);
//        		recipe.increaseMaxTradeUses(Integer.MAX_VALUE - 8);
//
//                MerchantRecipeList merchantrecipelist = this.theIMerchant.getRecipes(mc.player);
//                merchantrecipelist.add(recipe);
//            	Packets.sendServer(new SPacketMerchantUpdate(ServerEventsHandler.Merchant.getId(), merchantrecipelist));
//        	}
//
//        }
//
//        if (flag)
//        {
//            ((ContainerMerchantAdd)this.slots).setCurrentRecipeIndex(this.currentRecipeIndex);
//            PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
//        	packetbuffer.writeInt(this.currentRecipeIndex);
//            this.minecraft.getConnection().send(new CPacketSelectTrade(this.currentRecipeIndex));
//        }
//    }
//
//    @Override
//    public void save() {
//
//    }
//
//    @Override
//    public void closeGui() {
//        this.close();
//    }
//
//    @Override
//    public boolean hasSubGui() {
//        return false;
//    }
//
//    /**
//     * Draw the background layer for the ContainerScreen (everything behind the items)
//     */
//	@Override
//    protected void renderBg(float par1, int limbSwingAmount, int par3)
//    {
//        Minecraft mc = Minecraft.getInstance();
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//        minecraft.getTextureManager().bind(merchantGuiTextures);
//        int k = (this.width - this.xSize) / 2;
//        int l = (this.height - this.ySize) / 2;
//        this.fillGradient(matrixStack, k, l, 0, 0, this.xSize, this.ySize);
//        MerchantRecipeList merchantrecipelist = this.theIMerchant.getRecipes(mc.player);
//
//        if (merchantrecipelist != null && !merchantrecipelist.isEmpty())
//        {
//            int i1 = this.currentRecipeIndex;
//            MerchantRecipe merchantrecipe = merchantrecipelist.get(i1);
//
//            if (merchantrecipe.isRecipeDisabled())
//            {
//                minecraft.getTextureManager().bind(merchantGuiTextures);
//                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//                RenderSystem.disableLighting();
//                this.fillGradient(matrixStack, this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
//                this.fillGradient(matrixStack, this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
//            }
//        }
//    }
//
//    /**
//     * Draws the screen and all the components in it.
//     */
//	@Override
//    public void render(int par1, int limbSwingAmount, float par3)
//    {
//        super.render(par1, limbSwingAmount, par3);
//        Minecraft mc = Minecraft.getInstance();
//        MerchantRecipeList merchantrecipelist = this.theIMerchant.getRecipes(mc.player);
//
//        if (merchantrecipelist != null && !merchantrecipelist.isEmpty())
//        {
//            int k = (this.width - this.xSize) / 2;
//            int l = (this.height - this.ySize) / 2;
//            int i1 = this.currentRecipeIndex;
//            MerchantRecipe merchantrecipe = merchantrecipelist.get(i1);
//            RenderSystem.pushMatrix();
//            ItemStack itemstack = merchantrecipe.getItemToBuy();
//            ItemStack itemstack1 = merchantrecipe.getSecondItemToBuy();
//            ItemStack itemstack2 = merchantrecipe.getItemToSell();
//            RenderHelper.enableGUIStandardItemLighting();
//            RenderSystem.enableRescaleNormal();
//            RenderSystem.enableColorMaterial();
//            RenderSystem.enableLighting();
//            itemRenderer.blitOffset = 100.0F;
//            itemRenderer.renderAndDecorateItem(itemstack, k + 36, l + 24);
//            itemRenderer.renderGuiItemDecorations(this.font, itemstack, k + 36, l + 24);
//
//            if (itemstack1 != null)
//            {
//            	itemRenderer.renderAndDecorateItem(itemstack1, k + 62, l + 24);
//            	itemRenderer.renderGuiItemDecorations(this.font, itemstack1, k + 62, l + 24);
//            }
//
//            itemRenderer.renderAndDecorateItem(itemstack2, k + 120, l + 24);
//            itemRenderer.renderGuiItemDecorations(this.font, itemstack2, k + 120, l + 24);
//            itemRenderer.blitOffset = 0.0F;
//            RenderSystem.disableLighting();
//
//            if (this.isHovering(36, 24, 16, 16, par1, limbSwingAmount))
//            {
//                this.renderTooltip(matrixStack, itemstack, par1, limbSwingAmount);
//            }
//            else if (itemstack1 != null && this.isHovering(62, 24, 16, 16, par1, limbSwingAmount))
//            {
//                this.renderTooltip(matrixStack, itemstack1, par1, limbSwingAmount);
//            }
//            else if (this.isHovering(120, 24, 16, 16, par1, limbSwingAmount))
//            {
//                this.renderTooltip(matrixStack, itemstack2, par1, limbSwingAmount);
//            }
//
//            RenderSystem.popMatrix();
//            RenderSystem.enableLighting();
//            RenderSystem.enableDepthTest();
//            //RenderHelper.enableStandardItemLighting();
//        }
//    }
//
//    /**
//     * Gets the Instance of IMerchant interface.
//     */
//    public IMerchant getIMerchant()
//    {
//        return this.theIMerchant;
//    }
//
//    static ResourceLocation func_110417_h()
//    {
//        return merchantGuiTextures;
//    }
//    @OnlyIn(Dist.CLIENT)
//    static class MerchantButton extends GuiButton
//        {
//            private final boolean field_146157_o;
//            private static final String __OBFID = "CL_00000763";
//
//            public MerchantButton(int par1, int limbSwingAmount, int par3, boolean par4)
//            {
//                super(par1, limbSwingAmount, par3, 12, 19, "");
//                this.field_146157_o = par4;
//            }
//
//            @Override
//            public void render(int p_146112_2_, int p_146112_3_, float partialTicks)
//            {
//                if (this.visible)
//                {
//                    Minecraft.getInstance().getTextureManager().bind(GuiMerchantAdd.merchantGuiTextures);
//                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//                    boolean flag = p_146112_2_ >= this.x && p_146112_3_ >= this.y && p_146112_2_ < this.x + this.width && p_146112_3_ < this.y + this.height;
//                    int k = 0;
//                    int l = 176;
//
//                    if (!this.enabled)
//                    {
//                        l += this.width * 2;
//                    }
//                    else if (flag)
//                    {
//                        l += this.width;
//                    }
//
//                    if (!this.field_146157_o)
//                    {
//                        k += this.height;
//                    }
//
//                    this.fillGradient(matrixStack, this.x, this.y, l, k, this.width, this.height);
//                }
//            }
//        }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    @Override
    public Screen getParent() {
        return null;
    }

    @Override
    public void elementClicked() {

    }

    @Override
    public void subGuiClosed(Screen subgui) {

    }
}
