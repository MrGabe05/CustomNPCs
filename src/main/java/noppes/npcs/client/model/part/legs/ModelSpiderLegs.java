package noppes.npcs.client.model.part.legs;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.ModelData;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityNPCInterface;

public class ModelSpiderLegs extends ModelRenderer {
	private ModelRenderer spiderLeg1;
    private ModelRenderer spiderLeg2;
    private ModelRenderer spiderLeg3;
    private ModelRenderer spiderLeg4;
    private ModelRenderer spiderLeg5;
    private ModelRenderer spiderLeg6;
    private ModelRenderer spiderLeg7;
    private ModelRenderer spiderLeg8;
    private ModelRenderer spiderBody;
    private ModelRenderer spiderNeck;

	private BipedModel base;

	public ModelSpiderLegs(BipedModel base) {
		super(base.texWidth, base.texHeight, 0, 0);
		this.base = base;
        float var1 = 0.0F;
        byte var2 = 15;
        spiderNeck = new ModelRenderer(base.texWidth, base.texHeight, 0, 0);
        spiderNeck.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, var1);
        spiderNeck.setPos(0.0F, (float)var2, 2.0F);
        this.addChild(spiderNeck);
        
        spiderBody = new ModelRenderer(base.texWidth, base.texHeight, 0, 12);
        spiderBody.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, var1);
        spiderBody.setPos(0.0F, (float)var2, 11.0F);
        this.addChild(spiderBody);
        
        this.spiderLeg1 = new ModelRenderer(base.texWidth, base.texHeight, 18, 0);
        this.spiderLeg1.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg1.setPos(-4.0F, (float)var2, 4.0F);
        this.addChild(spiderLeg1);
        
        this.spiderLeg2 = new ModelRenderer(base.texWidth, base.texHeight, 18, 0);
        this.spiderLeg2.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg2.setPos(4.0F, (float)var2, 4.0F);
        this.addChild(spiderLeg2);
        
        this.spiderLeg3 = new ModelRenderer(base.texWidth, base.texHeight, 18, 0);
        this.spiderLeg3.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg3.setPos(-4.0F, (float)var2, 3.0F);
        this.addChild(spiderLeg3);
        
        this.spiderLeg4 = new ModelRenderer(base.texWidth, base.texHeight, 18, 0);
        this.spiderLeg4.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg4.setPos(4.0F, (float)var2, 3.0F);
        this.addChild(spiderLeg4);
        
        this.spiderLeg5 = new ModelRenderer(base.texWidth, base.texHeight, 18, 0);
        this.spiderLeg5.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg5.setPos(-4.0F, (float)var2, 2.0F);
        this.addChild(spiderLeg5);
        
        this.spiderLeg6 = new ModelRenderer(base.texWidth, base.texHeight, 18, 0);
        this.spiderLeg6.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg6.setPos(4.0F, (float)var2, 2.0F);
        this.addChild(spiderLeg6);
        
        this.spiderLeg7 = new ModelRenderer(base.texWidth, base.texHeight, 18, 0);
        this.spiderLeg7.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg7.setPos(-4.0F, (float)var2, 1.0F);
        this.addChild(spiderLeg7);
        
        this.spiderLeg8 = new ModelRenderer(base.texWidth, base.texHeight, 18, 0);
        this.spiderLeg8.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg8.setPos(4.0F, (float)var2, 1.0F);
        this.addChild(spiderLeg8);
	}

	public void setupAnim(ModelData data, float par1, float limbSwingAmount, float par3, float par4, float par5, float par6, Entity entity){
		this.xRot = 0;
    	this.y = 0;
    	this.z = 0;
		spiderBody.y = 15;
		spiderBody.z = 11;
		spiderNeck.xRot = 0;
		
        float var8 = ((float)Math.PI / 4F);
        this.spiderLeg1.zRot = -var8;
        this.spiderLeg2.zRot = var8;
        this.spiderLeg3.zRot = -var8 * 0.74F;
        this.spiderLeg4.zRot = var8 * 0.74F;
        this.spiderLeg5.zRot = -var8 * 0.74F;
        this.spiderLeg6.zRot = var8 * 0.74F;
        this.spiderLeg7.zRot = -var8;
        this.spiderLeg8.zRot = var8;
        float var9 = -0.0F;
        float var10 = 0.3926991F;
        this.spiderLeg1.yRot = var10 * 2.0F + var9;
        this.spiderLeg2.yRot = -var10 * 2.0F - var9;
        this.spiderLeg3.yRot = var10 * 1.0F + var9;
        this.spiderLeg4.yRot = -var10 * 1.0F - var9;
        this.spiderLeg5.yRot = -var10 * 1.0F + var9;
        this.spiderLeg6.yRot = var10 * 1.0F - var9;
        this.spiderLeg7.yRot = -var10 * 2.0F + var9;
        this.spiderLeg8.yRot = var10 * 2.0F - var9;
        float var11 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
        float var12 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * limbSwingAmount;
        float var13 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
        float var14 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + ((float)Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
        float var15 = Math.abs(MathHelper.sin(par1 * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
        float var16 = Math.abs(MathHelper.sin(par1 * 0.6662F + (float)Math.PI) * 0.4F) * limbSwingAmount;
        float var17 = Math.abs(MathHelper.sin(par1 * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
        float var18 = Math.abs(MathHelper.sin(par1 * 0.6662F + ((float)Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
        this.spiderLeg1.yRot += var11;
        this.spiderLeg2.yRot += -var11;
        this.spiderLeg3.yRot += var12;
        this.spiderLeg4.yRot += -var12;
        this.spiderLeg5.yRot += var13;
        this.spiderLeg6.yRot += -var13;
        this.spiderLeg7.yRot += var14;
        this.spiderLeg8.yRot += -var14;
        this.spiderLeg1.zRot += var15;
        this.spiderLeg2.zRot += -var15;
        this.spiderLeg3.zRot += var16;
        this.spiderLeg4.zRot += -var16;
        this.spiderLeg5.zRot += var17;
        this.spiderLeg6.zRot += -var17;
        this.spiderLeg7.zRot += var18;
        this.spiderLeg8.zRot += -var18;

        
        if(base.crouching){
        	z = 5;
        	y = -1;
			spiderBody.y = 16;
			spiderBody.z = 10;
			spiderNeck.xRot = (float) (Math.PI / -8);
        }
        if(((EntityNPCInterface)entity).isSleeping() || ((EntityNPCInterface)entity).currentAnimation == AnimationType.CRAWL){
        	y = 12 * data.getPartConfig(EnumParts.LEG_LEFT).scaleY;
        	z = 15 * data.getPartConfig(EnumParts.LEG_LEFT).scaleY;

			this.xRot = (float) (Math.PI / -2);
        }
    }

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}
}
