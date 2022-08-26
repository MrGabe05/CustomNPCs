package noppes.npcs.client.model.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMailboxUS extends Model {
	// fields
	ModelRenderer Shape1;
	ModelRenderer Shape2;
	ModelRenderer Shape3;
	ModelRenderer Shape4;
	ModelRenderer Shape5;
	ModelRenderer Shape6;
	ModelRenderer Shape7;
	ModelRenderer Shape8;
	ModelRenderer Shape9;
	ModelRenderer Shape10;
	ModelRenderer Shape11;
	ModelRenderer Shape12;
	ModelRenderer Shape13;

	public ModelMailboxUS() {
		super(RenderType::entityCutout);
		texWidth = 64;
		texHeight = 128;

		Shape1 = new ModelRenderer(this, 0, 48);
		Shape1.addBox(0F, 0F, 0F, 16, 14, 16);
		Shape1.setPos(-8F, 8F, -8F);
		
		Shape2 = new ModelRenderer(this, 0, 79);
		Shape2.addBox(0F, 0F, 0F, 1, 2, 1);
		Shape2.setPos(-8F, 22F, -8F);
		
		Shape3 = new ModelRenderer(this, 5, 79);
		Shape3.addBox(0F, 0F, 0F, 1, 2, 1);
		Shape3.setPos(-8F, 22F, 7F);
		
		Shape4 = new ModelRenderer(this, 10, 79);
		Shape4.addBox(0F, 0F, 0F, 1, 2, 1);
		Shape4.setPos(7F, 22F, -8F);
		
		Shape5 = new ModelRenderer(this, 15, 79);
		Shape5.addBox(0F, 0F, 0F, 1, 2, 1);
		Shape5.setPos(7F, 22F, 7F);
		
		Shape6 = new ModelRenderer(this, 0, 14);
		Shape6.addBox(0F, 0F, 0F, 16, 3, 7);
		Shape6.setPos(-8F, 5F, 0F);
		
		Shape7 = new ModelRenderer(this, 0, 6);
		Shape7.addBox(0F, 0F, 0F, 16, 2, 6);
		Shape7.setPos(-8F, 3F, 0F);
		
		Shape8 = new ModelRenderer(this, 0, 0);
		Shape8.addBox(0F, 0F, 0F, 16, 1, 5);
		Shape8.setPos(-8F, 2F, 0F);
		
		Shape9 = new ModelRenderer(this, 0, 37);
		Shape9.addBox(0F, 0F, 0F, 1, 3, 7);
		Shape9.setPos(-8F, 5F, -7F);
		
		Shape10 = new ModelRenderer(this, 16, 37);
		Shape10.addBox(0F, 0F, 0F, 1, 3, 7);
		Shape10.setPos(7F, 5F, -7F);
		
		Shape11 = new ModelRenderer(this, 0, 29);
		Shape11.addBox(0F, 0F, 0F, 1, 2, 6);
		Shape11.setPos(-8F, 3F, -6F);
		
		Shape12 = new ModelRenderer(this, 14, 29);
		Shape12.addBox(0F, 0F, 0F, 1, 2, 6);
		Shape12.setPos(7F, 3F, -6F);
		
		Shape13 = new ModelRenderer(this, 0, 25);
		Shape13.addBox(0F, 0F, 0F, 16, 1, 3);
		Shape13.setPos(-8F, 2F, -3F);
	}

	@Override
	public void renderToBuffer(MatrixStack mStack, IVertexBuilder iVertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
		Shape1.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape2.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape3.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape4.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape5.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape6.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape7.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape8.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape9.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape10.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape11.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape12.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Shape13.render(mStack, iVertex, lightmapUV, packedOverlayIn);
	}

}
