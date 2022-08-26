package noppes.npcs.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import noppes.npcs.ModelPartData;
import noppes.npcs.client.model.Model2DRenderer;
import noppes.npcs.client.model.part.head.ModelDuckBeak;
import noppes.npcs.client.model.part.horns.ModelAntennasBack;
import noppes.npcs.client.model.part.horns.ModelAntennasFront;
import noppes.npcs.client.model.part.horns.ModelAntlerHorns;
import noppes.npcs.client.model.part.horns.ModelBullHorns;
import noppes.npcs.constants.EnumParts;

public class LayerHead extends LayerInterface{

	private ModelRenderer small;
	private ModelRenderer medium;
	private ModelRenderer large;
	private ModelRenderer bunnySnout;
	private ModelRenderer beak;

	private Model2DRenderer beard;
	private Model2DRenderer hair;
	private Model2DRenderer mohawk;

	private ModelRenderer bull;
	private ModelRenderer antlers;
	private ModelRenderer antennasBack;
	private ModelRenderer antennasFront;

	private ModelRenderer ears;
	private ModelRenderer bunnyEars;
	
	public LayerHead(LivingRenderer render) {
		super(render);
		createParts();
	}

	private void createParts(){
		small = new ModelRenderer(base.texWidth, base.texHeight, 24, 0);
		small.addBox(0F, 0F, 0F, 4, 3, 1);
		small.setPos(-2F, -3F, -5F);

		medium = new ModelRenderer(base.texWidth, base.texHeight, 24, 0);
		medium.addBox(0F, 0F, 0F, 4, 3, 2);
		medium.setPos(-2F, -3F, -6F);

		large = new ModelRenderer(base.texWidth, base.texHeight, 24, 0);
		large.addBox(0F, 0F, 0F, 4, 3, 3);
		large.setPos(-2F, -3F, -7F);
		
		bunnySnout = new ModelRenderer(base.texWidth, base.texHeight, 24, 0);
		bunnySnout.addBox(1F, 1F, 0F, 4, 2, 1);
		bunnySnout.setPos(-3F, -4F, -5F);
		
		ModelRenderer tooth = new ModelRenderer(base.texWidth, base.texHeight, 24, 3);
		tooth.addBox(2F, 3f, 0F, 2, 1, 1);
		tooth.setPos(0F, 0F, 0F);
		bunnySnout.addChild(tooth);

		beak = new ModelDuckBeak(base);

		beard = new Model2DRenderer(base, 56, 20, 8, 12);
		beard.setRotationOffset(-3.99f, 11.8f, -4);
		beard.setScale(0.74f);

		hair = new Model2DRenderer(base, 56, 20, 8, 12);
		hair.setRotationOffset(-3.99f, 11.8f, 3);
		hair.setScale(0.75f);

		mohawk = new Model2DRenderer(base, 0, 0, 64 , 64);
		mohawk.setRotationOffset(-9F, 0.1f, -0.5F);
        setRotation(mohawk, 0, (float)(Math.PI/2f), 0);
        mohawk.setScale(0.825f);

		bull = new ModelBullHorns(base);
		antlers = new ModelAntlerHorns(base);
		antennasBack = new ModelAntennasBack(base);
		antennasFront = new ModelAntennasFront(base);

		ears = new ModelRenderer(base.texWidth, base.texHeight, 0, 0);
		Model2DRenderer right = new Model2DRenderer(base, 56, 0, 8, 4);
		right.setPos(-7.44f, -7.3f, -0.0f);
		right.setScale(0.234f, 0.234f);
		right.setThickness(1.16f);
		ears.addChild(right);

		Model2DRenderer left = new Model2DRenderer(base, 56, 0, 8, 4);
		left.setPos(7.44f, -7.3f, 1.15f);
		left.setScale(0.234f, 0.234f);
        setRotation(left, 0, (float)(Math.PI), 0);
        left.setThickness(1.16f);
        ears.addChild(left);

		Model2DRenderer right2 = new Model2DRenderer(base, 56, 4, 8, 4);
		right2.setPos(-7.44f, -7.3f, 1.14f);
		right2.setScale(0.234f, 0.234f);
		right2.setThickness(1.16f);
		ears.addChild(right2);

		Model2DRenderer left2 = new Model2DRenderer(base, 56, 4, 8, 4);
		left2.setPos(7.44f, -7.3f, 2.31f);
		left2.setScale(0.234f, 0.234f);
        setRotation(left2, 0, (float)(Math.PI), 0);
        left2.setThickness(1.16f);
        ears.addChild(left2);
		
		bunnyEars = new ModelRenderer(base.texWidth, base.texHeight, 0, 0);		
		ModelRenderer earleft = new ModelRenderer(base.texWidth, base.texHeight, 56, 0);
		earleft.mirror = true;
		earleft.addBox(-1.466667F, -4F, 0F, 3, 7, 1);
		earleft.setPos(2.533333F, -11F, 0F);
		bunnyEars.addChild(earleft);

		ModelRenderer earright = new ModelRenderer(base.texWidth, base.texHeight, 56, 0);
		earright.addBox(-1.5F, -4F, 0F, 3, 7, 1);
		earright.setPos(-2.466667F, -11F, 0F);
		bunnyEars.addChild(earright);
	}

	@Override
	public void render(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
		base.head.translateAndRotate(mStack);
		renderSnout(mStack, typeBuffer, lightmapUV);
		renderBeard(mStack, typeBuffer, lightmapUV);
		renderHair(mStack, typeBuffer, lightmapUV);
		renderMohawk(mStack, typeBuffer, lightmapUV);
		renderHorns(mStack, typeBuffer, lightmapUV);
		renderEars(mStack, typeBuffer, lightmapUV);
	}
	
	private void renderSnout(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV){
		ModelPartData data = playerdata.getPartData(EnumParts.SNOUT);
		if(data == null)
			return;
		preRender(data);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));
		if(data.type == 0){
			small.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 1){
			medium.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 2){
			large.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 3){
			bunnySnout.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 4){
			beak.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
	}

	private void renderBeard(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV){
		ModelPartData data = playerdata.getPartData(EnumParts.BEARD);
		if(data == null)
			return;
		preRender(data);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));
		beard.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
	}

	private void renderHair(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV){
		ModelPartData data = playerdata.getPartData(EnumParts.HAIR);
		if(data == null)
			return;
		preRender(data);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));
		hair.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
	}
	
	private void renderMohawk(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV){
		ModelPartData data = playerdata.getPartData(EnumParts.MOHAWK);
		if(data == null)
			return;
		preRender(data);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));
		mohawk.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
	}
	private void renderHorns(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV){
		ModelPartData data = playerdata.getPartData(EnumParts.HORNS);
		if(data == null)
			return;
		preRender(data);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));
		if(data.type == 0){
			bull.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 1){
			antlers.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 2 && data.pattern == 0){
			antennasBack.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 2 && data.pattern == 1){
			antennasFront.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
	}
	
	private void renderEars(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV){
		ModelPartData data = playerdata.getPartData(EnumParts.EARS);
		if(data == null)
			return;
		preRender(data);
		IVertexBuilder ivertex = typeBuffer.getBuffer(getRenderType(data));
		if(data.type == 0){
			ears.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
		else if(data.type == 1){
			bunnyEars.render(mStack, ivertex, lightmapUV, OverlayTexture.NO_OVERLAY, red(), green(), blue(), alpha());
		}
	}

	@Override
	public void rotate(MatrixStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		ModelRenderer head = base.head;
		if(head.xRot < 0){
			beard.xRot = 0;
			hair.xRot = -head.xRot * 1.2f;
			if(head.xRot > -1){
				hair.y = -head.xRot * 1.5f;
				hair.z = -head.xRot * 1.5f;
			}
		}
		else{
			hair.xRot = 0;
			hair.y = 0;
			hair.z = 0;
			beard.xRot = -head.xRot;
		}
		
	}
}
