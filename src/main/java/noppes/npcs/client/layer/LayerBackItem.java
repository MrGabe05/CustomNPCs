package noppes.npcs.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.vector.Vector3f;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.wrapper.ItemStackWrapper;

public class LayerBackItem extends LayerInterface{

	public LayerBackItem(LivingRenderer render) {
		super(render);
	}

	@Override
	public void render(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
		Minecraft minecraft = Minecraft.getInstance();
		ItemStack itemstack = ItemStackWrapper.MCItem(npc.inventory.getRightHand());
		if(NoppesUtilServer.IsItemStackNull(itemstack) || npc.isAttacking())
			return;
		Item item = itemstack.getItem();
		if (item instanceof BlockItem) {
			return;
		}
		mStack.pushPose();
		base.body.translateAndRotate(mStack);
		mStack.translate(0, 0.36f, 0.14f);
		mStack.mulPose(Vector3f.XP.rotationDegrees(180));
		if (item instanceof SwordItem) {
			mStack.mulPose(Vector3f.XN.rotationDegrees(180));
		}
		IBakedModel model = minecraft.getItemRenderer().getItemModelShaper().getItemModel(itemstack);
		ItemTransformVec3f p_175034_1_ = model.getTransforms().thirdPersonRightHand;
		mStack.scale(p_175034_1_.scale.x(), p_175034_1_.scale.y(), p_175034_1_.scale.z());

		minecraft.getItemRenderer().renderStatic(npc, itemstack, ItemCameraTransforms.TransformType.NONE, false, mStack, typeBuffer, npc.level, lightmapUV, LivingRenderer.getOverlayCoords(npc, 0.0F));
		mStack.popPose();
	}

	@Override
	public void rotate(MatrixStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		
	}
}
