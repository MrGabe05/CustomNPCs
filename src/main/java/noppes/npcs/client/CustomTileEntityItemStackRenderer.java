package noppes.npcs.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import noppes.npcs.CustomItems;
import noppes.npcs.items.ItemNpcBlock;

import java.util.HashMap;

public class CustomTileEntityItemStackRenderer extends ItemStackTileEntityRenderer {
    public static CustomTileEntityItemStackRenderer i = new CustomTileEntityItemStackRenderer();

    private HashMap<Block, TileEntity> data = new HashMap<Block, TileEntity>();

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if(stack.getItem() instanceof ItemNpcBlock){
            ItemNpcBlock item = (ItemNpcBlock) stack.getItem();
            TileEntity tile = data.get(item.block);
            if(tile == null) {
                data.put(item.block, tile = ((ContainerBlock)item.block).newBlockEntity(null));
            }
            TileEntityRendererDispatcher.instance.renderItem(tile, matrixStack, buffer, combinedLight, combinedOverlay);
        }
    }
}
