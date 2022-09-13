package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.vector.Vector3f;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.blocks.tiles.TileScripted.TextPlane;
import noppes.npcs.client.TextBlockClient;

import java.util.Random;

public class BlockScriptedRenderer extends BlockRendererInterface<TileScripted> {
	
	private static final Random random = new Random();

	public BlockScriptedRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(TileScripted tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
		matrixStack.pushPose();

        //RenderHelper.enableStandardItemLighting();
        //RenderSystem.translated(x + 0.5, y, z + 0.5);
        if(overrideModel()){
			matrixStack.translate(0.5f, 0.5f, 0.5f);
			matrixStack.scale(2, 2, 2);
        	renderItem(new ItemStack(CustomBlocks.scripted), matrixStack, buffer, light, overlay);
        }
        else{
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(tile.rotationY));
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(tile.rotationX));
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(tile.rotationZ));
			matrixStack.scale(tile.scaleX, tile.scaleY, tile.scaleZ);
        	Block b = tile.blockModel;
        	if(b == null || b == Blocks.AIR || b == CustomBlocks.scripted){
				matrixStack.translate(0.5f, 0.5f, 0.5f);
				matrixStack.scale(2, 2, 2);
        		renderItem(tile.itemModel, matrixStack, buffer, light, overlay);
        	}
        	else{
                BlockState state = b.defaultBlockState();
        		renderBlock(tile, b, state, matrixStack, buffer, light, overlay);

                if(b.hasTileEntity(state) && !tile.renderTileErrored){
                	try{
	                	if(tile.renderTile == null){
	                    	TileEntity entity = b.createTileEntity(state, tile.getLevel());
	                    	entity.setLevelAndPosition(tile.getLevel(), tile.getBlockPos());
	                    	//ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, entity, tile.itemModel.getItemDamage(), 5);
	                    	//ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, entity, b, 6);
	                    	tile.renderTile = entity;
	                    	if(entity instanceof ITickableTileEntity){
	                    		tile.renderTileUpdate = (ITickableTileEntity) entity;
	                    	}
	                	}
						TileEntityRenderer renderer = TileEntityRendererDispatcher.instance.getRenderer(tile.renderTile);
	                    
	                    if(renderer != null){
	                    	renderer.render(tile.renderTile, partialTicks, matrixStack, buffer, light, overlay);
	                    
	                    }
	                    else
	                		tile.renderTileErrored = true;
                	}
                	catch(Exception e){
                		tile.renderTileErrored = true;
                	}
                }
        	}
        }
		matrixStack.popPose();

        if(!tile.text1.text.isEmpty()) {
        	drawText(matrixStack, tile.text1);
        }
        if(!tile.text2.text.isEmpty()) {
        	drawText(matrixStack, tile.text2);
        }
        if(!tile.text3.text.isEmpty()) {
        	drawText(matrixStack, tile.text3);
        }
        if(!tile.text4.text.isEmpty()) {
        	drawText(matrixStack, tile.text4);
        }
        if(!tile.text5.text.isEmpty()) {
        	drawText(matrixStack, tile.text5);
        }
        if(!tile.text6.text.isEmpty()) {
        	drawText(matrixStack, tile.text6);
        }
	}
	
	private void drawText(MatrixStack matrixStack, TextPlane text1) {
		if(text1.textBlock == null || text1.textHasChanged){
			text1.textBlock = new TextBlockClient(text1.text, 336, true, Minecraft.getInstance().player);
			text1.textHasChanged = false;
		}
        RenderSystem.disableBlend();
        RenderSystem.enableLighting();
		matrixStack.pushPose();
		matrixStack.translate(0.5, 0.5, 0.5);
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(text1.rotationY));
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(text1.rotationX));
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(text1.rotationZ));
		matrixStack.scale(text1.scale, text1.scale, 1);
		matrixStack.translate(text1.offsetX, text1.offsetY, text1.offsetZ);
        float f1 = 0.6666667F;
        float f3 = 0.0133F * f1;
        RenderSystem.translatef(0.0F, 0.5f, 0.01F);
        RenderSystem.scalef(f3, -f3, f3);
        RenderSystem.normal3f(0.0F, 0.0F, -1.0F * f3);
        RenderSystem.depthMask(false);
        FontRenderer fontrenderer = this.renderer.font;
        
        float lineOffset = 0;
        if(text1.textBlock.lines.size() < 14)
        	lineOffset = (14f - text1.textBlock.lines.size()) / 2;
    	for(int i = 0; i < text1.textBlock.lines.size(); i++){
    		String text = text1.textBlock.lines.get(i).getString();
    		fontrenderer.draw(matrixStack, text, -fontrenderer.width(text) / 2, (int)((lineOffset + i) * (fontrenderer.lineHeight - 0.3)), 0);
    	}

        RenderSystem.depthMask(true);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		matrixStack.popPose();
	}
	
	private void renderItem(ItemStack item, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay){
		Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemCameraTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
		//Minecraft.getInstance().getItemRenderer().render(item, ItemCameraTransforms.TransformType.NONE);
	}
	
	private void renderBlock(TileScripted tile, Block b, BlockState state, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay){//TODO fix
		matrixStack.pushPose();
		//matrixStack.translate(-5F, -0, 5F);

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStack, buffer, light, OverlayTexture.NO_OVERLAY);
        if(random.nextInt(12) == 1)
        	state.getBlock().animateTick(state, tile.getLevel(), tile.getBlockPos(), random);
		matrixStack.popPose();
	}
	
	private boolean overrideModel(){
		ItemStack held = Minecraft.getInstance().player.getMainHandItem();
		if(held == null)
			return false;
		
		return held.getItem() == CustomItems.wand || held.getItem() == CustomItems.scripter;
	}
}
