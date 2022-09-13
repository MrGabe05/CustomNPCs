package noppes.npcs.client.model.part.legs;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelMermaidLegs extends ModelRenderer {

	private final ModelRenderer top;
	private final ModelRenderer middle;
	private final ModelRenderer bottom;
	private final ModelRenderer fin1;
	private final ModelRenderer fin2;

	public ModelMermaidLegs(Model base) {
		super(base.texWidth, base.texHeight, 0, 0);
		xTexSize = 64;
		yTexOffs = 32;

		top = new ModelRenderer(base.texWidth, base.texHeight, 0, 16);
		top.addBox(-2F, -2.5F, -2F, 8, 9, 4);
		top.setPos(-2F, 14F, 1F);
		setRotation(top, 0.26F, 0F, 0F);

		middle = new ModelRenderer(base.texWidth, base.texHeight, 28, 0);
		middle.addBox(0F, 0F, 0F, 7, 6, 4);
		middle.setPos(-1.5F, 6.5F, -1F);
		setRotation(middle, 0.86f, 0F, 0F);
		top.addChild(middle);

		bottom = new ModelRenderer(base.texWidth, base.texHeight, 24, 16);
		bottom.addBox(0F, 0F, 0F, 6, 7, 3);
		bottom.setPos(0.5F, 6F, 0.5f);
		setRotation(bottom, 0.15f, 0F, 0F);
		middle.addChild(bottom);

		fin1 = new ModelRenderer(base.texWidth, base.texHeight, 0, 0);
		fin1.addBox(0F, 0F, 0F, 5, 9, 1);
		fin1.setPos(0F, 4.5F, 1F);
		setRotation(fin1, 0.05f, 0, 0.5911399F);
		bottom.addChild(fin1);
		
		fin2 = new ModelRenderer(base.texWidth, base.texHeight, 0, 0);
		fin2.mirror = true;
		fin2.addBox(-5F, 0F, 0F, 5, 9, 1);
		fin2.setPos(6F, 4.5F, 1F);
		setRotation(fin2, 0.05f, 0, -0.591143F);
		bottom.addChild(fin2);

	}

	@Override
	public void render(MatrixStack mStack, IVertexBuilder iVertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (!visible)
			return;
		top.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}

	public void setupAnim(float par1, float limbSwingAmount, float par3,
			float par4, float par5, float par6, Entity entity) {
		float ani = MathHelper.sin(par1 * 0.6662F);
		if(ani > 0.2)
			ani /= 3f;
		top.xRot = 0.26F - ani * 0.2F * limbSwingAmount;
		middle.xRot = 0.86f - ani * 0.24F * limbSwingAmount;
		bottom.xRot = 0.15f - ani * 0.28F * limbSwingAmount;
		fin2.xRot = fin1.xRot = 0.05f - ani * 0.35F * limbSwingAmount;
		
	}
}
