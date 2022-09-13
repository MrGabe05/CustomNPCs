// Date: 11-10-2012 19:29:20
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX

package noppes.npcs.client.model.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelCarpentryBench extends Model {
	// fields
	ModelRenderer Leg1;
	ModelRenderer Leg2;
	ModelRenderer Leg3;
	ModelRenderer Leg4;
	ModelRenderer Bottom_plate;
	ModelRenderer Desktop;
	ModelRenderer Backboard;
	ModelRenderer Vice_Jaw1;
	ModelRenderer Vice_Jaw2;
	ModelRenderer Vice_Base1;
	ModelRenderer Vice_Base2;
	ModelRenderer Vice_Crank;
	ModelRenderer Vice_Screw;
	ModelRenderer Blueprint;

	public ModelCarpentryBench() {
		super(RenderType::entityCutoutNoCull);
		this.texWidth = 128;
		this.texHeight = 64;

		Leg1 = new ModelRenderer(this, 0, 0);
		Leg1.addBox(0F, 0F, 0F, 2, 14, 2);
		Leg1.setPos(6F, 10F, 5F);
		
		Leg2 = new ModelRenderer(this, 0, 0);
		Leg2.addBox(0F, 0F, 0F, 2, 14, 2);
		Leg2.setPos(6F, 10F, -5F);
		
		Leg3 = new ModelRenderer(this, 0, 0);
		Leg3.addBox(0F, 0F, 0F, 2, 14, 2);
		Leg3.setPos(-8F, 10F, 5F);
		
		Leg4 = new ModelRenderer(this, 0, 0);
		Leg4.addBox(0F, 0F, 0F, 2, 14, 2);
		Leg4.setPos(-8F, 10F, -5F);
		
		Bottom_plate = new ModelRenderer(this, 0, 24);
		Bottom_plate.addBox(0F, 0F, 0F, 14, 1, 10);
		Bottom_plate.setPos(-7F, 21F, -4F);
		Bottom_plate.setTexSize(130, 64);
		
		Desktop = new ModelRenderer(this, 0, 3);
		Desktop.addBox(0F, 0F, 0F, 18, 2, 13);
		Desktop.setPos(-9F, 9F, -6F);
		
		Backboard = new ModelRenderer(this, 0, 18);
		Backboard.addBox(-1F, 0F, 0F, 18, 5, 1);
		Backboard.setPos(-8F, 7F, 7F);
		
		Vice_Jaw1 = new ModelRenderer(this, 54, 18);
		Vice_Jaw1.addBox(0F, 0F, 0F, 3, 2, 1);
		Vice_Jaw1.setPos(3F, 6F, -8F);
		
		Vice_Jaw2 = new ModelRenderer(this, 54, 21);
		Vice_Jaw2.addBox(0F, 0F, 0F, 3, 2, 1);
		Vice_Jaw2.setPos(3F, 6F, -6F);
		
		Vice_Base1 = new ModelRenderer(this, 38, 30);
		Vice_Base1.addBox(0F, 0F, 0F, 3, 1, 3);
		Vice_Base1.setPos(3F, 8F, -5F);
		
		Vice_Base2 = new ModelRenderer(this, 38, 25);
		Vice_Base2.addBox(0F, 0F, 0F, 1, 2, 2);
		Vice_Base2.setPos(4F, 7F, -5F);
		
		Vice_Crank = new ModelRenderer(this, 54, 24);
		Vice_Crank.addBox(0F, 0F, 0F, 1, 5, 1);
		Vice_Crank.setPos(6F, 6F, -9F);
		
		Vice_Screw = new ModelRenderer(this, 44, 25);
		Vice_Screw.addBox(0F, 0F, 0F, 1, 1, 4);
		Vice_Screw.setPos(4F, 8F, -8F);
		
		Blueprint = new ModelRenderer(this, 31, 18);
		Blueprint.addBox(0F, 0F, 0F, 8, 0, 7);
		Blueprint.setPos(0F, 9F, 1F);
	    setRotation(Blueprint, 0.3271718F, 0.1487144F, 0F);
	}

	@Override
	public void renderToBuffer(MatrixStack mStack, IVertexBuilder iVertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
		Leg1.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Leg2.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Leg3.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Leg4.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Bottom_plate.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Desktop.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Backboard.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Vice_Jaw1.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Vice_Jaw2.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Vice_Base1.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Vice_Base2.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Vice_Crank.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Vice_Screw.render(mStack, iVertex, lightmapUV, packedOverlayIn);
		Blueprint.render(mStack, iVertex, lightmapUV, packedOverlayIn);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}


}