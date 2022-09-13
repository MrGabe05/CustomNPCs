package noppes.npcs.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.NPCRendererHelper;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.BipedModel.ArmPose;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.client.layer.*;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.ArmorLayerMixin;

import java.util.List;

public class RenderCustomNpc<T extends EntityCustomNpc, M extends BipedModel<T>> extends RenderNPCInterface<T, M> {
	private float partialTicks;
	private LivingEntity entity;
	private EntityNPCInterface npc;
	private LivingRenderer renderEntity;
	public M npcmodel;
	public Model otherModel;
	public ArmorLayerMixin armorLayer;
	public final List<LayerRenderer<T, M>> npclayers = Lists.newArrayList();

	private final LayerRenderer renderLayer = new LayerRenderer(null){

		@Override
		public void render(MatrixStack mStack, IRenderTypeBuffer typeBuffer, int lightmapUV, Entity p_225628_4_, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
			for(Object layer : renderEntity.layers){
				((LayerRenderer)layer).render(mStack, typeBuffer, lightmapUV, entity, limbSwing, limbSwingAmount, partialTicks, age, netHeadYaw, headPitch);
			}
		}
	};

	private final BipedModel renderModel = new BipedModel(0) {
		@Override
		public void renderToBuffer(MatrixStack mStack, IVertexBuilder iVertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
			int color = npc.display.getTint();
			if(color < 0xFFFFFF){
				red = (color >> 16 & 255) / 255f;
				green = (color >> 8  & 255) / 255f;
				blue = (color & 255) / 255f;
			}
			otherModel.renderToBuffer(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
		}

		@Override
		public void setupAnim(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			if(otherModel instanceof EntityModel) {
				EntityModel em = (EntityModel) otherModel;
				em.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			}
		}

		@Override
		public void prepareMobModel(Entity npc, float animationPos, float animationSpeed, float partialTicks) {
			if (PixelmonHelper.isPixelmon(entity)) {
				Model pixModel = (Model) PixelmonHelper.getModel(entity);
				if (pixModel != null) {
					otherModel = pixModel;
					PixelmonHelper.setupModel(entity, pixModel);
				}
			}

			if(otherModel instanceof BipedModel){
				BipedModel bm = (BipedModel) otherModel;
				bm.swimAmount = ((EntityCustomNpc) npc).getSwimAmount(partialTicks);
				bm.crouching = RenderCustomNpc.this.npcmodel.crouching;
			}

			if(otherModel instanceof EntityModel){
				EntityModel em = (EntityModel) otherModel;
				em.riding = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
				em.young = entity.isBaby();
				em.attackTime = getAttackAnim((T) npc, partialTicks);
				em.prepareMobModel(entity, animationPos, animationSpeed, partialTicks);
			}
		}
	};

	public RenderCustomNpc(EntityRendererManager manager, M model) {
		super(manager, model, 0.5f);
		npcmodel = model;
		addLayer(new LayerEyes(this));
		addLayer(new LayerHeadwear(this));
		addLayer(new LayerHead(this));
		addLayer(new LayerArms(this));
		addLayer(new LayerLegs(this));
		addLayer(new LayerBody(this));
		addLayer(new LayerNpcCloak(this));

		addLayer(new HeldItemLayer(this));
		addLayer(new HeadLayer(this));
		addLayer(new LayerGlow(this));
		BipedArmorLayer armorLayer = new BipedArmorLayer(this, new BipedModel(0.5f), new BipedModel(1f));
		addLayer(armorLayer);
		this.armorLayer = (ArmorLayerMixin) armorLayer;
	}

	@Override
	public Vector3d getRenderOffset(T npc, float partialTicks) {
		float xOffset = 0;
		float yOffset = npc.currentAnimation == AnimationType.NORMAL?npc.ais.bodyOffsetY / 10 - 0.5f:0;
		float zOffset = 0;

		if(npc.isAlive()){
			if(npc.isSleeping()){
				xOffset = (float) -Math.cos(Math.toRadians(180 - npc.ais.orientation));
				zOffset = (float) -Math.sin(Math.toRadians(npc.ais.orientation));
				yOffset += 0.14f;
			}
			else if(npc.currentAnimation == AnimationType.SIT || npc.isPassenger()){
				yOffset -= 0.5f - npc.modelData.getLegsY() * 0.8f;
			}
			else if(npc.isCrouching()){
				yOffset -= 0.125D;
			}
		}
		return new Vector3d(xOffset, yOffset * (npc.display.getSize() / 5f), zOffset);
	}

	@Override
	public void render(T npc, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
		this.npc = npc;
		this.partialTicks = partialTicks;
		Entity prevEntity = entity;
		entity = npc.modelData.getEntity(npc);
		if(prevEntity != null && entity == null){
			model = npcmodel;
			renderEntity = null;
			layers.clear();
			layers.addAll(npclayers);
		}
		if (entity != null) {
			EntityRenderer render = entityRenderDispatcher.getRenderer(entity);
			if(npc.modelData.simpleRender){
				renderEntity = null;
				matrixStack.pushPose();
				render.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
				renderNameTag(npc, StringTextComponent.EMPTY, matrixStack, buffer, packedLight);
				matrixStack.popPose();
				return;
			}
			if (render instanceof LivingRenderer) {
				renderEntity = (LivingRenderer) render;
				otherModel = renderEntity.getModel();
				model = (M) renderModel;
				layers.clear();
				layers.add(renderLayer);
				if(render instanceof RenderCustomNpc){
					for(Object layer : renderEntity.layers){
						if (layer instanceof LayerPreRender) {
							((LayerPreRender) layer).preRender((EntityCustomNpc) entity);
						}
					}
				}
			}
			else {
				renderEntity = null;
				entity = null;
				model = npcmodel;
				layers.clear();
				layers.addAll(npclayers);
			}

		} else {
			List<LayerRenderer<T, M>> list = this.layers;
			for (LayerRenderer<T, M> layer : list) {
				if (layer instanceof LayerPreRender) {
					((LayerPreRender) layer).preRender(npc);
				}
			}
		}

		npcmodel.rightArmPose = getPose(npc, npc.getMainHandItem());
		npcmodel.leftArmPose = getPose(npc, npc.getOffhandItem());
		super.render(npc, entityYaw, partialTicks, matrixStack, buffer, packedLight);
	}


	@Override
	protected RenderType getRenderType(T p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
		ResourceLocation resourcelocation = this.getTextureLocation(p_230496_1_);
		if (p_230496_2_ && model == renderModel) {
			return this.otherModel.renderType(resourcelocation);
		}
		return super.getRenderType(p_230496_1_, p_230496_2_, p_230496_3_, p_230496_4_);
	}

	public ArmPose getPose(T npc, ItemStack item) {
		if (NoppesUtilServer.IsItemStackNull(item))
			return ArmPose.EMPTY;

		if (npc.getUseItemRemainingTicks() > 0) {
			UseAction enumaction = item.getUseAnimation();

			if (enumaction == UseAction.BLOCK) {
				return BipedModel.ArmPose.BLOCK;
			} else if (enumaction == UseAction.BOW) {
				return BipedModel.ArmPose.BOW_AND_ARROW;
			}
		}
		return ArmPose.ITEM;
	}

//	protected void renderModel(MatrixStack mStack, IVertexBuilder iVertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
//		if (renderEntity != null) {
//			boolean flag = !npc.isInvisible();
//			boolean flag1 = !flag && !npc.isInvisibleTo(Minecraft.getInstance().player);
//			if (!flag && !flag1)
//				return;
//
//			if (flag1) {
//				RenderSystem.pushMatrix();
//				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.15F);
//				RenderSystem.depthMask(false);
//				RenderSystem.enableBlend();
//				RenderSystem.blendFunc(770, 771);
//				RenderSystem.alphaFunc(516, 0.003921569F);
//			}
//
//			NPCRendererHelper.renderModel(entity, limbSwingAmount, par3, par4, par5, par6, par7, renderEntity, model, getTextureLocation(npc));

//			if (!npc.display.getOverlayTexture().isEmpty()) {
//				RenderSystem.depthFunc(GL11.GL_LEQUAL);
//				if (npc.textureGlowLocation == null) {
//					npc.textureGlowLocation = new ResourceLocation(npc.display.getOverlayTexture());
//				}
//				float f1 = 1.0F;
//				RenderSystem.enableBlend();
//				RenderSystem.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
//				RenderSystem.disableLighting();
//				if (npc.isInvisible()) {
//					RenderSystem.depthMask(false);
//				} else {
//					RenderSystem.depthMask(true);
//				}
//				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//				RenderSystem.pushMatrix();
//				RenderSystem.scalef(1.001f, 1.001f, 1.001f);
//				NPCRendererHelper.renderModel(entity, limbSwingAmount, par3, par4, par5, par6, par7, renderEntity, model, npc.textureGlowLocation);
//				RenderSystem.popMatrix();
//				RenderSystem.enableLighting();
//				RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
//
//				RenderSystem.depthFunc(GL11.GL_LEQUAL);
//				RenderSystem.disableBlend();
//			}
//
//			if (flag1) {
//				RenderSystem.disableBlend();
//				RenderSystem.alphaFunc(516, 0.1F);
//				RenderSystem.popMatrix();
//				RenderSystem.depthMask(true);
//			}
//		}
//	}
//
//	@Override
//	protected void renderLayers(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
//		if (entity != null && renderEntity != null) {
//			NPCRendererHelper.drawLayers(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn, renderEntity);
//		} else {
//			super.renderLayers(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);
//		}
//	}

	@Override
    protected void scale(T npc, MatrixStack matrixScale, float f){
		if(renderEntity != null){
	    	renderColor(npc);
			int size = npc.display.getSize();
			if(entity instanceof EntityNPCInterface){
				((EntityNPCInterface)entity).display.setSize(5);
			}
			NPCRendererHelper.scale(entity, f, matrixScale, renderEntity);
			npc.display.setSize(size);
			matrixScale.scale(0.2f * npc.display.getSize(), 0.2f * npc.display.getSize(), 0.2f * npc.display.getSize());
		}
		else
			super.scale(npc, matrixScale, f);
    }

	@Override
    protected float getBob(T par1LivingEntity, float limbSwingAmount){
		if(renderEntity != null){
			return NPCRendererHelper.getBob(entity, limbSwingAmount, renderEntity);
		}
        return super.getBob(par1LivingEntity, limbSwingAmount);
    }
}