package noppes.npcs.client.model.part.legs;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.ModelData;

public class ModelHorseLegs extends ModelRenderer {
    private final ModelRenderer backLeftLeg;
    private final ModelRenderer backLeftShin;
    private final ModelRenderer backLeftHoof;
    
    private final ModelRenderer backRightLeg;
    private final ModelRenderer backRightShin;
    private final ModelRenderer backRightHoof;
    
    private final ModelRenderer frontLeftLeg;
    private final ModelRenderer frontLeftShin;
    private final ModelRenderer frontLeftHoof;
    
    private final ModelRenderer frontRightLeg;
    private final ModelRenderer frontRightShin;
    private final ModelRenderer frontRightHoof;

	private final BipedModel base;

	public ModelHorseLegs(BipedModel base) {
		super(base.texWidth, base.texHeight, 0, 0);
		this.base = base;

        float var1 = 0.0F;
        byte var2 = 15;
        int zOffset = 10;
        float yOffset = 7f;
        ModelRenderer body = new ModelRenderer(base.texWidth, base.texHeight,  0, 34);
        body.setTexSize(128, 128);
        body.addBox(-5.0F, -8.0F, -19.0F, 10, 10, 24);
        body.setPos(0.0F, 11.0F + yOffset, 9.0F + zOffset);
        addChild(body);
        
        this.backLeftLeg = new ModelRenderer(base.texWidth, base.texHeight,  78, 29);
        this.backLeftLeg.setTexSize(128, 128);
        this.backLeftLeg.addBox(-2F, -2.0F, -2.5F, 4, 9, 5);
        this.backLeftLeg.setPos(4.0F, 9.0F + yOffset, 11.0F + zOffset);
        addChild(backLeftLeg);
        this.backLeftShin = new ModelRenderer(base.texWidth, base.texHeight,  78, 43);
        this.backLeftShin.setTexSize(128, 128);
        this.backLeftShin.addBox(-1.5F, 0.0F, -1.5F, 3, 5, 3);
        this.backLeftShin.setPos(0F, 7.0F, 0);
        backLeftLeg.addChild(backLeftShin);
        this.backLeftHoof = new ModelRenderer(base.texWidth, base.texHeight,  78, 51);
        this.backLeftHoof.setTexSize(128, 128);
        this.backLeftHoof.addBox(-2F, 5.0F, -2.0F, 4, 3, 4);
        this.backLeftHoof.setPos(0F, 7.0F, 0);
        backLeftLeg.addChild(backLeftHoof);
        
        this.backRightLeg = new ModelRenderer(base.texWidth, base.texHeight,  96, 29);
        this.backRightLeg.setTexSize(128, 128);
        this.backRightLeg.addBox(-2F, -2.0F, -2.5F, 4, 9, 5);
        this.backRightLeg.setPos(-4.0F, 9.0F + yOffset, 11.0F + zOffset);
        addChild(backRightLeg);
        this.backRightShin = new ModelRenderer(base.texWidth, base.texHeight,  96, 43);
        this.backRightShin.setTexSize(128, 128);
        this.backRightShin.addBox(-1.5F, 0.0F, -1.5F, 3, 5, 3);
        this.backRightShin.setPos(0F, 7, 0);
        backRightLeg.addChild(backRightShin);
        this.backRightHoof = new ModelRenderer(base.texWidth, base.texHeight,  96, 51);
        this.backRightHoof.setTexSize(128, 128);
        this.backRightHoof.addBox(-2F, 5.0F, -2.0F, 4, 3, 4);
        this.backRightHoof.setPos(0F, 7, 0);
        backRightLeg.addChild(backRightHoof);
        
        this.frontLeftLeg = new ModelRenderer(base.texWidth, base.texHeight,  44, 29);
        this.frontLeftLeg.setTexSize(128, 128);
        this.frontLeftLeg.addBox(-1.4F, -1.0F, -2.1F, 3, 8, 4);
        this.frontLeftLeg.setPos(4.0F, 9.0F + yOffset, -8.0F + zOffset);
        addChild(frontLeftLeg);
        this.frontLeftShin = new ModelRenderer(base.texWidth, base.texHeight,  44, 41);
        this.frontLeftShin.setTexSize(128, 128);
        this.frontLeftShin.addBox(-1.4F, 0.0F, -1.6F, 3, 5, 3);
        this.frontLeftShin.setPos(0F, 7.0F, 0F);
        frontLeftLeg.addChild(frontLeftShin);
        this.frontLeftHoof = new ModelRenderer(base.texWidth, base.texHeight,  44, 51);
        this.frontLeftHoof.setTexSize(128, 128);
        this.frontLeftHoof.addBox(-1.9F, 5.0F, -2.1F, 4, 3, 4);
        this.frontLeftHoof.setPos(.0F, 7.0F, 0F);
        frontLeftLeg.addChild(frontLeftHoof);
        
        this.frontRightLeg = new ModelRenderer(base.texWidth, base.texHeight,  60, 29);
        this.frontRightLeg.setTexSize(128, 128);
        this.frontRightLeg.addBox(-1.6F, -1.0F, -2.1F, 3, 8, 4);
        this.frontRightLeg.setPos(-4.0F, 9.0F + yOffset, -8.0F + zOffset);
        addChild(frontRightLeg);
        this.frontRightShin = new ModelRenderer(base.texWidth, base.texHeight,  60, 41);
        this.frontRightShin.setTexSize(128, 128);
        this.frontRightShin.addBox(-1.6F, 0.0F, -1.6F, 3, 5, 3);
        this.frontRightShin.setPos(0F, 7, 0);
        frontRightLeg.addChild(frontRightShin);
        this.frontRightHoof = new ModelRenderer(base.texWidth, base.texHeight,  60, 51);
        this.frontRightHoof.setTexSize(128, 128);
        this.frontRightHoof.addBox(-2.1F, 5.0F, -2.1F, 4, 3, 4);
        this.frontRightHoof.setPos(0F, 7, 0);
        frontRightLeg.addChild(frontRightHoof);
	}

	public void setupAnim(ModelData data, float par1, float limbSwingAmount, float par3, float par4, float par5, float par6, Entity entity){
        this.frontLeftLeg.xRot = MathHelper.cos(par1 * 0.6662F) * .4F * limbSwingAmount;
        this.frontRightLeg.xRot = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * .4F * limbSwingAmount;
        this.backLeftLeg.xRot = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * .4F * limbSwingAmount;
        this.backRightLeg.xRot = MathHelper.cos(par1 * 0.6662F) * .4F * limbSwingAmount;
    }

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}
}
