package noppes.npcs.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.ModelPartData;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.client.model.part.legs.*;
import noppes.npcs.client.model.part.tails.*;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;

public class LayerLegs extends LayerInterface implements LayerPreRender{
	private ModelSpiderLegs spiderLegs;
	private ModelHorseLegs horseLegs;
	private ModelNagaLegs naga;
	private ModelDigitigradeLegs digitigrade;
	private ModelMermaidLegs mermaid;

	private ModelRenderer tail;
	private ModelCanineTail fox; 
	private ModelRenderer dragon;
	private ModelRenderer squirrel;
	private ModelRenderer horse;
	private ModelRenderer fin;
	private ModelRenderer rodent;
	private ModelRenderer feathers;
    
	public LayerLegs(LivingRenderer render) {
		super(render);
		createParts();
	}
	
	private void createParts(){
		spiderLegs = new ModelSpiderLegs(base);
		horseLegs = new ModelHorseLegs(base);
		naga = new ModelNagaLegs(base);
		mermaid = new ModelMermaidLegs(base);
        digitigrade = new ModelDigitigradeLegs(base);
        fox = new ModelCanineTail(base); 

		tail = new ModelRenderer(base.texWidth, base.texHeight, 56, 21);
		tail.addBox(-1F, 0F, 0F, 2, 9, 2);
		tail.setPos(0F, 0, 1F);
		setRotation(tail, 0.8714253F, 0F, 0F);
		
		horse = new ModelRenderer(base.texWidth, base.texHeight, 0, 0);
		horse.setTexSize(32, 32);
		horse.setPos(0, -1, 1);

		ModelRenderer tailBase = new ModelRenderer(base.texWidth, base.texHeight, 0, 26);
        tailBase.setTexSize(32, 32);
        tailBase.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3);
        setRotation(tailBase, -1.134464F, 0.0F, 0.0F);
        horse.addChild(tailBase);
        ModelRenderer tailMiddle = new ModelRenderer(base.texWidth, base.texHeight, 0, 13);
        tailMiddle.setTexSize(32, 32);
        tailMiddle.addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7);
        setRotation(tailMiddle, -1.134464F, 0.0F, 0.0F);
        horse.addChild(tailMiddle);
        ModelRenderer tailTip = new ModelRenderer(base.texWidth, base.texHeight, 0, 0);
        tailTip.setTexSize(32, 32);
        tailTip.addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7);
        setRotation(tailTip, -1.40215F, 0.0F, 0.0F);
        horse.addChild(tailTip);
        horse.xRot = 0.5f;

		dragon = new ModelDragonTail(base);
		squirrel = new ModelSquirrelTail(base);
		fin = new ModelTailFin(base);
		rodent = new ModelRodentTail(base);
		feathers = new ModelFeatherTail(base);
	}

	@Override
	public void render(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
		renderLegs(mStack, typeBuffer, lightmapUV, partialTicks);
		renderTails(mStack, typeBuffer, lightmapUV, partialTicks);
	}

	private void renderTails(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV, float partialTicks){
		ModelPartData data = playerdata.getPartData(EnumParts.TAIL);
		if(data == null)
			return;
		mStack.pushPose();
    	ModelPartConfig config = playerdata.getPartConfig(EnumParts.LEG_LEFT);
		mStack.translate(config.transX, config.transY + y * 0.064 , config.transZ + z * 0.064);
		mStack.translate(0, 0, (config.scaleZ - 1) * 5 * partialTicks );
		mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
		preRender(data);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));

		if(data.type == 0){
			if(data.pattern == 1){
				tail.x = -0.5f;
				tail.yRot -= 0.2;
				tail.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
				tail.x += 1;
				tail.yRot += 0.4;
				tail.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
				tail.x = 0;
			}
			else
				tail.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 1){
			dragon.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 2){
			horse.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 3){
			squirrel.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 4){
			fin.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 5){
			rodent.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if (data.type == 6) {
			feathers.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		} 
		else if (data.type == 7) {
			fox.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		mStack.popPose();
	}
	
	private void renderLegs(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV, float partialTicks){
		ModelPartData data = playerdata.getPartData(EnumParts.LEGS);
		if(data.type <= 0)
			return;
		mStack.pushPose();
    	ModelPartConfig config = playerdata.getPartConfig(EnumParts.LEG_LEFT);
		preRender(data);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));
		if(data.type == 1){
			mStack.translate(0, config.transY * 2, config.transZ * partialTicks + 0.04f);
			mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
			naga.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 2){
			mStack.translate(0, config.transY* 1.76f - 0.1f * config.scaleY, config.transZ * partialTicks);
			mStack.scale(1.06f, 1.06f, 1.06f);
			mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
			spiderLegs.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 3){
	    	if(config.scaleY >= 1)
				mStack.translate(0, config.transY * 1.76f, config.transZ * partialTicks);
	    	else
				mStack.translate(0, config.transY * 1.86f, config.transZ * partialTicks );
			mStack.scale(0.79f, 0.9f - config.scaleY / 10, 0.79f);
			mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
			horseLegs.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 4){
			mStack.translate(0, config.transY * 1.86f, config.transZ * partialTicks );
			mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
			mermaid.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 5){
			mStack.translate(0, config.transY * 1.86f, config.transZ * partialTicks );
			mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
			digitigrade.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		mStack.popPose();
	}

	@Override
	public void rotate(MatrixStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		rotateLegs(limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
		rotateTail(limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
	}
	public void rotateLegs(float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		ModelPartData part = playerdata.getPartData(EnumParts.LEGS);
		if(part.type == 2){
			spiderLegs.setupAnim(playerdata, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, npc);
		}
		else if(part.type == 3){
			horseLegs.setupAnim(playerdata, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, npc);
		}
		else if(part.type == 1){
			naga.riding = base.riding;
			naga.isSleeping = npc.isSleeping();
			naga.isCrawling = npc.currentAnimation == AnimationType.CRAWL;
			naga.isSneaking = base.crouching;
			naga.setupAnim(limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, npc);
		}
		else if(part.type == 4){
			mermaid.setupAnim(limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, npc);
		}
		else if(part.type == 5){
			digitigrade.setupAnim(limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, npc);
		}
		
	}
	float z;
	float y;
	public void rotateTail(float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPartData part = playerdata.getPartData(EnumParts.LEGS);
        ModelPartData partTail = playerdata.getPartData(EnumParts.TAIL);
        ModelPartConfig config = playerdata.getPartConfig(EnumParts.LEG_LEFT);
		float yRot = MathHelper.cos(limbSwing * 0.6662F) * 0.2f * limbSwingAmount;
		float xRot = MathHelper.sin(partialTicks * 0.067F) * 0.05F;
		z = 0;
		y = 11;
				
        if(part.type == 2){
    		y = 12 + (config.scaleY-1) * 3;
        	z = 15 + (config.scaleZ-1) * 10;
	        if(npc.isSleeping() || npc.currentAnimation == AnimationType.CRAWL){
	        	y = 12 + 16 * config.scaleZ;
	        	z = 1f * config.scaleY;

				xRot = (float) (Math.PI / -4);
	        }
        }
        else if(part.type == 3){
    		y = 10;
        	z = 16 + (config.scaleZ-1) * 12;
        }
        else{
        	z = (1 - config.scaleZ) * 1;
        }
        if(partTail != null){ 
        	if(partTail.type == 2)
        		xRot += 0.5;
        	if(partTail.type == 0)
        		xRot += 0.87F;
            if(partTail.type == 7){
            	fox.setupAnim(limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, npc);
			}
        }
        
        z += base.rightLeg.z + 0.5f;
        fox.xRot = tail.xRot = feathers.xRot = dragon.xRot = squirrel.xRot = horse.xRot = fin.xRot = rodent.xRot = xRot;
        fox.yRot = tail.yRot = feathers.yRot = dragon.yRot = squirrel.yRot = horse.yRot = fin.yRot = rodent.yRot = yRot;
	}
	

	@Override
	public void preRender(EntityCustomNpc player) {
		this.npc = player;
		playerdata = player.modelData;
		ModelPartData data = playerdata.getPartData(EnumParts.LEGS);
		base.leftLeg.visible = base.rightLeg.visible = data != null && data.type == 0;
	}

}
