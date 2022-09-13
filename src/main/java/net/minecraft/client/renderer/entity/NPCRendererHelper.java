package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.model.ModelWrapper;

public class NPCRendererHelper {

	private final static ModelWrapper wrapper = new ModelWrapper();
	
	public static String getTexture(EntityRenderer render, Entity entity){
		try{
			ResourceLocation location = render.getTextureLocation(entity);
			if(location != null){
				return location.toString();
			}
		}
		catch(Throwable ignored){

		}
		return "minecraft:missingno";
	}

	public static void scale(LivingEntity entity, float f, MatrixStack mStack, LivingRenderer render) {
		render.scale(entity, mStack, f);
	}

	public static void renderModel(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor,
								   LivingRenderer render, Model main, ResourceLocation resource) {
		wrapper.modelOld = render.model;
		if(!(main instanceof ModelWrapper)){
			wrapper.wrapped = main;
			wrapper.texture = resource;
			render.model = wrapper;
		}
	}

	public static float getBob(LivingEntity entity,
			float limbSwingAmount, LivingRenderer renderEntity) {
		return renderEntity.getBob(entity, limbSwingAmount);
	}

	public static void drawLayers(LivingEntity entity, float p_177093_2_,
								  float p_177093_3_, float p_177093_4_, float p_177093_5_,
								  float p_177093_6_, float p_177093_7_, float p_177093_8_, LivingRenderer renderEntity) {
		//renderEntity.renderLayers(entity, p_177093_2_, p_177093_3_, p_177093_4_, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_);
	}
}
