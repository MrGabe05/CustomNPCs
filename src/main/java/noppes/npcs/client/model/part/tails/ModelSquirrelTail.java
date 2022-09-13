package noppes.npcs.client.model.part.tails;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSquirrelTail extends ModelRenderer {

	private final BipedModel base;

	public ModelSquirrelTail(BipedModel base) {
		super(base.texWidth, base.texHeight, 0, 0);
		this.base = base;

		ModelRenderer Shape1 = new ModelRenderer(base.texWidth, base.texHeight, 0, 0);
		Shape1.addBox(-1F, -1F, -1F, 2, 2, 3);
		Shape1.setPos(0F, -1F, 3F);
		setRotation(Shape1, 0F, 0F, 0F);
		this.addChild(Shape1);
		
		ModelRenderer Shape2 = new ModelRenderer(base.texWidth, base.texHeight, 0, 9);
		Shape2.addBox(-2F, -5F, -1F, 4, 5, 3);
		Shape2.setPos(0F, 0F, 1F);
		setRotation(Shape2, -0.37F, 0F, 0F);
		Shape1.addChild(Shape2);
		
		ModelRenderer Shape3 = new ModelRenderer(base.texWidth, base.texHeight, 0, 18);
		Shape3.addBox(-2.466667F, -6F, -1F, 5, 7, 3);
		Shape3.setPos(0F, -5F, 0F);
		setRotation(Shape3, 0.3f, 0F, 0F);
		Shape2.addChild(Shape3);
		
		ModelRenderer Shape4 = new ModelRenderer(base.texWidth, base.texHeight, 25, 0);
		Shape4.addBox(-3F, -0.6F, -1F, 6, 5, 3);
		Shape4.setPos(0F, -5F, 1F);
		setRotation(Shape4, 2.5F, 0F, 0F);
		Shape3.addChild(Shape4);
		
		ModelRenderer Shape5 = new ModelRenderer(base.texWidth, base.texHeight, 25, 10);
		Shape5.addBox(-3F, -2F, -1F, 6, 3, 5);
		Shape5.setPos(0F, 3.5F, 0F);
		setRotation(Shape5, -2.5F, 0F, 0F);
		Shape4.addChild(Shape5);
	}

	public void setupAnim(float par1, float limbSwingAmount, float par3,
			float par4, float par5, float par6, Entity entity) {

	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}
}
