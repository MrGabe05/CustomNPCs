package noppes.npcs.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelWrapper extends EntityModel {
	public Model wrapped;
	public ResourceLocation texture;
	public Model modelOld;
	
	@Override
	public void renderToBuffer(MatrixStack matrix, IVertexBuilder builder, int lightmapUV, int overlay, float r, float g, float b, float a) {
    	Minecraft.getInstance().getTextureManager().bind(texture);
		wrapped.renderToBuffer(matrix, builder, lightmapUV, overlay, r, g, b, a);
    }

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float age, float netHeadYaw, float headPitch) {

	}
}
