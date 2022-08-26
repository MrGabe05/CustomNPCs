package noppes.npcs.client.model.part.horns;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelAntlerHorns extends ModelRenderer {

	public ModelAntlerHorns(BipedModel base) {
		super(base.texWidth, base.texHeight, 0, 0);
		ModelRenderer right_base_horn = new ModelRenderer(base.texWidth, base.texHeight, 58, 20);
		right_base_horn.addBox(0F, -5F, 0F, 1, 6, 1);
		right_base_horn.setPos(-2.5F, -6F, -1F);
		setRotation(right_base_horn, 0F, 0F, -0.2F);
		addChild(right_base_horn);
		ModelRenderer right_horn1 = new ModelRenderer(base.texWidth, base.texHeight, 58, 20);
		right_horn1.addBox(0F, -5F, 0F, 1, 5, 1);
		right_horn1.setPos(0F, -4F, 0F);
		setRotation(right_horn1, 1F, 0F, -1F);
		right_base_horn.addChild(right_horn1);
		ModelRenderer right_horn2 = new ModelRenderer(base.texWidth, base.texHeight, 58, 20);
		right_horn2.addBox(0F, -4F, 0F, 1, 5, 1);
		right_horn2.setPos(-0F, -6F, -0F);
		setRotation(right_horn2, -0.5F, -0.5F, 0F);
		right_base_horn.addChild(right_horn2);
		ModelRenderer things1 = new ModelRenderer(base.texWidth, base.texHeight, 58, 20);
		things1.addBox(0F, -5F, 0F, 1, 5, 1);
		things1.setPos(0F, -3F, 1F);
		setRotation(things1, 2F, 0.5f, 0.5f);
		right_horn2.addChild(things1);
		ModelRenderer things2 = new ModelRenderer(base.texWidth, base.texHeight, 58, 20);
		things2.addBox(0F, -5F, 0F, 1, 5, 1);
		things2.setPos(0F, -3F, 1F);
		setRotation(things2, 2F, -0.5f, -0.5f);
		right_horn2.addChild(things2);

		ModelRenderer left_base_horn = new ModelRenderer(base.texWidth, base.texHeight, 58, 20);
		left_base_horn.addBox(0F, -5F, 0F, 1, 6, 1);
		left_base_horn.setPos(1.5F, -6F, -1F);
		setRotation(left_base_horn, 0F, 0F, 0.2F);
		addChild(left_base_horn);
		ModelRenderer left_horn1 = new ModelRenderer(base.texWidth, base.texHeight, 58, 20);
		left_horn1.addBox(0F, -5F, 0F, 1, 5, 1);
		left_horn1.setPos(0F, -5F, 0F);
		setRotation(left_horn1, 1F, 0F, 1F);
		left_base_horn.addChild(left_horn1);
		ModelRenderer left_horn2 = new ModelRenderer(base.texWidth, base.texHeight, 58, 20);
		left_horn2.addBox(0F, -4F, 0F, 1, 5, 1);
		left_horn2.setPos(0F, -6F, 1F);
		setRotation(left_horn2, -0.5F, 0.5F, 0F);
		left_base_horn.addChild(left_horn2);
		ModelRenderer things8 = new ModelRenderer(base.texWidth, base.texHeight, 58, 20);
		things8.addBox(0F, -5F, 0F, 1, 5, 1);
		things8.setPos(0F, -3F, 1F);
		setRotation(things8, 2F, -0.5f, -0.5f);
		left_horn2.addChild(things8);
		ModelRenderer things4 = new ModelRenderer(base.texWidth, base.texHeight, 58, 20);
		things4.addBox(0F, -5F, 0F, 1, 5, 1);
		things4.setPos(0F, -3F, 1F);
		setRotation(things4, 2F, 0.5f, 0.5f);
		left_horn2.addChild(things4);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}
}
