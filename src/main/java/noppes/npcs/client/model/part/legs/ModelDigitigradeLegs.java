package noppes.npcs.client.model.part.legs;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelDigitigradeLegs extends ModelRenderer {

	private final ModelRenderer rightleg;
	private final ModelRenderer rightleg2;
	private final ModelRenderer rightleglow;
	private final ModelRenderer rightfoot;
	private final ModelRenderer leftleg;
	private final ModelRenderer leftleg2;
	private final ModelRenderer leftleglow;
	private final ModelRenderer leftfoot;

	private final BipedModel base;

	public ModelDigitigradeLegs(BipedModel base) {
		super(base.texWidth, base.texHeight, 0, 0);
		this.base = base;
		rightleg = new ModelRenderer(base.texWidth, base.texHeight, 0, 16);
		rightleg.addBox(-2F, 0F, -2F, 4, 6, 4);
		rightleg.setPos(-2.1F, 11F, 0F);
		setRotation(rightleg, -0.3F, 0F, 0F);
		this.addChild(rightleg);

		rightleg2 = new ModelRenderer(base.texWidth, base.texHeight, 0, 20);
		rightleg2.addBox(-1.5F, -1F, -2F, 3, 7, 3);
		rightleg2.setPos(0F, 4.1F, 0F);
		setRotation(rightleg2, 1.1f, 0F, 0F);
		rightleg.addChild(rightleg2);

		rightleglow = new ModelRenderer(base.texWidth, base.texHeight, 0, 24);
		rightleglow.addBox(-1.5F, 0F, -1F, 3, 5, 2);
		rightleglow.setPos(0F, 5F, 0F);
		setRotation(rightleglow, -1.35F, 0F, 0F);
		rightleg2.addChild(rightleglow);

		rightfoot = new ModelRenderer(base.texWidth, base.texHeight, 1, 26);
		rightfoot.addBox(-1.5F, 0F, -5F, 3, 2, 4);
		rightfoot.setPos(0F, 3.7F, 1.2F);
		setRotation(rightfoot, 0.55F, 0F, 0F);
		rightleglow.addChild(rightfoot);

		leftleg = new ModelRenderer(base.texWidth, base.texHeight, 0, 16);
		leftleg.mirror = true;
		leftleg.addBox(-2F, 0F, -2F, 4, 6, 4);
		leftleg.setPos(2.1F, 11F, 0F);
		setRotation(leftleg, -0.3F, 0F, 0F);
		this.addChild(leftleg);

		leftleg2 = new ModelRenderer(base.texWidth, base.texHeight, 0, 20);
		leftleg2.mirror = true;
		leftleg2.addBox(-1.5F, -1F, -2F, 3, 7, 3);
		leftleg2.setPos(0F, 4.1F, 0F);
		setRotation(leftleg2, 1.1f, 0F, 0F);
		leftleg.addChild(leftleg2);

		leftleglow = new ModelRenderer(base.texWidth, base.texHeight, 0, 24);
		leftleglow.mirror = true;
		leftleglow.addBox(-1.5F, 0F, -1F, 3, 5, 2);
		leftleglow.setPos(0F, 5F, 0F);
		setRotation(leftleglow, -1.35F, 0F, 0F);
		leftleg2.addChild(leftleglow);

		leftfoot = new ModelRenderer(base.texWidth, base.texHeight, 1, 26);
		leftfoot.mirror = true;
		leftfoot.addBox(-1.5F, 0F, -5F, 3, 2, 4);
		leftfoot.setPos(0F, 3.7F, 1.2F);
		setRotation(leftfoot, 0.55F, 0F, 0F);
		leftleglow.addChild(leftfoot);
	}

	public void setupAnim(float par1, float limbSwingAmount, float par3, float par4, float par5, float par6, Entity entity)
    {
    	rightleg.xRot = base.rightLeg.xRot - 0.3f;
    	leftleg.xRot = base.leftLeg.xRot - 0.3f;
    	rightleg.y = base.rightLeg.y;
    	leftleg.y = base.leftLeg.y;
    	rightleg.z = base.rightLeg.z;
    	leftleg.z = base.leftLeg.z;
    	if(!base.crouching){
    		leftleg.y--;
    		rightleg.y--;
    	}
    		
    }

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}
}
