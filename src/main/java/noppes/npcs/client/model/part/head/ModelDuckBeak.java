package noppes.npcs.client.model.part.head;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import com.mojang.blaze3d.systems.RenderSystem;

public class ModelDuckBeak extends ModelRenderer {
	ModelRenderer Top3;
	ModelRenderer Top2;
	ModelRenderer Bottom;
	ModelRenderer Left;
	ModelRenderer Right;
	ModelRenderer Middle;
	ModelRenderer Top;

	public ModelDuckBeak(BipedModel base) {
		super(base.texWidth, base.texHeight, 0, 0);
		
		Top3 = new ModelRenderer(base.texWidth, base.texHeight, 14, 0);
		Top3.addBox(0F, 0F, 0F, 2, 1, 3);
		Top3.setPos(-1F, -2F, -5F);
		setRotation(Top3, 0.3346075F, 0F, 0F);
		addChild(Top3);

		Top2 = new ModelRenderer(base.texWidth, base.texHeight, 0, 0);
		Top2.addBox(0F, 0F, -0.4F, 4, 1, 3);
		Top2.setPos(-2F, -3F, -2F);
		setRotation(Top2, 0.3346075F, 0F, 0F);
		addChild(Top2);

		Bottom = new ModelRenderer(base.texWidth, base.texHeight, 24, 0);
		Bottom.addBox(0F, 0F, 0F, 2, 1, 5);
		Bottom.setPos(-1F, -1F, -5F);
		addChild(Bottom);

		Left = new ModelRenderer(base.texWidth, base.texHeight, 0, 4);
		Left.mirror = true;
		Left.addBox(0F, 0F, 0F, 1, 3, 2);
		Left.setPos(0.98F, -3F, -2F);
		addChild(Left);

		Right = new ModelRenderer(base.texWidth, base.texHeight, 0, 4);
		Right.addBox(0F, 0F, 0F, 1, 3, 2);
		Right.setPos(-1.98F, -3F, -2F);
		addChild(Right);

		Middle = new ModelRenderer(base.texWidth, base.texHeight, 3, 0);
		Middle.addBox(0F, 0F, 0F, 2, 1, 3);
		Middle.setPos(-1F, -2F, -5F);
		addChild(Middle);

		Top = new ModelRenderer(base.texWidth, base.texHeight, 6, 4);
		Top.addBox(0F, 0F, 0F, 2, 2, 1);
		Top.setPos(-1F, -4.4F, -1F);
		addChild(Top);
	}
	
	@Override
	public void render(MatrixStack mstack, IVertexBuilder builder, int light, int overlay, float red, float green, float blue, float alpha) {
		mstack.pushPose();
		mstack.translate(0, 0, -1f);//mstack.translate(0, 0, -1f * f);
		mstack.scale(0.82f, 0.82f, 0.70f);
    	super.render(mstack, builder, light, overlay, red, green, blue, alpha);
		mstack.popPose();
    }

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}

}
