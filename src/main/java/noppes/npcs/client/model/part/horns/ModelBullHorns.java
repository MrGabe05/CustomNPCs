package noppes.npcs.client.model.part.horns;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelBullHorns extends ModelRenderer {

	public ModelBullHorns(BipedModel base) {
		super(base.texWidth, base.texHeight, 0, 0);
		ModelRenderer Left1 = new ModelRenderer(base.texWidth, base.texHeight, 36, 16);
		Left1.mirror = true;
		Left1.addBox(0F, 0F, 0F, 2, 2, 2);
		Left1.setPos(4F, -8F, -2F);
		addChild(Left1);
		
		ModelRenderer Right1 = new ModelRenderer(base.texWidth, base.texHeight, 36, 16);
		Right1.addBox(-3F, 0F, 0F, 2, 2, 2);
		Right1.setPos(-3F, -8F, -2F);
		addChild(Right1);
		
		ModelRenderer Left2 = new ModelRenderer(base.texWidth, base.texHeight, 12, 16);
		Left2.mirror = true;
		Left2.addBox(0F, 0F, 0F, 2, 2, 2);
		Left2.setPos(5F, -8F, -2F);
		setRotation(Left2, 0.0371786F, 0.3346075F, -0.2602503F);
		addChild(Left2);
		
		ModelRenderer Right2 = new ModelRenderer(base.texWidth, base.texHeight, 12, 16);
		Right2.addBox(-2F, 0F, 0F, 2, 2, 2);
		Right2.setPos(-5F, -8F, -2F);
		setRotation(Right2, 0.0371786F, -0.3346075F, 0.2602503F);
		addChild(Right2);
		
		ModelRenderer Left3 = new ModelRenderer(base.texWidth, base.texHeight, 13, 17);
		Left3.mirror = true;
		Left3.addBox(-1F, 0F, 0F, 2, 1, 1);
		Left3.setPos(7F, -8F, -2F);
		setRotation(Left3, 0.2602503F, 0.8551081F, -0.4089647F);
		addChild(Left3);
		
		ModelRenderer Right3 = new ModelRenderer(base.texWidth, base.texHeight, 13, 17);
		Right3.addBox(-1F, 0F, 0F, 2, 1, 1);
		Right3.setPos(-7F, -8F, -2F);
		setRotation(Right3, -0.2602503F, -0.8551081F, 0.4089647F);
		addChild(Right3);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}
}
