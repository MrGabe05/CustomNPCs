package noppes.npcs.client.model.part.legs;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelPlaneNpcsRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelNagaLegs extends ModelRenderer{

    private final ModelRenderer nagaPart1;
    private final ModelRenderer nagaPart2;
    private final ModelRenderer nagaPart3;
    private final ModelRenderer nagaPart4;
    private final ModelRenderer nagaPart5;

    public boolean riding = false;
    public boolean isSneaking = false;
    public boolean isSleeping = false;
    public boolean isCrawling = false;
    
	public ModelNagaLegs(Model base) {
		super(base.texWidth, base.texHeight, 0, 0);
		
		nagaPart1 = new ModelRenderer(base.texWidth, base.texHeight,0,0);
		
		ModelRenderer legPart = new ModelRenderer(base.texWidth, base.texHeight,0,16);
		legPart.addBox(0, -2, -2, 4, 4, 4);
		legPart.setPos(-4, 0, 0);
		nagaPart1.addChild(legPart);
		legPart = new ModelRenderer(base.texWidth, base.texHeight,0,16);
		legPart.mirror = true;
		legPart.addBox(0, -2, -2, 4, 4, 4);
		nagaPart1.addChild(legPart);

		nagaPart2 = new ModelRenderer(base.texWidth, base.texHeight,0,0);
		nagaPart2.children.clear();
		nagaPart2.children.addAll(nagaPart1.children);

		nagaPart3 = new ModelRenderer(base.texWidth, base.texHeight,0,0);
		
		ModelPlaneNpcsRenderer plane = new ModelPlaneNpcsRenderer(base, 4, 24);
		plane.addBackPlane(0,-2, 0, 4, 4);
		plane.setPos(-4, 0, 0);
		nagaPart3.addChild(plane);
		plane = new ModelPlaneNpcsRenderer(base, 4, 24);
		plane.mirror = true;
		plane.addBackPlane(0,-2, 0, 4, 4);
		nagaPart3.addChild(plane);
		
		plane = new ModelPlaneNpcsRenderer(base, 8, 24);
		plane.addBackPlane(0,-2, 6, 4, 4);
		plane.setPos(-4, 0, 0);
		nagaPart3.addChild(plane);
		plane = new ModelPlaneNpcsRenderer(base, 8, 24);
		plane.mirror = true;
		plane.addBackPlane(0,-2, 6, 4, 4);
		nagaPart3.addChild(plane);

		plane = new ModelPlaneNpcsRenderer(base, 4, 26);
		plane.addTopPlane(0,-2, -6, 4, 6);
		plane.setPos(-4, 0, 0);
		plane.xRot = (float) (Math.PI);
		nagaPart3.addChild(plane);
		plane = new ModelPlaneNpcsRenderer(base, 4, 26);
		plane.mirror = true;
		plane.addTopPlane(0,-2, -6, 4, 6);
		plane.xRot = (float) (Math.PI);
		nagaPart3.addChild(plane);

		plane = new ModelPlaneNpcsRenderer(base, 8, 26);
		plane.addTopPlane(0,-2, 0, 4, 6);
		plane.setPos(-4, 0, 0);
		nagaPart3.addChild(plane);
		plane = new ModelPlaneNpcsRenderer(base, 8, 26);
		plane.mirror = true;
		plane.addTopPlane(0,-2, 0, 4, 6);
		nagaPart3.addChild(plane);

		plane = new ModelPlaneNpcsRenderer(base, 0, 26);
		plane.xRot = (float) (Math.PI / 2);
		plane.addSidePlane(0,0, -2, 6, 4);
		plane.setPos(-4, 0, 0);
		nagaPart3.addChild(plane);
		plane = new ModelPlaneNpcsRenderer(base, 0, 26);
		plane.xRot = (float) (Math.PI / 2);
		plane.addSidePlane(4,0, -2, 6, 4);
		nagaPart3.addChild(plane);

		nagaPart4 = new ModelRenderer(base.texWidth, base.texHeight,0,0);
		nagaPart4.children.clear();
		nagaPart4.children.addAll(nagaPart3.children);
		
		nagaPart5 = new ModelRenderer(base.texWidth, base.texHeight,0,0);
		
		legPart = new ModelRenderer(base.texWidth, base.texHeight,56,20);
		legPart.addBox(0, 0, -2, 2, 5, 2);
		legPart.setPos(-2, 0, 0);
		legPart.xRot = (float) (Math.PI/2);
		nagaPart5.addChild(legPart);
		legPart = new ModelRenderer(base.texWidth, base.texHeight,56,20);
		legPart.mirror = true;
		legPart.addBox(0, 0, -2, 2, 5, 2);
		legPart.xRot = (float) (Math.PI/2);
		nagaPart5.addChild(legPart);

		this.addChild(nagaPart1);
		this.addChild(nagaPart2);
		this.addChild(nagaPart3);
		this.addChild(nagaPart4);
		this.addChild(nagaPart5);

        nagaPart1.setPos(0F, 14.0F, 0.0F);
        nagaPart2.setPos(0, 18.0F, 0.6F);
        nagaPart3.setPos(0F, 22.0F, -0.3F);
        nagaPart4.setPos(0F, 22.0F, 5F);
        nagaPart5.setPos(0F, 22.0F, 10F);
	}

    public void setupAnim(float par1, float limbSwingAmount, float par3, float par4, float par5, float par6, Entity entity)
    {
		this.nagaPart1.yRot = MathHelper.cos(par1 * 0.6662F) * 0.26F * limbSwingAmount;
		this.nagaPart2.yRot = MathHelper.cos(par1 * 0.6662F) * 0.5F * limbSwingAmount;
		this.nagaPart3.yRot = MathHelper.cos(par1 * 0.6662F) * 0.26F * limbSwingAmount;
		this.nagaPart4.yRot = -MathHelper.cos(par1 * 0.6662F) * 0.16F * limbSwingAmount;
		this.nagaPart5.yRot = -MathHelper.cos(par1 * 0.6662F) * 0.3F * limbSwingAmount;

		nagaPart1.setPos(0F, 14.0F, 0.0F);
		nagaPart2.setPos(0, 18.0F, 0.6F);
		nagaPart3.setPos(0F, 22.0F, -0.3F);
		nagaPart4.setPos(0F, 22.0F, 5F);
		nagaPart5.setPos(0F, 22.0F, 10F);

		nagaPart1.xRot = 0;
		nagaPart2.xRot = 0;
		nagaPart3.xRot = 0;
		nagaPart4.xRot = 0;
		nagaPart5.xRot = 0;
		
		if(isSleeping || isCrawling){
			nagaPart3.xRot = (float) -(Math.PI/2);
			nagaPart4.xRot = (float) -(Math.PI/2);
			nagaPart5.xRot = (float) -(Math.PI/2);

			nagaPart3.y -= 2;
        	nagaPart3.z = 0.9f;

        	nagaPart4.y += 4;
        	nagaPart4.z = 0.9f;
        	
        	nagaPart5.y += 7;
        	nagaPart5.z = 2.9f;
		}
		if(this.riding){
			nagaPart1.y-= 1;
			nagaPart1.xRot = (float) -(Math.PI/16f);
			nagaPart1.z = -1;
        	
			nagaPart2.y-= 4;
			nagaPart2.z = -1;
			
			nagaPart3.y-= 9;
        	nagaPart3.z -= 1;
        	nagaPart4.y-= 13;
        	nagaPart4.z -= 1;
        	nagaPart5.y-= 9;
        	nagaPart5.z -= 1;
        	if (this.isSneaking){
        		nagaPart1.z += 5;
        		nagaPart3.z += 5;
        		nagaPart4.z += 5;
        		nagaPart5.z += 4;
            	nagaPart1.y--;
            	nagaPart2.y--;
            	nagaPart3.y--;
            	nagaPart4.y--;
            	nagaPart5.y--;
        	}
		}
		else if (this.isSneaking){
			nagaPart1.y--;
			nagaPart2.y--;
			nagaPart3.y--;
			nagaPart4.y--;
			nagaPart5.y--;

        	nagaPart1.z = 5;
        	nagaPart2.z = 3;
        }
    }

	@Override
	public void render(MatrixStack mStack, IVertexBuilder iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if(!visible)
			return;

		nagaPart1.render(mStack, iVertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		nagaPart3.render(mStack, iVertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if(!this.riding)
			nagaPart2.render(mStack, iVertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		mStack.pushPose();
		mStack.scale(0.74f, 0.7f,0.85f);
		mStack.translate(nagaPart3.yRot, 0.66f, 0.06F);
		nagaPart4.render(mStack, iVertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		mStack.popPose();

		mStack.pushPose();
		mStack.translate(nagaPart3.yRot + nagaPart4.yRot, 0, 0);
		nagaPart5.render(mStack, iVertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		mStack.popPose();
	}
}
