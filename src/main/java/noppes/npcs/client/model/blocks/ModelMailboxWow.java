package noppes.npcs.client.model.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMailboxWow extends Model {
	// fields
	ModelRenderer Shape4;
	ModelRenderer Shape1;
	ModelRenderer Shape2;
	ModelRenderer Shape3;

	public ModelMailboxWow() {
		super(RenderType::entityCutout);
		texWidth = 128;
		texHeight = 64;

		Shape4 = new ModelRenderer(this, 59, 0);
		Shape4.addBox(0F, 0F, 0F, 8, 6, 0);
		Shape4.setPos(-4F, -4F, 0F);
		
		Shape1 = new ModelRenderer(this, 0, 39);
		Shape1.addBox(0F, 0F, 0F, 8, 5, 8);
		Shape1.setPos(-4F, 19F, -4F);
		
		Shape2 = new ModelRenderer(this, 0, 21);
		Shape2.addBox(0F, 0F, 0F, 6, 9, 6);
		Shape2.setPos(-3F, 10F, -3F);
		
		Shape3 = new ModelRenderer(this, 0, 0);
		Shape3.addBox(0F, 0F, 0F, 12, 8, 12);
		Shape3.setPos(-6F, 2F, -6F);
	}

	@Override
	public void renderToBuffer(MatrixStack mStack, IVertexBuilder iVertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
		Shape4.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape1.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape2.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape3.render(mStack, iVertex, lightmapUV, packedOverlayIn);
	}
}
